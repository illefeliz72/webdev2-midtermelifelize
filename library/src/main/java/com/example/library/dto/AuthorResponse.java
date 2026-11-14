package com.example.library.dto;

import com.example.library.entity.Author;

public record AuthorResponse(Long id, String name, String country, int bookCount) {

    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getCountry(),
                author.getBooks().size());
    }
}