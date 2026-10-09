package com.pp.library.reader.exception;

public class ReaderNotFoundException extends RuntimeException {

    public ReaderNotFoundException(Long id) {
        super("Reader not found: " + id);
    }
}