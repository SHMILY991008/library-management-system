package com.pp.library.borrow.mapper;

import com.pp.library.book.entity.BookEntity;
import com.pp.library.book.mapper.BookMapper;
import com.pp.library.borrow.entity.BorrowRecordEntity;
import com.pp.library.reader.entity.ReaderEntity;
import com.pp.library.reader.mapper.ReaderMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class BorrowRecordMapperIntegrationTest {

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private ReaderMapper readerMapper;

    @Test
    void shouldInsertFindAndReturnBorrowRecord() {
        BookEntity book = new BookEntity();
        book.setIsbn("BORROW-BOOK-001");
        book.setTitle("Java 入门");
        book.setAuthor("作者");
        book.setPublisher("出版社");
        book.setTotalStock(5);
        book.setAvailableStock(5);

        bookMapper.insert(book);
        assertNotNull(book.getId());

        ReaderEntity reader = new ReaderEntity();
        reader.setReaderNo("BORROW-READER-001");
        reader.setName("借阅者");
        reader.setPhone("13900000000");
        reader.setDeleted(false);
        reader.setCreatedAt(LocalDateTime.now());
        reader.setUpdatedAt(LocalDateTime.now());

        readerMapper.insert(reader);
        assertNotNull(reader.getId());

        BorrowRecordEntity record = new BorrowRecordEntity();
        record.setBookId(book.getId());
        record.setReaderId(reader.getId());
        record.setBorrowedAt(LocalDateTime.now());
        record.setStatus("BORROWED");

        int insertedRows = borrowRecordMapper.insert(record);

        assertEquals(1, insertedRows);
        assertNotNull(record.getId());

        BorrowRecordEntity savedRecord =
                borrowRecordMapper.selectById(record.getId());

        assertNotNull(savedRecord);
        assertEquals(book.getId(), savedRecord.getBookId());
        assertEquals(reader.getId(), savedRecord.getReaderId());
        assertEquals("BORROWED", savedRecord.getStatus());

        List<BorrowRecordEntity> records =
                borrowRecordMapper.selectByReaderId(reader.getId());

        assertEquals(1, records.size());
        assertTrue(borrowRecordMapper.selectAll().stream().anyMatch(item -> item.getId().equals(record.getId())));

        int returnedRows =
                borrowRecordMapper.markReturned(record.getId());

        assertEquals(1, returnedRows);

        BorrowRecordEntity returnedRecord =
                borrowRecordMapper.selectById(record.getId());

        assertEquals("RETURNED", returnedRecord.getStatus());
        assertNotNull(returnedRecord.getReturnedAt());
    }
}
