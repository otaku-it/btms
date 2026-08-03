ALTER TABLE village_profile
    ADD COLUMN intro_image_url VARCHAR(300) NOT NULL DEFAULT '/assets/riverwalk.webp' AFTER hero_image_url;
