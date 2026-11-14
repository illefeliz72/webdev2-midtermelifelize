package com.example.library.dto;

import com.example.library.entity.Book;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        Integer publishedYear,
        Long authorId,
        String authorName) {

    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublishedYear(),
                book.getAuthor().getId(),
                book.getAuthor().getName());
    }
}