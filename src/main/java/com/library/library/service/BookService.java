package com.library.library.service;

import com.library.library.dto.request.BookRequest;
import com.library.library.dto.response.BookResponse;
import com.library.library.entity.Book;
import com.library.library.exception.ResourceNotFoundException;
import com.library.library.repository.BookRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public BookResponse addBook(BookRequest request) {
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .category(request.getCategory())
                .publishDate(LocalDateTime.now())
                .visible(true)
                .build();

        Book savedBook = bookRepository.save(book);
        return BookResponse.fromEntity(savedBook);
    }

    public void removeBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        bookRepository.delete(book);
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookResponse::fromEntity).toList();
    }

    public List<BookResponse> searchBooks(String keyword) {
        String term = keyword == null ? "" : keyword;
        return bookRepository
                .findByTitleContainingIgnoreCase(term)
                .stream()
                .map(BookResponse::fromEntity)
                .toList();
    }
}
