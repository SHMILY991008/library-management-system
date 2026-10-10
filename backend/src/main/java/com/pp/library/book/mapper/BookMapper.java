package com.pp.library.book.mapper;

import com.pp.library.book.entity.BookEntity;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface BookMapper {

    int insert(BookEntity book);

    BookEntity findById(Long id);

    BookEntity findByIsbn(String isbn);

    int logicalDelete(Long id);

    int update(BookEntity book);

    int decreaseAvailableStock(Long bookId);

    List<BookEntity> findPage(
        @Param("title") String title,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
    
}