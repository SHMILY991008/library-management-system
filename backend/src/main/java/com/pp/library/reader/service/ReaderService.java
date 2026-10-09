package com.pp.library.reader.service;

import com.pp.library.reader.dto.CreateReaderRequest;
import com.pp.library.reader.vo.ReaderResponse;
import java.util.List;

public interface ReaderService {

    ReaderResponse create(CreateReaderRequest request);

    ReaderResponse findById(Long id);
    List<ReaderResponse> findAll();
}
