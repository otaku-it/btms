package com.bitan.village.admin.content;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/content")
public class AdminContentController {
    private final AdminContentService service;

    public AdminContentController(AdminContentService service) {
        this.service = service;
    }

    @GetMapping
    public AdminContent getAll() {
        return service.getAll();
    }

    @PutMapping("/profile")
    public AdminContent saveProfile(@Valid @RequestBody AdminContent.Profile profile) {
        return service.saveProfile(profile);
    }

    @PutMapping("/stats")
    public AdminContent saveStats(@Valid @RequestBody List<AdminContent.Stat> items) {
        return service.saveStats(items);
    }

    @PutMapping("/journey")
    public AdminContent saveJourney(@Valid @RequestBody List<AdminContent.JourneyStop> items) {
        return service.saveJourney(items);
    }

    @PutMapping("/seasons")
    public AdminContent saveSeasons(@Valid @RequestBody List<AdminContent.Season> items) {
        return service.saveSeasons(items);
    }

    @PutMapping("/gallery")
    public AdminContent saveGallery(@Valid @RequestBody List<AdminContent.GalleryItem> items) {
        return service.saveGallery(items);
    }

    @PutMapping("/guides")
    public AdminContent saveGuides(@Valid @RequestBody List<AdminContent.GuideItem> items) {
        return service.saveGuides(items);
    }
}
