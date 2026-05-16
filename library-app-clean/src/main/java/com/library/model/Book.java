package com.library.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotNull(message = "Published year is required")
    @Positive(message = "Published year must be positive")
    private Integer publishedYear;

    @NotNull(message = "Total copies is required")
    @Positive(message = "Total copies must be positive")
    private Integer totalCopies;

    private Integer availableCopies;

    private BookStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum BookStatus {
        AVAILABLE, CHECKED_OUT, RESERVED, LOST
    }
}
