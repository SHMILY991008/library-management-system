package com.pp.library.book.exception;

public class InvalidStockException extends RuntimeException {

    public InvalidStockException() {
        super("Available stock cannot be greater than total stock");
    }
}