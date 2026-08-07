package com.pp.library.reader.service;

import com.pp.library.reader.dto.CreateReaderRequest;
import com.pp.library.reader.entity.ReaderEntity;
import com.pp.library.reader.exception.DuplicateReaderNoException;
import com.pp.library.reader.exception.ReaderNotFoundException;
import com.pp.library.reader.mapper.ReaderMapper;
import com.pp.library.reader.vo.ReaderResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReaderServiceImpl implements ReaderService {

    private final ReaderMapper readerMapper;

    public ReaderServiceImpl(ReaderMapper readerMapper) {
        this.readerMapper = readerMapper;
    }

    @Override
    public ReaderResponse create(CreateReaderRequest request) {
        ReaderEntity existingReader = readerMapper.selectByReaderNo(request.getReaderNo());
        if (existingReader != null) {
            throw new DuplicateReaderNoException(request.getReaderNo());
        }

        LocalDateTime now = LocalDateTime.now();
        ReaderEntity reader = new ReaderEntity();
        reader.setReaderNo(request.getReaderNo());
        reader.setName(request.getName());
        reader.setPhone(request.getPhone());
        reader.setDeleted(false);
        reader.setCreatedAt(now);
        reader.setUpdatedAt(now);

        if (readerMapper.insert(reader) != 1) {
            throw new IllegalStateException("Failed to create reader");
        }

        return toResponse(reader);
    }

    @Override
    public ReaderResponse findById(Long id) {
        ReaderEntity reader = readerMapper.selectById(id);
        if (reader == null || Boolean.TRUE.equals(reader.getDeleted())) {
            throw new ReaderNotFoundException(id);
        }
        return toResponse(reader);
    }

    private ReaderResponse toResponse(ReaderEntity reader) {
        ReaderResponse response = new ReaderResponse();
        response.setId(reader.getId());
        response.setReaderNo(reader.getReaderNo());
        response.setName(reader.getName());
        response.setPhone(reader.getPhone());
        return response;
    }
}
