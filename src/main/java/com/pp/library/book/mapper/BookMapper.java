package com.pp.library.book.mapper;

import com.pp.library.book.entity.BookEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper {

    int insert(BookEntity book);

    BookEntity findById(Long id);

    BookEntity findByIsbn(String isbn);

    int logicalDelete(Long id);
}