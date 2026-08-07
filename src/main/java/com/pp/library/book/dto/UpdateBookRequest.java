package com.pp.library.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class UpdateBookRequest {
    @NotBlank(message = "ISBN cannot be blank")
    private String isbn;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Author cannot be blank")
    private String author;

    @NotBlank(message = "Publisher cannot be blank")
    private String publisher;

    @NotNull(message = "Total stock cannot be null")
    @PositiveOrZero(message = "Total stock cannot be negative")
    private Integer totalStock;

    @NotNull(message = "Available stock cannot be null")
    @PositiveOrZero(message = "Available stock cannot be negative")
    private Integer availableStock;

    public UpdateBookRequest() {}

    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public Integer getTotalStock() { return totalStock; }
    public Integer getAvailableStock() { return availableStock; }
}
