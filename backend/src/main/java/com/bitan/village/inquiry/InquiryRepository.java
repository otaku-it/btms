package com.bitan.village.inquiry;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class InquiryRepository {
    private final JdbcTemplate jdbcTemplate;

    public InquiryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(InquiryRequest request) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO visitor_inquiry (name, email, visit_date, party_size, message, status)
                    VALUES (?, ?, ?, ?, ?, 'NEW')
                    """, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, request.name().trim());
            statement.setString(2, request.email().trim().toLowerCase());
            if (request.visitDate() == null) {
                statement.setNull(3, java.sql.Types.DATE);
            } else {
                statement.setDate(3, Date.valueOf(request.visitDate()));
            }
            if (request.partySize() == null) {
                statement.setNull(4, java.sql.Types.INTEGER);
            } else {
                statement.setInt(4, request.partySize());
            }
            statement.setString(5, request.message().trim());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("留言保存成功但未返回编号");
        }
        return key.longValue();
    }
}
