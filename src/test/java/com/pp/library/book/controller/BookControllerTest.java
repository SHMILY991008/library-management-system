package com.pp.library.book.controller;

import tools.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.any;
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
        response.setTotalStock(10);
        response.setAvailableStock(10);

        when(bookService.create(any(CreateBookRequest.class)))
                .thenReturn(response);

        CreateBookRequest request = new CreateBookRequest();
        request.setIsbn("TEST-001");
        request.setTitle("Java编程");
        request.setAuthor("张三");
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
}