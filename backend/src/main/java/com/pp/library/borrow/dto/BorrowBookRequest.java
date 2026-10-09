package com.pp.library.borrow.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BorrowBookRequest {

    @NotNull(message = "Book id cannot be null")
    @Positive(message = "Book id must be positive")
    private Long bookId;

    @NotNull(message = "Reader id cannot be null")
    @Positive(message = "Reader id must be positive")
    private Long readerId;

    public BorrowBookRequest() {
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getReaderId() {
        return readerId;
    }

    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }
}