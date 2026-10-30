-- =======================================================
-- Pure Java + MySQL Database Schema & Seed Data
-- =======================================================

CREATE DATABASE IF NOT EXISTS newsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE newsdb;

-- 1. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 2. News Table
CREATE TABLE IF NOT EXISTS news (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    summary VARCHAR(500),
    content TEXT NOT NULL,
    author VARCHAR(100) DEFAULT 'Staff Reporter',
    published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. News Categories Table (Many-to-Many Join Table)
CREATE TABLE IF NOT EXISTS news_categories (
    news_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (news_id, category_id),
    CONSTRAINT fk_pure_nc_news FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE,
    CONSTRAINT fk_pure_nc_cat FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- 4. Comments Table (One-to-Many with News)
CREATE TABLE IF NOT EXISTS comments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    news_id INT NOT NULL,
    author_name VARCHAR(100) NOT NULL,
    comment_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pure_comm_news FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE
);

-- Sample Data
INSERT INTO categories (id, name, description) VALUES
(1, 'Sports', 'Athletic events, matches, tournaments, and sports updates'),
(2, 'Entertainment', 'Cinema, music, celebrity culture, and pop buzz'),
(3, 'Technology', 'Computing, gadgets, artificial intelligence, and innovations'),
(4, 'World News', 'Global affairs and international relations')
ON DUPLICATE KEY UPDATE name=name;

-- "Sl lost" belongs to BOTH Sports (1) and Entertainment (2)
INSERT INTO news (id, title, summary, content, author) VALUES
(1, 'Sl lost', 
 'Dramatic defeat sparks nationwide entertainment and sports commentary as final over slips away.',
 'The national cricket team suffered a nail-biting defeat in the final over of yesterday''s thriller. While sports analysts dissect bowling strategies, the dramatic post-match press conference and unexpected celebrity reactions have made it the top trending topic across sports talk shows and entertainment channels alike.',
 'Nuwan Perera'),
(2, 'Championship Finals Scheduled for This Sunday',
 'Top regional teams gear up for high-stakes clash this coming weekend.',
 'The premier regional athletic championships reach their climax this Sunday at the National Stadium. Over 30,000 spectators are expected to attend what promises to be one of the most competitive finishes in recent history.',
 'Samantha Dias'),
(3, 'Blockbuster Film Breaks Global Opening Weekend Box Office Records',
 'Record-smashing box office figures stunned industry insiders worldwide.',
 'The newest cinematic installment has surpassed all projections, grossing over $250 million across its debut weekend. Critics praise the groundbreaking visual effects and captivating ensemble performances.',
 'Arun Kumar'),
(4, 'Next-Gen AI Systems Transform Modern Healthcare Diagnostics',
 'Hospitals adopt state-of-the-art neural networks to assist clinical specialists.',
 'Pioneering artificial intelligence algorithms are demonstrating unprecedented accuracy in early medical screenings, reducing patient diagnostic wait times and aiding healthcare professionals in complex cases.',
 'Elena Rostova')
ON DUPLICATE KEY UPDATE title=title;

-- Many-to-Many Mappings
INSERT IGNORE INTO news_categories (news_id, category_id) VALUES
(1, 1), -- Sl lost -> Sports
(1, 2), -- Sl lost -> Entertainment
(2, 1), -- Championship -> Sports
(3, 2), -- Blockbuster -> Entertainment
(4, 3); -- Next-Gen AI -> Technology

-- Sample Comments on "Sl lost"
INSERT INTO comments (news_id, author_name, comment_text) VALUES
(1, 'reader_kamal', 'Hard luck! The middle overs were good, but the death overs cost us the game.'),
(1, 'sports_fan99', 'Entertaining game regardless, kept everyone on the edge of their seats!');
