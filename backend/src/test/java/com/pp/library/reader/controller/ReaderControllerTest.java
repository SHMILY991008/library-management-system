package com.pp.library.reader.controller;

import com.pp.library.book.exception.GlobalExceptionHandler;
import com.pp.library.reader.dto.CreateReaderRequest;
import com.pp.library.reader.exception.ReaderNotFoundException;
import com.pp.library.reader.service.ReaderService;
import com.pp.library.reader.vo.ReaderResponse;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReaderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ReaderService readerService;

    @InjectMocks
    private ReaderController readerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(readerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCreateReader() throws Exception {
        CreateReaderRequest request = new CreateReaderRequest();
        request.setReaderNo("R-001");
        request.setName("张三");
        request.setPhone("13800000000");

        ReaderResponse response = new ReaderResponse();
        response.setId(1L);
        response.setReaderNo("R-001");
        response.setName("张三");
        response.setPhone("13800000000");

        when(readerService.create(any(CreateReaderRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/readers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.readerNo").value("R-001"))
                .andExpect(jsonPath("$.name").value("张三"));
    }

    @Test
    void shouldFindReaderById() throws Exception {
        ReaderResponse response = new ReaderResponse();
        response.setId(1L);
        response.setReaderNo("R-001");
        response.setName("张三");

        when(readerService.findById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/readers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.readerNo").value("R-001"))
                .andExpect(jsonPath("$.name").value("张三"));
    }

    @Test
    void shouldListReaders() throws Exception {
        ReaderResponse response = new ReaderResponse();
        response.setId(1L);
        response.setReaderNo("R-001");
        response.setName("张三");
        when(readerService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/readers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].readerNo").value("R-001"));
    }

    @Test
    void shouldRejectBlankReaderNo() throws Exception {
        CreateReaderRequest request = new CreateReaderRequest();
        request.setReaderNo("");
        request.setName("张三");

        mockMvc.perform(post("/api/readers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(readerService, never())
                .create(any(CreateReaderRequest.class));
    }

    @Test
    void shouldReturnReaderNotFoundError() throws Exception {
        when(readerService.findById(99L))
                .thenThrow(new ReaderNotFoundException(99L));

        mockMvc.perform(get("/api/readers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("READER_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Reader not found: 99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
