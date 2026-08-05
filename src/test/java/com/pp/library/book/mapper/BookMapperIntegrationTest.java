package com.pp.library.book.mapper;

import com.pp.library.book.entity.BookEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional

class BookMapperIntegrationTest {

    @Autowired
    private BookMapper bookMapper;

    @Test
    void testBookEntityToBookDtoMapping() {
        // Given
        BookEntity book = new BookEntity();
        book.setIsbn("12138");
        book.setTitle("harry potter");
        book.setAuthor("JK Rowling");
        book.setPublisher("Bloomsbury");
        book.setTotalStock(10);
        book.setAvailableStock(5);

        int rowsAffected = bookMapper.insert(book);
        assertEquals(1, rowsAffected);
        assertNotNull(book.getId());

        BookEntity savedBook = bookMapper.findById(book.getId());
        assertNotNull(savedBook);
        assertEquals("12138", savedBook.getIsbn());
        assertEquals("harry potter", savedBook.getTitle());
        assertEquals("JK Rowling", savedBook.getAuthor());
        assertEquals("Bloomsbury", savedBook.getPublisher());
        assertEquals(10, savedBook.getTotalStock());
        assertEquals(5, savedBook.getAvailableStock());

        BookEntity foundByIsbn = bookMapper.findByIsbn("12138");
        assertNotNull(foundByIsbn);
        assertEquals("12138", foundByIsbn.getIsbn());
        assertEquals("harry potter", foundByIsbn.getTitle());

        // 调用 logicalDelete(id)
        // 断言影响行数为 1
        // 再次调用 findById(id)
        // 断言返回 null
        int deletedRows = bookMapper.logicalDelete(book.getId());
        assertEquals(1, deletedRows);
        BookEntity deletedBook = bookMapper.findById(book.getId());
        assertEquals(null, deletedBook);
    }
}