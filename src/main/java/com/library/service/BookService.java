package com.library.service;

import com.library.dto.request.BookRequest;
import com.library.dto.response.*;
import com.library.entity.Book;
import com.library.exception.ConflictException;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    public ApiResponse<CreatedResponse> create(@Valid BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new ConflictException("Book already exists with this ISBN");
        }

        Book book = Book.builder()
                .title(request.title())
                .description(request.description())
                .isbn(request.isbn())
                .publishedYear(request.publishedYear())
                .build();
        book.setCreatedAt(Instant.now());
        book.setAuthors(new HashSet<>(authorRepository.findAllById(request.authorIds())));
        book.setCategories(new HashSet<>(categoryRepository.findAllById(request.categoryIds())));
        bookRepository.save(book);

        return ApiResponse.success(new CreatedResponse(book.getId()));
    }

    public ApiResponse<BookResponse> getById(Long id) {
        return null;
    }

    public ApiResponse<Page<BookResponse>> search(String title, String authorName, String categoryName, Pageable pageable) {
        Page<Book> books = bookRepository.search(title, authorName, categoryName, pageable);
        books.map(book -> BookResponse.builder()
                .title(book.getTitle())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .publishedYear(book.getPublishedYear())
                .authors(book.getAuthors().stream().map(author -> AuthorResponse.fromEntity(author, author.getBooks().size())).toList())
                .categories(book.getCategories().stream().map(category -> CategoryResponse.fromEntity(category, category.getBooks().size())).toList())
                .totalCopies(book.getCopies().size())
                .availableCopies(book.getCopies().size())
                .build());
        return null;
    }

    public ApiResponse<Void> update(Long id, @Valid BookRequest request) {

        return ApiResponse.success("book updated");
    }

    public ApiResponse<Void> delete(Long id) {

        return ApiResponse.success("book deleted");
    }
}
