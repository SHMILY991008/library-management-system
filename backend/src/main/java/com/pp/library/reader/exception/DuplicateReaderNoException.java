package com.pp.library.reader.exception;

public class DuplicateReaderNoException extends RuntimeException {

    public DuplicateReaderNoException(String readerNo) {
        super("Reader number already exists: " + readerNo);
    }
}