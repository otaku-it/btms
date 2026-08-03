package com.bitan.village.admin.inquiry;

import com.bitan.village.shared.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/inquiries")
public class AdminInquiryController {
    private final AdminInquiryRepository repository;

    public AdminInquiryController(AdminInquiryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<AdminInquiry> list() {
        return repository.findAll();
    }

    @PatchMapping("/{id}/status")
    public AdminInquiry updateStatus(@PathVariable long id, @Valid @RequestBody StatusRequest request) {
        if (repository.updateStatus(id, request.status()) == 0) {
            throw new ResourceNotFoundException("没有找到这条留言");
        }
        return repository.findAll().stream()
                .filter(item -> item.id() == id)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("没有找到这条留言"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        if (repository.delete(id) == 0) {
            throw new ResourceNotFoundException("没有找到这条留言");
        }
    }

    public record StatusRequest(
            @Pattern(regexp = "NEW|READ|ARCHIVED", message = "留言状态无效") String status
    ) {}
}
