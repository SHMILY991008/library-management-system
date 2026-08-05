CREATE DATABASE IF NOT EXISTS library_manager
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

use library_manager;

CREATE TABLE IF NOT EXISTS book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ISBN VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publisher VARCHAR(255),
    total_stock INT NOT NULL,
    available_stock INT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    CONSTRAINT chk_book_totalstock CHECK (total_stock >= 0),
    CONSTRAINT chk_book_availablestock CHECK (available_stock >= 0 AND available_stock <= total_stock)
);

CREATE INDEX idx_book_title_deleted ON book (title, deleted);