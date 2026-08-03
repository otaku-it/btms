package com.bitan.village.admin.inquiry;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminInquiryRepository {
    private final JdbcTemplate jdbcTemplate;

    public AdminInquiryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AdminInquiry> findAll() {
        return jdbcTemplate.query("""
                SELECT id, name, email, visit_date, party_size, message, status, created_at
                FROM visitor_inquiry
                ORDER BY created_at DESC, id DESC
                """, (rs, rowNum) -> new AdminInquiry(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getDate("visit_date") == null ? null : rs.getDate("visit_date").toLocalDate(),
                rs.getObject("party_size", Integer.class),
                rs.getString("message"),
                rs.getString("status"),
                rs.getTimestamp("created_at").toInstant()
        ));
    }

    public int updateStatus(long id, String status) {
        return jdbcTemplate.update("UPDATE visitor_inquiry SET status = ? WHERE id = ?", status, id);
    }

    public int delete(long id) {
        return jdbcTemplate.update("DELETE FROM visitor_inquiry WHERE id = ?", id);
    }
}
