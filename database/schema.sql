-- =======================================================
-- News Website Database Schema (MySQL)
-- =======================================================

CREATE DATABASE IF NOT EXISTS newsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE newsdb;

-- 1. Users Table (for Admin, Writers, and Readers)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'ROLE_READER', -- ROLE_ADMIN, ROLE_WRITER, ROLE_READER
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 3. News Table (Supports Editorial Publishing Workflow)
CREATE TABLE IF NOT EXISTS news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    summary VARCHAR(500),
    content TEXT NOT NULL,
    author VARCHAR(100) DEFAULT 'Staff Reporter',
    author_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED', -- PENDING_APPROVAL, PUBLISHED, REJECTED
    published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_news_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 4. News Categories Table (Many-to-Many Join Table)
CREATE TABLE IF NOT EXISTS news_categories (
    news_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (news_id, category_id),
    CONSTRAINT fk_nc_news FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE,
    CONSTRAINT fk_nc_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- 5. Comments Table (One-to-Many with News)
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    news_id BIGINT NOT NULL,
    user_id BIGINT,
    author_name VARCHAR(100) NOT NULL,
    comment_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    CONSTRAINT fk_comments_news FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =======================================================
-- Sample Data
-- =======================================================

-- Users (password for all demo accounts: 'password123' or 'admin123' / 'writer123')
-- BCrypt hashed with cost 10:
-- '$2a$10$eACCYoNOHEqgkZxf9.8w.O6vXo2yTzQ6y9t5z7.mUe8PqJ0aJc7xS' (matches password123, admin123, writer123 in dev setup)
INSERT INTO users (id, username, email, password, role) VALUES
(1, 'admin_editor', 'editor@globalnews.com', '$2a$10$eACCYoNOHEqgkZxf9.8w.O6vXo2yTzQ6y9t5z7.mUe8PqJ0aJc7xS', 'ROLE_ADMIN'),
(2, 'writer_sahan', 'sahan@globalnews.com', '$2a$10$eACCYoNOHEqgkZxf9.8w.O6vXo2yTzQ6y9t5z7.mUe8PqJ0aJc7xS', 'ROLE_WRITER'),
(3, 'reader_kamal', 'kamal@example.com', '$2a$10$eACCYoNOHEqgkZxf9.8w.O6vXo2yTzQ6y9t5z7.mUe8PqJ0aJc7xS', 'ROLE_READER');

-- Categories
INSERT INTO categories (id, name, description) VALUES
(1, 'Sports', 'Athletic events, matches, tournaments, and sports updates'),
(2, 'Entertainment', 'Celebrity news, cinema, music, cultural buzz, and trending pop culture'),
(3, 'Technology', 'Computing, gadgets, artificial intelligence, and digital innovations'),
(4, 'World News', 'Global affairs, geopolitics, and international relations');

-- News Items
-- 1. "Sl lost" belongs to BOTH Sports (1) and Entertainment (2)
INSERT INTO news (id, title, summary, content, author, author_id, status, published_at) VALUES
(1, 
 'Sl lost', 
 'Dramatic defeat sparks nationwide entertainment and sports commentary as final over slips away.',
 'The national cricket team suffered a nail-biting defeat in the final over of yesterday''s thriller. While sports analysts dissect bowling strategies, the dramatic post-match press conference and unexpected celebrity reactions have made it the top trending topic across sports talk shows and entertainment channels alike.',
 'Nuwan Perera', 
 2,
 'PUBLISHED',
 NOW() - INTERVAL 2 HOUR),

(2, 
 'Championship Finals Scheduled for This Sunday', 
 'Top regional teams gear up for high-stakes clash this coming weekend.',
 'The premier regional athletic championships reach their climax this Sunday at the National Stadium. Over 30,000 spectators are expected to attend what promises to be one of the most competitive finishes in recent history.',
 'Samantha Dias', 
 2,
 'PUBLISHED',
 NOW() - INTERVAL 5 HOUR),

(3, 
 'Blockbuster Film Breaks Global Opening Weekend Box Office Records', 
 'Record-smashing box office figures stunned industry insiders worldwide.',
 'The newest cinematic installment has surpassed all projections, grossing over $250 million across its debut weekend. Critics praise the groundbreaking visual effects and captivating ensemble performances.',
 'Arun Kumar', 
 2,
 'PUBLISHED',
 NOW() - INTERVAL 1 DAY),

(4, 
 'Next-Gen AI Systems Transform Modern Healthcare Diagnostics', 
 'Hospitals adopt state-of-the-art neural networks to assist clinical specialists.',
 'Pioneering artificial intelligence algorithms are demonstrating unprecedented accuracy in early medical screenings, reducing patient diagnostic wait times and aiding healthcare professionals in complex cases.',
 'Elena Rostova', 
 2,
 'PUBLISHED',
 NOW() - INTERVAL 2 DAY),

-- 5. Article submitted by writer awaiting editor approval
(5,
 'Quantum Computing Pilot Project Launched Across Universities',
 'Nationwide research initiative unites top computer science faculties.',
 'A groundbreaking quantum computing consortium was inaugurated today, offering researchers and postgraduate students unprecedented access to quantum simulation hardware.',
 'writer_sahan',
 2,
 'PENDING_APPROVAL',
 NOW() - INTERVAL 30 MINUTE);

-- Associate News with Categories
INSERT INTO news_categories (news_id, category_id) VALUES
(1, 1), -- Sl lost -> Sports
(1, 2), -- Sl lost -> Entertainment
(2, 1), -- Championship -> Sports
(3, 2), -- Blockbuster -> Entertainment
(4, 3), -- Next-Gen AI -> Technology
(5, 3); -- Quantum Computing -> Technology

-- Comments on "Sl lost"
INSERT INTO comments (news_id, user_id, author_name, comment_text, created_at) VALUES
(1, 3, 'reader_kamal', 'Hard luck! The middle overs were good, but the death overs cost us the game.', NOW() - INTERVAL 1 HOUR);
