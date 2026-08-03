package com.bitan.village.admin.content;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AdminContent(
        Profile profile,
        List<Stat> stats,
        List<JourneyStop> journey,
        List<Season> seasons,
        List<GalleryItem> gallery,
        List<GuideItem> guides
) {
    public record Profile(
            Long id,
            @NotBlank @Size(max = 80) String name,
            @NotBlank @Size(max = 160) String location,
            @NotBlank @Size(max = 120) String eyebrow,
            @NotBlank @Size(max = 200) String slogan,
            @NotBlank @Size(max = 3000) String introLead,
            @NotBlank @Size(max = 5000) String introBody,
            @NotBlank @Size(max = 160) String landTitle,
            @NotBlank @Size(max = 5000) String landDescription,
            @NotBlank @Size(max = 500) String mapUrl,
            @NotBlank @Size(max = 300) String heroImageUrl,
            @NotBlank @Size(max = 300) String introImageUrl,
            @NotBlank @Size(max = 160) String baikeTitle,
            @NotBlank @Size(max = 5000) String baikeSummary,
            @NotBlank @Size(max = 500) String baikeSourceUrl,
            boolean published
    ) {}

    public record Stat(
            Long id,
            @NotBlank @Size(max = 40) String value,
            @NotBlank @Size(max = 100) String label,
            @Min(0) @Max(999) int sortOrder,
            boolean published
    ) {}

    public record JourneyStop(
            Long id,
            @NotBlank @Size(max = 80) String duration,
            @NotBlank @Size(max = 120) String title,
            @NotBlank @Size(max = 5000) String description,
            @NotBlank @Size(max = 80) String tag,
            @NotBlank @Size(max = 300) String imageUrl,
            @NotBlank @Size(max = 200) String imageAlt,
            @Min(0) @Max(999) int sortOrder,
            boolean published
    ) {}

    public record Season(
            Long id,
            @NotBlank @Pattern(regexp = "[a-z0-9-]{2,20}") String code,
            @NotBlank @Size(max = 20) String name,
            @NotBlank @Size(max = 80) String period,
            @NotBlank @Size(max = 160) String title,
            @NotBlank @Size(max = 5000) String description,
            @NotBlank @Size(max = 120) String sight,
            @NotBlank @Size(max = 120) String note,
            @Min(0) @Max(999) int sortOrder,
            boolean published
    ) {}

    public record GalleryItem(
            Long id,
            @NotBlank @Size(max = 100) String title,
            @NotBlank @Size(max = 300) String imageUrl,
            @NotBlank @Size(max = 200) String imageAlt,
            @NotBlank @Size(max = 100) String scope,
            @NotBlank @Pattern(regexp = "wide|tall|standard") String layout,
            @Min(0) @Max(999) int sortOrder,
            boolean published
    ) {}

    public record GuideItem(
            Long id,
            @NotBlank @Size(max = 80) String title,
            @NotBlank @Size(max = 5000) String content,
            @Min(0) @Max(999) int sortOrder,
            boolean published
    ) {}
}
