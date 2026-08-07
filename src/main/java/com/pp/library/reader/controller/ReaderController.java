package com.pp.library.reader.controller;

import com.pp.library.reader.dto.CreateReaderRequest;
import com.pp.library.reader.service.ReaderService;
import com.pp.library.reader.vo.ReaderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @PostMapping
    public ResponseEntity<ReaderResponse> create(
            @Valid @RequestBody CreateReaderRequest request) {
        ReaderResponse response = readerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReaderResponse> findById(@PathVariable Long id) {
        ReaderResponse response = readerService.findById(id);
        return ResponseEntity.ok(response);
    }
}
