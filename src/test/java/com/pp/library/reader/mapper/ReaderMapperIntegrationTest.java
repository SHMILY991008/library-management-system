package com.pp.library.reader.mapper;

import com.pp.library.reader.entity.ReaderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class ReaderMapperIntegrationTest {

    @Autowired
    private ReaderMapper readerMapper;

    @Test
    void shouldInsertAndFindReader() {
        ReaderEntity reader = new ReaderEntity();
        reader.setReaderNo("READER-TEST-001");
        reader.setName("张三");
        reader.setPhone("13800000000");
        reader.setDeleted(false);
        reader.setCreatedAt(LocalDateTime.now());
        reader.setUpdatedAt(LocalDateTime.now());

        int rowsInserted = readerMapper.insert(reader);
        assertEquals(1, rowsInserted);
        assertNotNull(reader.getId());

        ReaderEntity savedReader = readerMapper.selectById(reader.getId());
        assertNotNull(savedReader);
        assertEquals("READER-TEST-001", savedReader.getReaderNo());
        assertEquals("张三", savedReader.getName());
        assertEquals("13800000000", savedReader.getPhone());
        assertEquals(false, savedReader.getDeleted());

        ReaderEntity foundReader =
                readerMapper.selectByReaderNo("READER-TEST-001");

        assertNotNull(foundReader);
        assertEquals(reader.getId(), foundReader.getId());
        assertEquals("张三", foundReader.getName());
    }
}