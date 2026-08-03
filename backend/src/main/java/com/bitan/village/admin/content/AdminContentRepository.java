package com.bitan.village.admin.content;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminContentRepository {
    private final JdbcTemplate jdbcTemplate;

    public AdminContentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AdminContent findAll() {
        return new AdminContent(findProfile(), findStats(), findJourney(), findSeasons(), findGallery(), findGuides());
    }

    public AdminContent.Profile findProfile() {
        return jdbcTemplate.queryForObject("""
                SELECT id, name, location, eyebrow, slogan, intro_lead, intro_body,
                       land_title, land_description, map_url, hero_image_url, intro_image_url,
                       baike_title, baike_summary, baike_source_url, published
                FROM village_profile
                ORDER BY id
                LIMIT 1
                """, (rs, rowNum) -> new AdminContent.Profile(
                rs.getLong("id"), rs.getString("name"), rs.getString("location"),
                rs.getString("eyebrow"), rs.getString("slogan"), rs.getString("intro_lead"),
                rs.getString("intro_body"), rs.getString("land_title"), rs.getString("land_description"),
                rs.getString("map_url"), rs.getString("hero_image_url"), rs.getString("intro_image_url"),
                rs.getString("baike_title"), rs.getString("baike_summary"), rs.getString("baike_source_url"),
                rs.getBoolean("published")
        ));
    }

    public List<AdminContent.Stat> findStats() {
        return jdbcTemplate.query("""
                SELECT id, display_value, label, sort_order, published
                FROM site_stat ORDER BY sort_order, id
                """, (rs, rowNum) -> new AdminContent.Stat(
                rs.getLong("id"), rs.getString("display_value"), rs.getString("label"),
                rs.getInt("sort_order"), rs.getBoolean("published")
        ));
    }

    public List<AdminContent.JourneyStop> findJourney() {
        return jdbcTemplate.query("""
                SELECT id, duration, title, description, tag, image_url, image_alt, sort_order, published
                FROM journey_stop ORDER BY sort_order, id
                """, (rs, rowNum) -> new AdminContent.JourneyStop(
                rs.getLong("id"), rs.getString("duration"), rs.getString("title"),
                rs.getString("description"), rs.getString("tag"), rs.getString("image_url"),
                rs.getString("image_alt"), rs.getInt("sort_order"), rs.getBoolean("published")
        ));
    }

    public List<AdminContent.Season> findSeasons() {
        return jdbcTemplate.query("""
                SELECT id, code, name, period, title, description, sight, note, sort_order, published
                FROM season_content ORDER BY sort_order, id
                """, (rs, rowNum) -> new AdminContent.Season(
                rs.getLong("id"), rs.getString("code"), rs.getString("name"), rs.getString("period"),
                rs.getString("title"), rs.getString("description"), rs.getString("sight"),
                rs.getString("note"), rs.getInt("sort_order"), rs.getBoolean("published")
        ));
    }

    public List<AdminContent.GalleryItem> findGallery() {
        return jdbcTemplate.query("""
                SELECT id, title, image_url, image_alt, scope, layout, sort_order, published
                FROM gallery_item ORDER BY sort_order, id
                """, (rs, rowNum) -> new AdminContent.GalleryItem(
                rs.getLong("id"), rs.getString("title"), rs.getString("image_url"),
                rs.getString("image_alt"), rs.getString("scope"), rs.getString("layout"),
                rs.getInt("sort_order"), rs.getBoolean("published")
        ));
    }

    public List<AdminContent.GuideItem> findGuides() {
        return jdbcTemplate.query("""
                SELECT id, title, content, sort_order, published
                FROM guide_item ORDER BY sort_order, id
                """, (rs, rowNum) -> new AdminContent.GuideItem(
                rs.getLong("id"), rs.getString("title"), rs.getString("content"),
                rs.getInt("sort_order"), rs.getBoolean("published")
        ));
    }

    public void saveProfile(AdminContent.Profile profile) {
        int updated = profile.id() == null ? 0 : jdbcTemplate.update("""
                UPDATE village_profile
                SET name=?, location=?, eyebrow=?, slogan=?, intro_lead=?, intro_body=?,
                    land_title=?, land_description=?, map_url=?, hero_image_url=?, intro_image_url=?,
                    baike_title=?, baike_summary=?, baike_source_url=?, published=TRUE
                WHERE id=?
                """, profile.name(), profile.location(), profile.eyebrow(), profile.slogan(),
                profile.introLead(), profile.introBody(), profile.landTitle(), profile.landDescription(),
                profile.mapUrl(), profile.heroImageUrl(), profile.introImageUrl(), profile.baikeTitle(),
                profile.baikeSummary(), profile.baikeSourceUrl(), profile.id());
        if (updated == 0) {
            jdbcTemplate.update("""
                    INSERT INTO village_profile
                    (name, location, eyebrow, slogan, intro_lead, intro_body, land_title, land_description,
                     map_url, hero_image_url, intro_image_url, baike_title, baike_summary, baike_source_url, published)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE)
                    """, profile.name(), profile.location(), profile.eyebrow(), profile.slogan(),
                    profile.introLead(), profile.introBody(), profile.landTitle(), profile.landDescription(),
                    profile.mapUrl(), profile.heroImageUrl(), profile.introImageUrl(), profile.baikeTitle(),
                    profile.baikeSummary(), profile.baikeSourceUrl());
        }
    }

    public void replaceStats(List<AdminContent.Stat> items) {
        jdbcTemplate.update("DELETE FROM site_stat");
        jdbcTemplate.batchUpdate("""
                INSERT INTO site_stat (display_value, label, sort_order, published) VALUES (?, ?, ?, ?)
                """, items, items.size(), (ps, item) -> {
            ps.setString(1, item.value()); ps.setString(2, item.label());
            ps.setInt(3, item.sortOrder()); ps.setBoolean(4, item.published());
        });
    }

    public void replaceJourney(List<AdminContent.JourneyStop> items) {
        jdbcTemplate.update("DELETE FROM journey_stop");
        jdbcTemplate.batchUpdate("""
                INSERT INTO journey_stop
                (duration, title, description, tag, image_url, image_alt, sort_order, published)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """, items, items.size(), (ps, item) -> {
            ps.setString(1, item.duration()); ps.setString(2, item.title());
            ps.setString(3, item.description()); ps.setString(4, item.tag());
            ps.setString(5, item.imageUrl()); ps.setString(6, item.imageAlt());
            ps.setInt(7, item.sortOrder()); ps.setBoolean(8, item.published());
        });
    }

    public void replaceSeasons(List<AdminContent.Season> items) {
        jdbcTemplate.update("DELETE FROM season_content");
        jdbcTemplate.batchUpdate("""
                INSERT INTO season_content
                (code, name, period, title, description, sight, note, sort_order, published)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, items, items.size(), (ps, item) -> {
            ps.setString(1, item.code()); ps.setString(2, item.name()); ps.setString(3, item.period());
            ps.setString(4, item.title()); ps.setString(5, item.description()); ps.setString(6, item.sight());
            ps.setString(7, item.note()); ps.setInt(8, item.sortOrder()); ps.setBoolean(9, item.published());
        });
    }

    public void replaceGallery(List<AdminContent.GalleryItem> items) {
        jdbcTemplate.update("DELETE FROM gallery_item");
        jdbcTemplate.batchUpdate("""
                INSERT INTO gallery_item
                (title, image_url, image_alt, scope, layout, sort_order, published)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, items, items.size(), (ps, item) -> {
            ps.setString(1, item.title()); ps.setString(2, item.imageUrl());
            ps.setString(3, item.imageAlt()); ps.setString(4, item.scope());
            ps.setString(5, item.layout()); ps.setInt(6, item.sortOrder()); ps.setBoolean(7, item.published());
        });
    }

    public void replaceGuides(List<AdminContent.GuideItem> items) {
        jdbcTemplate.update("DELETE FROM guide_item");
        jdbcTemplate.batchUpdate("""
                INSERT INTO guide_item (title, content, sort_order, published) VALUES (?, ?, ?, ?)
                """, items, items.size(), (ps, item) -> {
            ps.setString(1, item.title()); ps.setString(2, item.content());
            ps.setInt(3, item.sortOrder()); ps.setBoolean(4, item.published());
        });
    }
}
