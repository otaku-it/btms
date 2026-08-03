package com.bitan.village.inquiry;

import java.time.Instant;

public record InquiryResponse(long id, String message, Instant createdAt) {}
