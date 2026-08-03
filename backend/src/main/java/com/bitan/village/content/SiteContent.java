package com.bitan.village.content;

import java.util.List;

public record SiteContent(
        Profile profile,
        List<Stat> stats,
        List<JourneyStop> journey,
        List<Season> seasons,
        List<GalleryItem> gallery,
        List<GuideItem> guides
) {
    public record Profile(
            long id,
            String name,
            String location,
            String eyebrow,
            String slogan,
            String introLead,
            String introBody,
            String landTitle,
            String landDescription,
            String mapUrl,
            String heroImageUrl,
            String introImageUrl,
            String baikeTitle,
            String baikeSummary,
            String baikeSourceUrl
    ) {}

    public record Stat(long id, String value, String label, int sortOrder) {}

    public record JourneyStop(
            long id,
            String duration,
            String title,
            String description,
            String tag,
            String imageUrl,
            String imageAlt,
            int sortOrder
    ) {}

    public record Season(
            long id,
            String code,
            String name,
            String period,
            String title,
            String description,
            String sight,
            String note,
            int sortOrder
    ) {}

    public record GalleryItem(
            long id,
            String title,
            String imageUrl,
            String imageAlt,
            String scope,
            String layout,
            int sortOrder
    ) {}

    public record GuideItem(long id, String title, String content, int sortOrder) {}
}
