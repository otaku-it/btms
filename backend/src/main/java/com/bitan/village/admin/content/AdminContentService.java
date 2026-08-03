package com.bitan.village.admin.content;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminContentService {
    private final AdminContentRepository repository;

    public AdminContentService(AdminContentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public AdminContent getAll() {
        return repository.findAll();
    }

    @Transactional
    public AdminContent saveProfile(AdminContent.Profile profile) {
        repository.saveProfile(profile);
        return repository.findAll();
    }

    @Transactional
    public AdminContent saveStats(List<AdminContent.Stat> items) {
        repository.replaceStats(items);
        return repository.findAll();
    }

    @Transactional
    public AdminContent saveJourney(List<AdminContent.JourneyStop> items) {
        repository.replaceJourney(items);
        return repository.findAll();
    }

    @Transactional
    public AdminContent saveSeasons(List<AdminContent.Season> items) {
        repository.replaceSeasons(items);
        return repository.findAll();
    }

    @Transactional
    public AdminContent saveGallery(List<AdminContent.GalleryItem> items) {
        repository.replaceGallery(items);
        return repository.findAll();
    }

    @Transactional
    public AdminContent saveGuides(List<AdminContent.GuideItem> items) {
        repository.replaceGuides(items);
        return repository.findAll();
    }
}
