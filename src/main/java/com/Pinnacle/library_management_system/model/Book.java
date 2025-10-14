package com.Pinnacle.library_management_system.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title too long")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 200, message = "Author name too long")
    private String author;

    @Size(max = 50)
    private String isbn;

    private LocalDate publishedDate;
}
