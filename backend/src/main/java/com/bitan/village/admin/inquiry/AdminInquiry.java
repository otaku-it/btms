package com.bitan.village.admin.inquiry;

import java.time.Instant;
import java.time.LocalDate;

public record AdminInquiry(
        long id,
        String name,
        String email,
        LocalDate visitDate,
        Integer partySize,
        String message,
        String status,
        Instant createdAt
) {}
