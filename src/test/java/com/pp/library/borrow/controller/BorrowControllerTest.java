package com.pp.library.borrow.controller;

import com.pp.library.book.exception.GlobalExceptionHandler;
import com.pp.library.borrow.dto.BorrowBookRequest;
import com.pp.library.borrow.exception.BookOutOfStockException;
import com.pp.library.borrow.service.BorrowService;
import com.pp.library.borrow.vo.BorrowRecordResponse;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BorrowControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private BorrowService borrowService;

    @InjectMocks
    private BorrowController borrowController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(borrowController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldBorrowBook() throws Exception {
        BorrowBookRequest request = new BorrowBookRequest();
        request.setBookId(1L);
        request.setReaderId(10L);

        BorrowRecordResponse response = new BorrowRecordResponse();
        response.setId(100L);
        response.setBookId(1L);
        response.setReaderId(10L);
        response.setStatus("BORROWED");

        when(borrowService.borrow(any(BorrowBookRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/borrow-records")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.readerId").value(10))
                .andExpect(jsonPath("$.status").value("BORROWED"));
    }

    @Test
    void shouldReturnBook() throws Exception {
        BorrowRecordResponse response = new BorrowRecordResponse();
        response.setId(100L);
        response.setBookId(1L);
        response.setReaderId(10L);
        response.setStatus("RETURNED");

        when(borrowService.returnBook(100L))
                .thenReturn(response);

        mockMvc.perform(put("/api/borrow-records/100/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("RETURNED"));

        verify(borrowService).returnBook(100L);
    }

    @Test
    void shouldFindBorrowRecordsByReaderId() throws Exception {
        BorrowRecordResponse response = new BorrowRecordResponse();
        response.setId(100L);
        response.setBookId(1L);
        response.setReaderId(10L);
        response.setStatus("BORROWED");

        when(borrowService.findByReaderId(10L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/readers/10/borrow-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].status").value("BORROWED"));
    }

    @Test
    void shouldReturnOutOfStockError() throws Exception {
        BorrowBookRequest request = new BorrowBookRequest();
        request.setBookId(1L);
        request.setReaderId(10L);

        when(borrowService.borrow(any(BorrowBookRequest.class)))
                .thenThrow(new BookOutOfStockException(1L));

        mockMvc.perform(post("/api/borrow-records")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("BOOK_OUT_OF_STOCK"))
                .andExpect(jsonPath("$.message")
                        .value("Book is out of stock: 1"));
    }
}