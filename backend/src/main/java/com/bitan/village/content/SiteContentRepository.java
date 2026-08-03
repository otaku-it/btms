package com.bitan.village.content;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SiteContentRepository {
    private final JdbcTemplate jdbcTemplate;

    public SiteContentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SiteContent.Profile findProfile() {
        return jdbcTemplate.queryForObject("""
                SELECT id, name, location, eyebrow, slogan, intro_lead, intro_body,
                       land_title, land_description, map_url, hero_image_url, intro_image_url,
                       baike_title, baike_summary, baike_source_url
                FROM village_profile
                WHERE published = TRUE
                ORDER BY id
                LIMIT 1
                """, (rs, rowNum) -> new SiteContent.Profile(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("location"),
                rs.getString("eyebrow"),
                rs.getString("slogan"),
                rs.getString("intro_lead"),
                rs.getString("intro_body"),
                rs.getString("land_title"),
                rs.getString("land_description"),
                rs.getString("map_url"),
                rs.getString("hero_image_url"),
                rs.getString("intro_image_url"),
                rs.getString("baike_title"),
                rs.getString("baike_summary"),
                rs.getString("baike_source_url")
        ));
    }

    public List<SiteContent.Stat> findStats() {
        return jdbcTemplate.query("""
                SELECT id, display_value, label, sort_order
                FROM site_stat
                WHERE published = TRUE
                ORDER BY sort_order, id
                """, (rs, rowNum) -> new SiteContent.Stat(
                rs.getLong("id"),
                rs.getString("display_value"),
                rs.getString("label"),
                rs.getInt("sort_order")
        ));
    }

    public List<SiteContent.JourneyStop> findJourneyStops() {
        return jdbcTemplate.query("""
                SELECT id, duration, title, description, tag, image_url, image_alt, sort_order
                FROM journey_stop
                WHERE published = TRUE
                ORDER BY sort_order, id
                """, (rs, rowNum) -> new SiteContent.JourneyStop(
                rs.getLong("id"),
                rs.getString("duration"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("tag"),
                rs.getString("image_url"),
                rs.getString("image_alt"),
                rs.getInt("sort_order")
        ));
    }

    public List<SiteContent.Season> findSeasons() {
        return jdbcTemplate.query("""
                SELECT id, code, name, period, title, description, sight, note, sort_order
                FROM season_content
                WHERE published = TRUE
                ORDER BY sort_order, id
                """, (rs, rowNum) -> new SiteContent.Season(
                rs.getLong("id"),
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("period"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("sight"),
                rs.getString("note"),
                rs.getInt("sort_order")
        ));
    }

    public List<SiteContent.GalleryItem> findGalleryItems() {
        return jdbcTemplate.query("""
                SELECT id, title, image_url, image_alt, scope, layout, sort_order
                FROM gallery_item
                WHERE published = TRUE
                ORDER BY sort_order, id
                """, (rs, rowNum) -> new SiteContent.GalleryItem(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("image_url"),
                rs.getString("image_alt"),
                rs.getString("scope"),
                rs.getString("layout"),
                rs.getInt("sort_order")
        ));
    }

    public List<SiteContent.GuideItem> findGuides() {
        return jdbcTemplate.query("""
                SELECT id, title, content, sort_order
                FROM guide_item
                WHERE published = TRUE
                ORDER BY sort_order, id
                """, (rs, rowNum) -> new SiteContent.GuideItem(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getInt("sort_order")
        ));
    }
}
