package com.bitan.village.admin.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {
    private final AdminAuthService authService;

    public AdminAuthController(AdminAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        AdminAuthService.LoginResult result = authService.login(request.username(), request.password());
        return new AuthResponse(result.token(), result.username(), result.expiresAt());
    }

    @GetMapping("/me")
    public MeResponse me(HttpServletRequest request) {
        AdminAuthService.AdminPrincipal principal = (AdminAuthService.AdminPrincipal)
                request.getAttribute(AdminAuthInterceptor.PRINCIPAL_ATTRIBUTE);
        return new MeResponse(principal.username());
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String authorizationHeader) {
        authService.logout(authorizationHeader);
    }

    public record LoginRequest(
            @NotBlank(message = "请输入用户名") @Size(max = 60) String username,
            @NotBlank(message = "请输入密码") @Size(max = 200) String password
    ) {}

    public record AuthResponse(String token, String username, Instant expiresAt) {}

    public record MeResponse(String username) {}
}
