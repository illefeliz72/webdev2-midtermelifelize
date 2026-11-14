package com.example.library.service;

import com.example.library.dto.AuthorRequest;
import com.example.library.dto.AuthorResponse;
import com.example.library.entity.Author;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.AuthorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public AuthorResponse create(AuthorRequest request) {
        if (authorRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Author already exists: " + request.name());
        }
        Author author = new Author();
        author.setName(request.name());
        author.setCountry(request.country());
        return AuthorResponse.from(authorRepository.save(author));
    }

    @Transactional(readOnly = true)
    public Page<AuthorResponse> getAll(Pageable pageable) {
        return authorRepository.findAll(pageable).map(AuthorResponse::from);
    }

    @Transactional(readOnly = true)
    public AuthorResponse getById(Long id) {
        return AuthorResponse.from(findOrThrow(id));
    }

    public AuthorResponse update(Long id, AuthorRequest request) {
        Author author = findOrThrow(id);
        if (!author.getName().equalsIgnoreCase(request.name())
                && authorRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Author already exists: " + request.name());
        }
        author.setName(request.name());
        author.setCountry(request.country());
        return AuthorResponse.from(author);
    }

    public void delete(Long id) {
        authorRepository.delete(findOrThrow(id));
    }

    private Author findOrThrow(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
    }
}