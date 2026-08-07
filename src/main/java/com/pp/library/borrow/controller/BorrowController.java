package com.pp.library.borrow.controller;

import com.pp.library.borrow.dto.BorrowBookRequest;
import com.pp.library.borrow.service.BorrowService;
import com.pp.library.borrow.vo.BorrowRecordResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping("/borrow-records")
    public ResponseEntity<BorrowRecordResponse> borrow(
            @Valid @RequestBody BorrowBookRequest request) {

        BorrowRecordResponse response =
                borrowService.borrow(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/borrow-records/{id}/return")
    public ResponseEntity<BorrowRecordResponse> returnBook(
            @PathVariable Long id) {

        BorrowRecordResponse response =
                borrowService.returnBook(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/readers/{readerId}/borrow-records")
    public ResponseEntity<List<BorrowRecordResponse>> findByReaderId(
            @PathVariable Long readerId) {

        List<BorrowRecordResponse> responses =
                borrowService.findByReaderId(readerId);

        return ResponseEntity.ok(responses);
    }
}