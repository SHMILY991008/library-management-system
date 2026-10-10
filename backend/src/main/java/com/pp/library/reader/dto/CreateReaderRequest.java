package com.pp.library.reader.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateReaderRequest {

    @NotBlank(message = "Reader number cannot be blank")
    private String readerNo;

    @NotBlank(message = "Reader name cannot be blank")
    private String name;
    private String phone;

    public CreateReaderRequest() {

    }

    // Getters and Setters
    public String getReaderNo() {
        return readerNo;
    }

    public void setReaderNo(String readerNo) {
        this.readerNo = readerNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
