package com.bitan.village.admin.auth;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

@Service
public class AdminAuthService {
    private static final int HASH_ITERATIONS = 120_000;
    private static final int HASH_LENGTH_BITS = 256;

    private final JdbcTemplate jdbcTemplate;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String initialUsername;
    private final String initialPassword;
    private final long sessionHours;

    public AdminAuthService(
            JdbcTemplate jdbcTemplate,
            @Value("${app.admin.username:admin}") String initialUsername,
            @Value("${app.admin.password:BitanAdmin2026!}") String initialPassword,
            @Value("${app.admin.session-hours:12}") long sessionHours
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.initialUsername = initialUsername;
        this.initialPassword = initialPassword;
        this.sessionHours = sessionHours;
    }

    @PostConstruct
    @Transactional
    public void ensureInitialAdmin() {
        List<AdminUser> existingUsers = jdbcTemplate.query("""
                SELECT id, username, password_hash, password_salt
                FROM admin_user
                WHERE username = ?
                LIMIT 1
                """, (rs, rowNum) -> new AdminUser(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("password_salt")
        ), initialUsername);

        if (!existingUsers.isEmpty()) {
            AdminUser user = existingUsers.get(0);
            if (!passwordMatches(initialPassword, user)) {
                byte[] salt = randomBytes(16);
                jdbcTemplate.update("""
                        UPDATE admin_user
                        SET password_hash = ?, password_salt = ?, active = TRUE
                        WHERE id = ?
                        """, hashPassword(initialPassword, salt), Base64.getEncoder().encodeToString(salt), user.id());
                jdbcTemplate.update("DELETE FROM admin_session WHERE admin_user_id = ?", user.id());
            }
            return;
        }

        byte[] salt = randomBytes(16);
        jdbcTemplate.update("""
                INSERT INTO admin_user (username, password_hash, password_salt, active)
                VALUES (?, ?, ?, TRUE)
                """, initialUsername, hashPassword(initialPassword, salt), Base64.getEncoder().encodeToString(salt));
    }

    @Transactional
    public LoginResult login(String username, String password) {
        List<AdminUser> users = jdbcTemplate.query("""
                SELECT id, username, password_hash, password_salt
                FROM admin_user
                WHERE username = ? AND active = TRUE
                LIMIT 1
                """, (rs, rowNum) -> new AdminUser(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("password_salt")
        ), username.trim());

        if (users.isEmpty() || !passwordMatches(password, users.get(0))) {
            throw new AdminUnauthorizedException("用户名或密码错误");
        }

        jdbcTemplate.update("DELETE FROM admin_session WHERE expires_at <= CURRENT_TIMESTAMP");
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes(32));
        Instant expiresAt = Instant.now().plus(sessionHours, ChronoUnit.HOURS);
        AdminUser user = users.get(0);
        jdbcTemplate.update("""
                INSERT INTO admin_session (admin_user_id, token_hash, expires_at)
                VALUES (?, ?, ?)
                """, user.id(), hashToken(token), java.sql.Timestamp.from(expiresAt));
        return new LoginResult(token, user.username(), expiresAt);
    }

    @Transactional(readOnly = true)
    public AdminPrincipal authenticate(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        List<AdminPrincipal> matches = jdbcTemplate.query("""
                SELECT u.id, u.username
                FROM admin_session s
                JOIN admin_user u ON u.id = s.admin_user_id
                WHERE s.token_hash = ?
                  AND s.expires_at > CURRENT_TIMESTAMP
                  AND u.active = TRUE
                LIMIT 1
                """, (rs, rowNum) -> new AdminPrincipal(
                rs.getLong("id"),
                rs.getString("username")
        ), hashToken(token));

        if (matches.isEmpty()) {
            throw new AdminUnauthorizedException("登录已失效，请重新登录");
        }
        return matches.get(0);
    }

    public void logout(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        jdbcTemplate.update("DELETE FROM admin_session WHERE token_hash = ?", hashToken(token));
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AdminUnauthorizedException("请先登录管理后台");
        }
        String token = authorizationHeader.substring(7).trim();
        if (token.isEmpty()) {
            throw new AdminUnauthorizedException("请先登录管理后台");
        }
        return token;
    }

    private boolean passwordMatches(String password, AdminUser user) {
        byte[] salt = Base64.getDecoder().decode(user.passwordSalt());
        byte[] expected = Base64.getDecoder().decode(user.passwordHash());
        byte[] actual = Base64.getDecoder().decode(hashPassword(password, salt));
        return MessageDigest.isEqual(expected, actual);
    }

    private String hashPassword(String password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, HASH_LENGTH_BITS);
        try {
            return Base64.getEncoder().encodeToString(
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded()
            );
        } catch (Exception exception) {
            throw new IllegalStateException("无法生成管理员密码摘要", exception);
        } finally {
            spec.clearPassword();
        }
    }

    private String hashToken(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("当前运行环境不支持 SHA-256", exception);
        }
    }

    private byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    private record AdminUser(long id, String username, String passwordHash, String passwordSalt) {}

    public record AdminPrincipal(long id, String username) {}

    public record LoginResult(String token, String username, Instant expiresAt) {}
}
