package com.bitan.village.content;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SiteContentService {
    private final SiteContentRepository repository;

    public SiteContentService(SiteContentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public SiteContent getPublishedContent() {
        return new SiteContent(
                repository.findProfile(),
                repository.findStats(),
                repository.findJourneyStops(),
                repository.findSeasons(),
                repository.findGalleryItems(),
                repository.findGuides()
        );
    }
}
