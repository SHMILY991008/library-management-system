package com.pp.library.borrow.service;

import com.pp.library.book.entity.BookEntity;
import com.pp.library.book.exception.BookNotFoundException;
import com.pp.library.book.mapper.BookMapper;
import com.pp.library.borrow.dto.BorrowBookRequest;
import com.pp.library.borrow.entity.BorrowRecordEntity;
import com.pp.library.borrow.exception.AlreadyReturnedException;
import com.pp.library.borrow.exception.BookOutOfStockException;
import com.pp.library.borrow.exception.BorrowRecordNotFoundException;
import com.pp.library.borrow.mapper.BorrowRecordMapper;
import com.pp.library.borrow.vo.BorrowRecordResponse;
import com.pp.library.reader.entity.ReaderEntity;
import com.pp.library.reader.exception.ReaderNotFoundException;
import com.pp.library.reader.mapper.ReaderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowServiceImpl implements BorrowService {

    private final BookMapper bookMapper;
    private final ReaderMapper readerMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    public BorrowServiceImpl(
            BookMapper bookMapper,
            ReaderMapper readerMapper,
            BorrowRecordMapper borrowRecordMapper) {
        this.bookMapper = bookMapper;
        this.readerMapper = readerMapper;
        this.borrowRecordMapper = borrowRecordMapper;
    }

    @Override
    @Transactional
    public BorrowRecordResponse borrow(BorrowBookRequest request) {
        BookEntity book = bookMapper.findById(request.getBookId());
        if (book == null || Boolean.TRUE.equals(book.getDeleted())) {
            throw new BookNotFoundException(request.getBookId());
        }

        ReaderEntity reader = readerMapper.selectById(request.getReaderId());
        if (reader == null || Boolean.TRUE.equals(reader.getDeleted())) {
            throw new ReaderNotFoundException(request.getReaderId());
        }

        int decreasedRows =
                bookMapper.decreaseAvailableStock(book.getId());

        if (decreasedRows != 1) {
            throw new BookOutOfStockException(book.getId());
        }

        BorrowRecordEntity record = new BorrowRecordEntity();
        record.setBookId(book.getId());
        record.setReaderId(reader.getId());
        record.setBorrowedAt(LocalDateTime.now());
        record.setStatus("BORROWED");

        if (borrowRecordMapper.insert(record) != 1) {
            throw new IllegalStateException("Failed to create borrow record");
        }

        return toResponse(record);
    }

    @Override
    @Transactional
    public BorrowRecordResponse returnBook(Long borrowRecordId) {
        BorrowRecordEntity record = borrowRecordMapper.selectById(borrowRecordId);
        if (record == null) {
            throw new BorrowRecordNotFoundException(borrowRecordId);
        }
        if ("RETURNED".equals(record.getStatus())) {
            throw new AlreadyReturnedException(borrowRecordId);
        }

        BookEntity book = bookMapper.findById(record.getBookId());
        if (book == null || Boolean.TRUE.equals(book.getDeleted())) {
            throw new BookNotFoundException(record.getBookId());
        }

        book.setAvailableStock(book.getAvailableStock() + 1);
        if (bookMapper.update(book) != 1) {
            throw new IllegalStateException("Failed to update book stock");
        }
        if (borrowRecordMapper.markReturned(borrowRecordId) != 1) {
            throw new IllegalStateException("Failed to return book");
        }

        record.setStatus("RETURNED");
        record.setReturnedAt(LocalDateTime.now());
        return toResponse(record);
    }

    @Override
    public List<BorrowRecordResponse> findByReaderId(Long readerId) {
        ReaderEntity reader = readerMapper.selectById(readerId);

        if (reader == null || Boolean.TRUE.equals(reader.getDeleted())) {
            throw new ReaderNotFoundException(readerId);
        }

        List<BorrowRecordEntity> records =
                borrowRecordMapper.selectByReaderId(readerId);

        List<BorrowRecordResponse> responses = new java.util.ArrayList<>();

        for (BorrowRecordEntity record : records) {
            responses.add(toResponse(record));
        }

        return responses;
    }

    private BorrowRecordResponse toResponse(BorrowRecordEntity record) {
        BorrowRecordResponse response = new BorrowRecordResponse();
        response.setId(record.getId());
        response.setBookId(record.getBookId());
        response.setReaderId(record.getReaderId());
        response.setBorrowedAt(record.getBorrowedAt());
        response.setReturnedAt(record.getReturnedAt());
        response.setStatus(record.getStatus());
        return response;
    }
}
