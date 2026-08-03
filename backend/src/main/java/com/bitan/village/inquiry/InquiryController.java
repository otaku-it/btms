package com.bitan.village.inquiry;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {
    private final InquiryRepository repository;

    public InquiryController(InquiryRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<InquiryResponse> create(@Valid @RequestBody InquiryRequest request) {
        long id = repository.save(request);
        InquiryResponse response = new InquiryResponse(id, "留言已收到，我们会妥善保存你的到访意向。", Instant.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
