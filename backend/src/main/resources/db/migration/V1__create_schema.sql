CREATE TABLE village_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL,
    location VARCHAR(160) NOT NULL,
    eyebrow VARCHAR(120) NOT NULL,
    slogan VARCHAR(200) NOT NULL,
    intro_lead TEXT NOT NULL,
    intro_body TEXT NOT NULL,
    land_title VARCHAR(160) NOT NULL,
    land_description TEXT NOT NULL,
    map_url VARCHAR(500) NOT NULL,
    published BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE site_stat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    display_value VARCHAR(40) NOT NULL,
    label VARCHAR(100) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    published BOOLEAN NOT NULL DEFAULT TRUE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE journey_stop (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    duration VARCHAR(80) NOT NULL,
    title VARCHAR(120) NOT NULL,
    description TEXT NOT NULL,
    tag VARCHAR(80) NOT NULL,
    image_url VARCHAR(300) NOT NULL,
    image_alt VARCHAR(200) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    published BOOLEAN NOT NULL DEFAULT TRUE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE season_content (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(20) NOT NULL,
    period VARCHAR(80) NOT NULL,
    title VARCHAR(160) NOT NULL,
    description TEXT NOT NULL,
    sight VARCHAR(120) NOT NULL,
    note VARCHAR(120) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    published BOOLEAN NOT NULL DEFAULT TRUE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE gallery_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    image_url VARCHAR(300) NOT NULL,
    image_alt VARCHAR(200) NOT NULL,
    scope VARCHAR(100) NOT NULL,
    layout VARCHAR(20) NOT NULL DEFAULT 'standard',
    sort_order INT NOT NULL DEFAULT 0,
    published BOOLEAN NOT NULL DEFAULT TRUE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE guide_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(80) NOT NULL,
    content TEXT NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    published BOOLEAN NOT NULL DEFAULT TRUE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE visitor_inquiry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(40) NOT NULL,
    email VARCHAR(120) NOT NULL,
    visit_date DATE NULL,
    party_size INT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_inquiry_created_at (created_at),
    INDEX idx_inquiry_status (status)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
