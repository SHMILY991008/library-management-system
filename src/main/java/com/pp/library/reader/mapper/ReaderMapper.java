package com.pp.library.reader.mapper;

import com.pp.library.reader.entity.ReaderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReaderMapper {

    int insert(ReaderEntity reader);

    ReaderEntity selectById(Long id);

    ReaderEntity selectByReaderNo(String readerNo);
}

