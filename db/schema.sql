CREATE DATABASE IF NOT EXISTS library_manager
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_manager;

CREATE TABLE IF NOT EXISTS book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publisher VARCHAR(255),
    total_stock INT NOT NULL,
    available_stock INT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_book_title_deleted (title, deleted),
    CONSTRAINT chk_book_total_stock CHECK (total_stock >= 0),
    CONSTRAINT chk_book_available_stock CHECK (
        available_stock >= 0 AND available_stock <= total_stock
    )
);

CREATE TABLE IF NOT EXISTS reader (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reader_no VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_reader_deleted (reader_no, deleted)
);

CREATE TABLE IF NOT EXISTS borrow_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reader_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    borrowed_at DATETIME NOT NULL,
    returned_at DATETIME,
    status VARCHAR(10) NOT NULL,
    INDEX idx_borrow_reader (reader_id),
    INDEX idx_borrow_book (book_id),
    CONSTRAINT fk_borrow_book FOREIGN KEY (book_id) REFERENCES book(id),
    CONSTRAINT fk_borrow_reader FOREIGN KEY (reader_id) REFERENCES reader(id),
    CONSTRAINT chk_borrow_status CHECK (status IN ('BORROWED', 'RETURNED'))
);
