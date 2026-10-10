package com.pp.library.borrow.mapper;

import com.pp.library.borrow.entity.BorrowRecordEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BorrowRecordMapper {

    int insert(BorrowRecordEntity record);

    BorrowRecordEntity selectById(Long id);

    List<BorrowRecordEntity> selectByReaderId(Long readerId);
    List<BorrowRecordEntity> selectAll();

    int markReturned(Long id);
}
