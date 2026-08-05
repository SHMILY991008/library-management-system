package com.pp.library.book.dto;

public class CreateBookRequest {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer totalStock;
    private Integer availableStock;

    public CreateBookRequest() {

    }

    //setters
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    //getters
    public String getIsbn() {
        return isbn;
    }   

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public Integer getTotalStock() {
        return totalStock;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }
}
