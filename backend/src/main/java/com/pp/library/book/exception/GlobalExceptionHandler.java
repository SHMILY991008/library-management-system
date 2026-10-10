package com.pp.library.book.exception;

import com.pp.library.book.vo.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.pp.library.reader.exception.DuplicateReaderNoException;
import com.pp.library.reader.exception.ReaderNotFoundException;
import com.pp.library.borrow.exception.AlreadyReturnedException;
import com.pp.library.borrow.exception.BookOutOfStockException;
import com.pp.library.borrow.exception.BorrowRecordNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        ErrorResponse response = new ErrorResponse(
                "VALIDATION_ERROR",
                message
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFoundException(
            BookNotFoundException exception) {

        ErrorResponse response = new ErrorResponse(
                "BOOK_NOT_FOUND",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(InvalidStockException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStockException(
            InvalidStockException exception) {

        ErrorResponse response = new ErrorResponse(
                "INVALID_STOCK",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(DuplicateIsbnException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateIsbnException(
            DuplicateIsbnException exception) {

        ErrorResponse response = new ErrorResponse(
                "DUPLICATE_ISBN",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(ReaderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReaderNotFoundException(
            ReaderNotFoundException exception) {

        ErrorResponse response = new ErrorResponse(
                "READER_NOT_FOUND",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(DuplicateReaderNoException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateReaderNoException(
            DuplicateReaderNoException exception) {

        ErrorResponse response = new ErrorResponse(
                "DUPLICATE_READER_NO",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(BookOutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleBookOutOfStockException(
            BookOutOfStockException exception) {

        ErrorResponse response = new ErrorResponse(
                "BOOK_OUT_OF_STOCK",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(BorrowRecordNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBorrowRecordNotFoundException(
            BorrowRecordNotFoundException exception) {

        ErrorResponse response = new ErrorResponse(
                "BORROW_RECORD_NOT_FOUND",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(AlreadyReturnedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyReturnedException(
            AlreadyReturnedException exception) {

        ErrorResponse response = new ErrorResponse(
                "ALREADY_RETURNED",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
}