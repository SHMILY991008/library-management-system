package com.pp.library.reader.service;

import com.pp.library.reader.dto.CreateReaderRequest;
import com.pp.library.reader.entity.ReaderEntity;
import com.pp.library.reader.exception.DuplicateReaderNoException;
import com.pp.library.reader.exception.ReaderNotFoundException;
import com.pp.library.reader.mapper.ReaderMapper;
import com.pp.library.reader.vo.ReaderResponse;
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

@ExtendWith(MockitoExtension.class)
class ReaderServiceTest {

    @Mock
    private ReaderMapper readerMapper;

    @InjectMocks
    private ReaderServiceImpl readerService;

    @Test
    void shouldCreateReaderWhenReaderNoIsUnique() {
        CreateReaderRequest request = new CreateReaderRequest();
        request.setReaderNo("R-001");
        request.setName("张三");
        request.setPhone("13800000000");

        when(readerMapper.selectByReaderNo("R-001"))
                .thenReturn(null);

        when(readerMapper.insert(any(ReaderEntity.class)))
                .thenAnswer(invocation -> {
                    ReaderEntity reader = invocation.getArgument(0);
                    reader.setId(1L);
                    return 1;
                });

        ReaderResponse response = readerService.create(request);

        assertEquals(1L, response.getId());
        assertEquals("R-001", response.getReaderNo());
        assertEquals("张三", response.getName());
        verify(readerMapper).insert(any(ReaderEntity.class));
    }

    @Test
    void shouldRejectDuplicateReaderNo() {
        CreateReaderRequest request = new CreateReaderRequest();
        request.setReaderNo("R-001");
        request.setName("李四");

        ReaderEntity existingReader = new ReaderEntity();
        existingReader.setId(1L);
        existingReader.setReaderNo("R-001");

        when(readerMapper.selectByReaderNo("R-001"))
                .thenReturn(existingReader);

        assertThrows(
                DuplicateReaderNoException.class,
                () -> readerService.create(request)
        );

        verify(readerMapper, never()).insert(any(ReaderEntity.class));
    }

    @Test
    void shouldRejectWhenReaderDoesNotExist() {
        when(readerMapper.selectById(99L))
                .thenReturn(null);

        assertThrows(
                ReaderNotFoundException.class,
                () -> readerService.findById(99L)
        );
    }
}