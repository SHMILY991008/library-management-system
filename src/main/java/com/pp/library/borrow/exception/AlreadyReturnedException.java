package com.pp.library.borrow.exception;

public class AlreadyReturnedException extends RuntimeException {

    public AlreadyReturnedException(Long id) {
        super("Borrow record already returned: " + id);
    }
}