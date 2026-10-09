package com.pp.library.book.service;

import com.pp.library.book.dto.CreateBookRequest;
import com.pp.library.book.entity.BookEntity;
import com.pp.library.book.exception.BookNotFoundException;
import com.pp.library.book.exception.DuplicateIsbnException;
import com.pp.library.book.exception.InvalidStockException;
import com.pp.library.book.mapper.BookMapper;
import com.pp.library.book.vo.BookResponse;
import com.pp.library.book.dto.UpdateBookRequest;

import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import com.pp.library.book.exception.BookNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookMapper bookMapper;

    public BookService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public BookResponse create(CreateBookRequest request) {
        // 1. 接收 CreateBookRequest
        // 2. 调用 bookMapper.findByIsbn(request.getIsbn())
        // 3. 如果返回 null，创建 BookEntity
        // 4. 把 request 的字段复制到 Entity
        // 5. 调用 bookMapper.insert(entity)
        // 6. 把 Entity 转成 BookResponse
        // 7. 返回 Response
        if (request.getTotalStock() == null
                || request.getAvailableStock() == null
                || request.getTotalStock() < 0
                || request.getAvailableStock() < 0
                || request.getAvailableStock() > request.getTotalStock()) {
            throw new InvalidStockException();
        }
        BookEntity existingBook = bookMapper.findByIsbn(request.getIsbn());  
        
        if (existingBook != null) {
                throw new DuplicateIsbnException(request.getIsbn());
            }

            BookEntity bookEntity = new BookEntity();
            bookEntity.setIsbn(request.getIsbn());
            bookEntity.setTitle(request.getTitle());
            bookEntity.setAuthor(request.getAuthor());
            bookEntity.setPublisher(request.getPublisher());
            bookEntity.setTotalStock(request.getTotalStock());
            bookEntity.setAvailableStock(request.getAvailableStock());

            int rowsAffected = bookMapper.insert(bookEntity);
            if (rowsAffected != 1) {
                throw new RuntimeException("Failed to insert the book into the database.");
            }

            BookResponse response = new BookResponse();
            response.setId(bookEntity.getId());
            response.setIsbn(bookEntity.getIsbn());
            response.setTitle(bookEntity.getTitle());
            response.setAuthor(bookEntity.getAuthor());
            response.setPublisher(bookEntity.getPublisher());
            response.setTotalStock(bookEntity.getTotalStock());
            response.setAvailableStock(bookEntity.getAvailableStock());

            return response;
    }

    public BookResponse findById(Long id) {
        BookEntity bookEntity = bookMapper.findById(id);
        if (bookEntity == null) {
            throw new BookNotFoundException(id);
        }

        BookResponse response = new BookResponse();
        response.setId(bookEntity.getId());
        response.setIsbn(bookEntity.getIsbn());
        response.setTitle(bookEntity.getTitle());
        response.setAuthor(bookEntity.getAuthor());
        response.setPublisher(bookEntity.getPublisher());
        response.setTotalStock(bookEntity.getTotalStock());
        response.setAvailableStock(bookEntity.getAvailableStock());

        return response;
    }

    public void delete(Long id) {
        int rowsAffected = bookMapper.logicalDelete(id);
        if (rowsAffected != 1) {
            throw new BookNotFoundException(id);
        }
    }

    public BookResponse update(Long id, UpdateBookRequest request) {
        BookEntity existingBook = bookMapper.findById(id);
        if (existingBook == null) {
            throw new BookNotFoundException(id);
        }

        if (request.getTotalStock() == null
                || request.getAvailableStock() == null
                || request.getTotalStock() < 0
                || request.getAvailableStock() < 0
                || request.getAvailableStock() > request.getTotalStock()) {
            throw new InvalidStockException();
        }
        
        // Update the book entity with the new values
        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setPublisher(request.getPublisher());
        existingBook.setTotalStock(request.getTotalStock());
        existingBook.setAvailableStock(request.getAvailableStock());

        // Call the mapper to update the book in the database
        int rowsAffected = bookMapper.update(existingBook);
        if (rowsAffected != 1) {
            throw new RuntimeException("Failed to update the book in the database.");
        }

        // Return the updated book response
        BookResponse response = new BookResponse();
        response.setId(existingBook.getId());
        response.setIsbn(existingBook.getIsbn());
        response.setTitle(existingBook.getTitle());
        response.setAuthor(existingBook.getAuthor());
        response.setPublisher(existingBook.getPublisher());
        response.setTotalStock(existingBook.getTotalStock());
        response.setAvailableStock(existingBook.getAvailableStock());

        return response;
    }

    public List<BookResponse> findPage(
            String title,
            int page,
            int size) {

        int offset = (page - 1) * size;

        List<BookEntity> bookEntities =
                bookMapper.findPage(title, offset, size);

        // Entity 转 Response
        return bookEntities.stream().map(bookEntity -> {
            BookResponse response = new BookResponse();
            response.setId(bookEntity.getId());
            response.setIsbn(bookEntity.getIsbn());
            response.setTitle(bookEntity.getTitle());
            response.setAuthor(bookEntity.getAuthor());
            response.setPublisher(bookEntity.getPublisher());
            response.setTotalStock(bookEntity.getTotalStock());
            response.setAvailableStock(bookEntity.getAvailableStock());
            return response;
        }).toList();
    }
}