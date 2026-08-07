package com.pp.library.borrow.exception;

public class BorrowRecordNotFoundException extends RuntimeException {

    public BorrowRecordNotFoundException(Long id) {
        super("Borrow record not found: " + id);
    }
}