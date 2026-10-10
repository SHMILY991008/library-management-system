package com.pp.library.book.controller;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pp.library.book.dto.CreateBookRequest;
import com.pp.library.book.service.BookService;
import com.pp.library.book.vo.BookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.pp.library.book.dto.UpdateBookRequest;
import com.pp.library.book.exception.BookNotFoundException;
import com.pp.library.book.exception.GlobalExceptionHandler;
import com.pp.library.book.exception.DuplicateIsbnException;
import com.pp.library.book.exception.InvalidStockException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCreateBook() throws Exception {
        BookResponse response = new BookResponse();
        response.setId(1L);
        response.setIsbn("TEST-001");
        response.setTitle("Java编程");
        response.setAuthor("张三");
        response.setPublisher("人民出版社");
        response.setTotalStock(10);
        response.setAvailableStock(10);

        when(bookService.create(any(CreateBookRequest.class)))
                .thenReturn(response);

        CreateBookRequest request = new CreateBookRequest();
        request.setIsbn("TEST-001");
        request.setTitle("Java编程");
        request.setAuthor("张三");
        request.setPublisher("人民出版社");
        request.setTotalStock(10);
        request.setAvailableStock(10);

        mockMvc.perform(post("/api/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isbn").value("TEST-001"))
                .andExpect(jsonPath("$.title").value("Java编程"))
                .andExpect(jsonPath("$.totalStock").value(10))
                .andExpect(jsonPath("$.availableStock").value(10));
    }

    @Test
    void shouldGetBookById() throws Exception {
        BookResponse response = new BookResponse();
        response.setId(1L);
        response.setIsbn("TEST-001");
        response.setTitle("Java编程");
        response.setAuthor("张三");
        response.setTotalStock(10);
        response.setAvailableStock(8);

        when(bookService.findById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isbn").value("TEST-001"))
                .andExpect(jsonPath("$.title").value("Java编程"))
                .andExpect(jsonPath("$.availableStock").value(8));
    }

    
    @Test
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).delete(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService).delete(1L);
    }

    @Test
    void shouldUpdateBook() throws Exception {
        BookResponse response = new BookResponse();
        response.setId(1L);
        response.setIsbn("TEST-001");
        response.setTitle("更新后的Java编程");
        response.setAuthor("李四");
        response.setPublisher("人民出版社");
        response.setTotalStock(20);
        response.setAvailableStock(18);

        when(bookService.update(
                any(Long.class),
                any(UpdateBookRequest.class)))
                .thenReturn(response);

        UpdateBookRequest request = new UpdateBookRequest();
        request.setIsbn("TEST-001");
        request.setTitle("更新后的Java编程");
        request.setAuthor("李四");
        request.setPublisher("人民出版社");
        request.setTotalStock(20);
        request.setAvailableStock(18);

        mockMvc.perform(put("/api/books/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("更新后的Java编程"))
                .andExpect(jsonPath("$.author").value("李四"))
                .andExpect(jsonPath("$.totalStock").value(20))
                .andExpect(jsonPath("$.availableStock").value(18));
    }

    @Test
    void shouldFindPagedBooks() throws Exception {
        BookResponse response = new BookResponse();
        response.setId(1L);
        response.setIsbn("PAGE-001");
        response.setTitle("Java编程");
        response.setAuthor("张三");
        response.setTotalStock(10);
        response.setAvailableStock(8);

        when(bookService.findPage("Java", 1, 10))
                .thenReturn(java.util.Collections.singletonList(response));

        mockMvc.perform(get("/api/books")
                        .param("title", "Java")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].isbn").value("PAGE-001"))
                .andExpect(jsonPath("$[0].title").value("Java编程"));
    }

    @Test
    void shouldRejectBlankIsbn() throws Exception {
        CreateBookRequest request = new CreateBookRequest();
        request.setIsbn("");
        request.setTitle("Java");
        request.setAuthor("张三");
        request.setPublisher("测试出版社");
        request.setTotalStock(10);
        request.setAvailableStock(10);

        mockMvc.perform(post("/api/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(bookService, org.mockito.Mockito.never())
                .create(any(CreateBookRequest.class));
    }

    @Test
    void shouldRejectIsbnLongerThanTwentyCharacters() throws Exception {
        CreateBookRequest request = new CreateBookRequest();
        request.setIsbn("POSTMAN-BOOK-20260807-001");
        request.setTitle("Java");
        request.setAuthor("Author");
        request.setPublisher("Publisher");
        request.setTotalStock(10);
        request.setAvailableStock(10);

        mockMvc.perform(post("/api/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("ISBN must not exceed 20 characters"));

        verify(bookService, org.mockito.Mockito.never())
                .create(any(CreateBookRequest.class));
    }

    @Test
    void shouldReturnBookNotFoundError() throws Exception {
        when(bookService.findById(99L))
                .thenThrow(new BookNotFoundException(99L));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Book not found: 99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnInvalidStockError() throws Exception {
        CreateBookRequest request = new CreateBookRequest();
        request.setIsbn("STOCK-001");
        request.setTitle("Java");
        request.setAuthor("张三");
        request.setPublisher("测试出版社");
        request.setTotalStock(5);
        request.setAvailableStock(6);

        when(bookService.create(any(CreateBookRequest.class)))
                .thenThrow(new InvalidStockException());

        mockMvc.perform(post("/api/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_STOCK"))
                .andExpect(jsonPath("$.message")
                        .value("Available stock cannot be greater than total stock"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnDuplicateIsbnError() throws Exception {
        CreateBookRequest request = new CreateBookRequest();
        request.setIsbn("DUPLICATE-001");
        request.setTitle("Java");
        request.setAuthor("张三");
        request.setPublisher("测试出版社");
        request.setTotalStock(10);
        request.setAvailableStock(10);

        when(bookService.create(any(CreateBookRequest.class)))
                .thenThrow(new DuplicateIsbnException("DUPLICATE-001"));

        mockMvc.perform(post("/api/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_ISBN"))
                .andExpect(jsonPath("$.message")
                        .value("ISBN already exists: DUPLICATE-001"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
