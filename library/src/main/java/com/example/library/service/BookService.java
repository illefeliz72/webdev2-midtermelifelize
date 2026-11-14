package com.example.library.service;

import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public BookResponse create(BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new DuplicateResourceException("ISBN already exists: " + request.isbn());
        }
        Author author = findAuthorOrThrow(request.authorId());
        Book book = new Book();
        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setPublishedYear(request.publishedYear());
        author.addBook(book);
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getAll(String title, Long authorId, Pageable pageable) {
        String search = (title == null || title.isBlank()) ? null : title;
        Page<Book> page;
        if (authorId != null) {
            page = bookRepository.searchByAuthor(authorId, search, pageable);
        } else if (search != null) {
            page = bookRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else {
            page = bookRepository.findAll(pageable);
        }
        return page.map(BookResponse::from);
    }

    @Transactional(readOnly = true)
    public BookResponse getById(Long id) {
        return BookResponse.from(findBookOrThrow(id));
    }

    public BookResponse update(Long id, BookRequest request) {
        Book book = findBookOrThrow(id);
        if (!book.getIsbn().equals(request.isbn()) && bookRepository.existsByIsbn(request.isbn())) {
            throw new DuplicateResourceException("ISBN already exists: " + request.isbn());
        }
        Author newAuthor = findAuthorOrThrow(request.authorId());
        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setPublishedYear(request.publishedYear());
        if (!book.getAuthor().getId().equals(newAuthor.getId())) {
            book.getAuthor().getBooks().remove(book);
            newAuthor.addBook(book);
        }
        return BookResponse.from(book);
    }

    public void delete(Long id) {
        Book book = findBookOrThrow(id);
        book.getAuthor().getBooks().remove(book);
        bookRepository.delete(book);
    }

    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    private Author findAuthorOrThrow(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
    }
}