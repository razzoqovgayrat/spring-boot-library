package com.library.service;

import com.library.dto.request.BookRequest;
import com.library.dto.response.*;
import com.library.entity.Book;
import com.library.enums.BookCopyStatus;
import com.library.exception.ConflictException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final CustomUserDetailsService customUserDetailsService;

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
        book.setCreatedAt(Instant.now());
        book.setCreatedBy(customUserDetailsService.getCurrentUser().getUsername());
        bookRepository.save(book);

        return ApiResponse.success(new CreatedResponse(book.getId()));
    }

    @Transactional
    public ApiResponse<BookResponse> getById(Long id) {
        Book book = bookRepository.getByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with this id"));

        BookResponse bookResponse = BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .publishedYear(book.getPublishedYear())
                .authors(book.getAuthors().stream().map(author -> AuthorResponse.fromEntity(author, author.getBooks().size())).toList())
                .categories(book.getCategories().stream().map(category -> CategoryResponse.fromEntity(category, category.getBooks().size())).toList())
                .totalCopies(book.getCopies().size())
                .availableCopies(book.getCopies().size())
                .build();

        return ApiResponse.success(bookResponse);
    }

    @Transactional
    public ApiResponse<Page<BookResponse>> search(String title, String authorName, String categoryName, Pageable pageable) {
        String safeTitle = (title != null && !title.isBlank()) ? title.trim() : "";
        String safeAuthorName = (authorName != null && !authorName.isBlank()) ? authorName.trim() : "";
        String safeCategoryName = (categoryName != null && !categoryName.isBlank()) ? categoryName.trim() : "";

        Page<Book> books = bookRepository.search(safeTitle, safeAuthorName, safeCategoryName, pageable);

        Page<BookResponse> map = books.map(book -> BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .publishedYear(book.getPublishedYear())
                .authors(book.getAuthors().stream().map(author -> AuthorResponse.fromEntity(author, author.getBooks().size())).toList())
                .categories(book.getCategories().stream().map(category -> CategoryResponse.fromEntity(category, category.getBooks().size())).toList())
                .totalCopies(book.getCopies().size())
                .availableCopies(book.getCopies().stream().filter(bookCopy ->
                        bookCopy.getStatus().equals(BookCopyStatus.AVAILABLE) && Objects.isNull(bookCopy.getDeletedAt())
                ).toList().size())
                .build());


        return ApiResponse.success(map);
    }

    public ApiResponse<Void> update(Long id, @Valid BookRequest request) {
        if (bookRepository.existsByIsbnAndIdNot(request.isbn(), id)) {
            throw new ConflictException("this ISBN is already taken");
        }

        Book book = bookRepository.getByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("book not found with this id"));

        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setDescription(request.description());
        book.setPublishedYear(request.publishedYear());
        book.setAuthors(new HashSet<>(authorRepository.findAllById(request.authorIds())));
        book.setCategories(new HashSet<>(categoryRepository.findAllById(request.categoryIds())));
        book.setUpdatedAt(Instant.now());
        book.setUpdatedBy(customUserDetailsService.getCurrentUser().getUsername());
        bookRepository.save(book);

        return ApiResponse.success("book updated");
    }

    public ApiResponse<Void> delete(Long id) {
        Book book = bookRepository.getByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with this id"));

        book.setDeletedAt(Instant.now());
        bookRepository.save(book);

        return ApiResponse.success("book deleted");
    }
}
