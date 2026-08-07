package com.pp.library.borrow.exception;

public class BookOutOfStockException extends RuntimeException {

    public BookOutOfStockException(Long bookId) {
        super("Book is out of stock: " + bookId);
    }
}