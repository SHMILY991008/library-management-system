package com.pp.library.book.mapper;

import com.pp.library.book.entity.BookEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNull;
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
        assertNull(deletedBook);

        // Java核心技术
        // Spring Boot实战
        // Python编程

        BookEntity book1 = new BookEntity();
        book1.setIsbn("ISBN-001");
        book1.setTitle("Java核心技术");
        book1.setAuthor("张三");
        book1.setPublisher("人民邮电出版社");
        book1.setTotalStock(10);
        book1.setAvailableStock(5);

        BookEntity book2 = new BookEntity();
        book2.setIsbn("ISBN-002");
        book2.setTitle("Spring Boot实战");
        book2.setAuthor("李四");
        book2.setPublisher("电子工业出版社");
        book2.setTotalStock(15);
        book2.setAvailableStock(10);
        
        BookEntity book3 = new BookEntity();
        book3.setIsbn("ISBN-003");
        book3.setTitle("Python编程");
        book3.setAuthor("王五");
        book3.setPublisher("清华大学出版社");
        book3.setTotalStock(20);
        book3.setAvailableStock(15);

        assertEquals(1, bookMapper.insert(book1));
        assertEquals(1, bookMapper.insert(book2));
        assertEquals(1, bookMapper.insert(book3));

        assertNotNull(book1.getId());
        assertNotNull(book2.getId());
        assertNotNull(book3.getId());

        // findPage("Java", 0, 10)
        // → 只返回 Java相关图书
        int javaBooksCount = bookMapper.findPage("Java", 0, 10).size();
        assertEquals(1, javaBooksCount);

        // findPage(null, 0, 2)
        // → 最多返回 2 条
        int firstPageCount = bookMapper.findPage(null, 0, 2).size();
        assertEquals(2, firstPageCount);

        // 逻辑删除其中一本
        // findPage(null, 0, 10)
        // → 不再包含已删除图书
        bookMapper.logicalDelete(book2.getId());
        int deletedBookCount =
                bookMapper.findPage("Spring Boot", 0, 10).size();
        assertEquals(0, deletedBookCount);
    }

    @Test
    void shouldDecreaseStockOnlyOnceWhenOnlyOneAvailable() {
        BookEntity book = new BookEntity();
        book.setIsbn("ATOMIC-001");
        book.setTitle("Atomic Stock Test");
        book.setAuthor("PP");
        book.setPublisher("Learning Press");
        book.setTotalStock(1);
        book.setAvailableStock(1);

        assertEquals(1, bookMapper.insert(book));
        assertNotNull(book.getId());

        int firstResult =
                bookMapper.decreaseAvailableStock(book.getId());

        int secondResult =
                bookMapper.decreaseAvailableStock(book.getId());

        assertEquals(1, firstResult);
        assertEquals(0, secondResult);

        BookEntity savedBook = bookMapper.findById(book.getId());

        assertNotNull(savedBook);
        assertEquals(0, savedBook.getAvailableStock());
    }
}
