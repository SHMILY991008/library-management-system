package com.pp.library.borrow.service;

import com.pp.library.book.entity.BookEntity;
import com.pp.library.book.mapper.BookMapper;
import com.pp.library.borrow.dto.BorrowBookRequest;
import com.pp.library.borrow.entity.BorrowRecordEntity;
import com.pp.library.borrow.exception.AlreadyReturnedException;
import com.pp.library.borrow.exception.BookOutOfStockException;
import com.pp.library.borrow.mapper.BorrowRecordMapper;
import com.pp.library.borrow.vo.BorrowRecordResponse;
import com.pp.library.reader.entity.ReaderEntity;
import com.pp.library.reader.mapper.ReaderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock
    private BookMapper bookMapper;

    @Mock
    private ReaderMapper readerMapper;

    @Mock
    private BorrowRecordMapper borrowRecordMapper;

    @InjectMocks
    private BorrowServiceImpl borrowService;

    @Test
    void shouldBorrowBookSuccessfully() {
        BookEntity book = new BookEntity();
        book.setId(1L);
        book.setTotalStock(5);
        book.setAvailableStock(2);
        book.setDeleted(false);

        ReaderEntity reader = new ReaderEntity();
        reader.setId(10L);
        reader.setDeleted(false);

        BorrowBookRequest request = new BorrowBookRequest();
        request.setBookId(1L);
        request.setReaderId(10L);

        when(bookMapper.findById(1L)).thenReturn(book);
        when(readerMapper.selectById(10L)).thenReturn(reader);
        when(bookMapper.decreaseAvailableStock(1L)).thenReturn(1);
        when(borrowRecordMapper.insert(any(BorrowRecordEntity.class)))
                .thenAnswer(invocation -> {
                    BorrowRecordEntity record =
                            invocation.getArgument(0);
                    record.setId(100L);
                    return 1;
                });

        BorrowRecordResponse response =
                borrowService.borrow(request);

        assertEquals(100L, response.getId());
        assertEquals(1L, response.getBookId());
        assertEquals(10L, response.getReaderId());
        assertEquals("BORROWED", response.getStatus());
        verify(bookMapper).decreaseAvailableStock(1L);
        verify(borrowRecordMapper).insert(any(BorrowRecordEntity.class));
    }

    @Test
    void shouldRejectBorrowWhenBookOutOfStock() {
        BookEntity book = new BookEntity();
        book.setId(1L);
        book.setAvailableStock(1);
        book.setDeleted(false);

        ReaderEntity reader = new ReaderEntity();
        reader.setId(10L);
        reader.setDeleted(false);

        BorrowBookRequest request = new BorrowBookRequest();
        request.setBookId(1L);
        request.setReaderId(10L);

        when(bookMapper.findById(1L)).thenReturn(book);
        when(readerMapper.selectById(10L)).thenReturn(reader);
        when(bookMapper.decreaseAvailableStock(1L)).thenReturn(0);

        assertThrows(
                BookOutOfStockException.class,
                () -> borrowService.borrow(request)
        );

        verify(bookMapper, never()).update(any(BookEntity.class));
        verify(borrowRecordMapper, never())
                .insert(any(BorrowRecordEntity.class));
        verify(bookMapper).decreaseAvailableStock(1L);
    }

    @Test
    void shouldReturnBookSuccessfully() {
        BorrowRecordEntity record = new BorrowRecordEntity();
        record.setId(100L);
        record.setBookId(1L);
        record.setReaderId(10L);
        record.setStatus("BORROWED");

        BookEntity book = new BookEntity();
        book.setId(1L);
        book.setTotalStock(5);
        book.setAvailableStock(1);
        book.setDeleted(false);

        when(borrowRecordMapper.selectById(100L))
                .thenReturn(record);
        when(bookMapper.findById(1L))
                .thenReturn(book);
        when(bookMapper.update(any(BookEntity.class)))
                .thenReturn(1);
        when(borrowRecordMapper.markReturned(100L))
                .thenReturn(1);

        BorrowRecordResponse response =
                borrowService.returnBook(100L);

        assertEquals("RETURNED", response.getStatus());
        assertEquals(2, book.getAvailableStock());

        verify(bookMapper).update(book);
        verify(borrowRecordMapper).markReturned(100L);
    }

    @Test
    void shouldRejectReturningAlreadyReturnedRecord() {
        BorrowRecordEntity record = new BorrowRecordEntity();
        record.setId(100L);
        record.setStatus("RETURNED");

        when(borrowRecordMapper.selectById(100L))
                .thenReturn(record);

        assertThrows(
                AlreadyReturnedException.class,
                () -> borrowService.returnBook(100L)
        );

        verify(bookMapper, never()).update(any(BookEntity.class));
        verify(borrowRecordMapper, never()).markReturned(100L);
    }

    @Test
    void shouldFindBorrowRecordsByReaderId() {
        ReaderEntity reader = new ReaderEntity();
        reader.setId(10L);
        reader.setDeleted(false);

        BorrowRecordEntity record = new BorrowRecordEntity();
        record.setId(100L);
        record.setBookId(1L);
        record.setReaderId(10L);
        record.setStatus("BORROWED");

        when(readerMapper.selectById(10L)).thenReturn(reader);
        when(borrowRecordMapper.selectByReaderId(10L))
                .thenReturn(List.of(record));

        List<BorrowRecordResponse> responses =
                borrowService.findByReaderId(10L);

        assertEquals(1, responses.size());
        assertEquals(100L, responses.get(0).getId());
        assertEquals(1L, responses.get(0).getBookId());
        assertEquals("BORROWED", responses.get(0).getStatus());
    }
}
