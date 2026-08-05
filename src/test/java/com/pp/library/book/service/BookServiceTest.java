package com.pp.library.book.service;

import com.pp.library.book.mapper.BookMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pp.library.book.dto.CreateBookRequest;
import com.pp.library.book.entity.BookEntity;
import com.pp.library.book.exception.BookNotFoundException;
import com.pp.library.book.vo.BookResponse;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldCreateBookWhenRequestIsValid() {
        // 给定合法图书参数
        // Mapper 插入成功
        // 调用 service.create(...)
        // 返回结果包含 ISBN、书名和库存
        CreateBookRequest request = new CreateBookRequest();
        request.setIsbn("99999");
        request.setTitle("journey to the west");
        request.setAuthor("Wu Cheng'en");
        request.setPublisher("People's Literature Publishing House");
        request.setTotalStock(23);
        request.setAvailableStock(11);

        when(bookMapper.findByIsbn("99999")).thenReturn(null);
        when(bookMapper.insert(any(BookEntity.class))).thenAnswer(invocation -> {
            BookEntity book = invocation.getArgument(0);
            book.setId(1L); // 模拟数据库生成的 ID
            return 1; // 模拟插入成功，返回影响行数为 1
        });

        BookResponse response = bookService.create(request);
        assertEquals("99999", response.getIsbn());
        assertEquals("journey to the west", response.getTitle());  
        assertEquals("Wu Cheng'en", response.getAuthor());
        assertEquals("People's Literature Publishing House", response.getPublisher()); 
        assertEquals(23, response.getTotalStock());
        assertEquals(11, response.getAvailableStock());
    }

    @Test
    void shouldRejectWhenAvailableStockGreaterThanTotalStock() {
        CreateBookRequest request = new CreateBookRequest();
        request.setIsbn("INVALID-001");
        request.setTitle("Invalid Stock Book");
        request.setAuthor("Author");
        request.setPublisher("Publisher");
        request.setTotalStock(5);
        request.setAvailableStock(6);

        assertThrows(
                IllegalArgumentException.class,
                () -> bookService.create(request)
        );
    }

    @Test
    void shouldRejectWhenIsbnAlreadyExists() {
        CreateBookRequest request = new CreateBookRequest();
        request.setIsbn("EXISTING-001");
        request.setTitle("Another Book");
        request.setAuthor("Author");
        request.setPublisher("Publisher");
        request.setTotalStock(5);
        request.setAvailableStock(5);

        BookEntity existingBook = new BookEntity();
        existingBook.setId(10L);
        existingBook.setIsbn("EXISTING-001");

        when(bookMapper.findByIsbn("EXISTING-001"))
                .thenReturn(existingBook);

        assertThrows(
                IllegalArgumentException.class,
                () -> bookService.create(request)
        );
        verify(bookMapper, never()).insert(any(BookEntity.class));
    }

    @Test
    void shouldRejectWhenBookDoesNotExist() {
        when(bookMapper.findById(99L)).thenReturn(null);

        assertThrows(
                BookNotFoundException.class,
                () -> bookService.findById(99L)
        );
    }

    @Test
    void shouldDeleteBookWhenExists() {
        when(bookMapper.logicalDelete(1L)).thenReturn(1);

        bookService.delete(1L);

        verify(bookMapper).logicalDelete(1L);
    }


}