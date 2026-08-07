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

CREATE TABLE IF NOT EXISTS reader (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reader_no  VARCHAR(30) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    deleted BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
)

CREATE TABLE IF NOT EXISTS borrow_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reader_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    borrow_date DATETIME NOT NULL,
    return_date DATETIME,

    CONSTRAINT fk_borrow_book FOREIGN KEY (book_id) REFERENCES book(id),
    CONSTRAINT fk_borrow_reader FOREIGN KEY (reader_id) REFERENCES reader(id);
    CONSTRAINT chk_borrow_status CHECK (status IN ('BORROWED', 'RETURNED'))
);

CREATE INDEX idx_reader_deleted
    ON reader (reader_no, deleted);

CREATE INDEX idx_borrow_reader
    ON borrow_record (reader_id);

CREATE INDEX idx_borrow_book
    ON borrow_record (book_id);