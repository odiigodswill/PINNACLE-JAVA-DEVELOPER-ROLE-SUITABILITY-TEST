package com.pinnacle.frontend.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Book {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private LocalDate publishedDate;

    public Book() {}

    public Book(Long id, String title, String author, String isbn, LocalDate publishedDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishedDate = publishedDate;
    }

}
