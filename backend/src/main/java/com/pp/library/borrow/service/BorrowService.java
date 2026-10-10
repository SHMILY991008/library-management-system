package com.pp.library.borrow.service;

import com.pp.library.borrow.dto.BorrowBookRequest;
import com.pp.library.borrow.vo.BorrowRecordResponse;

import java.util.List;

public interface BorrowService {

    BorrowRecordResponse borrow(BorrowBookRequest request);

    BorrowRecordResponse returnBook(Long borrowRecordId);

    List<BorrowRecordResponse> findByReaderId(Long readerId);
    List<BorrowRecordResponse> findAll();
}
