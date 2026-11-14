package com.example.library.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRequest(
        @NotBlank(message = "Title is required") @Size(max = 200, message = "Title must be at most 200 characters") String title,

        @NotBlank(message = "ISBN is required") @Size(min = 10, max = 17, message = "ISBN must be 10 to 17 characters") String isbn,

        @Min(value = 1000, message = "Published year must be 1000 or later") @Max(value = 2100, message = "Published year must be 2100 or earlier") Integer publishedYear,

        @NotNull(message = "Author id is required") Long authorId) {
}