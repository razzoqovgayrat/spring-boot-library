package com.library.service;

import com.library.dto.request.BookRequest;
import com.library.dto.response.BookResponse;
import com.library.entity.Book;
import com.library.entity.User;
import com.library.enums.Role;
import com.library.enums.UserStatus;
import com.library.exception.ResourceNotFoundException;
import com.library.exception.UserBlockedException;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final List<Role> ROLES = List.of(Role.ADMIN, Role.STAFF);

    public BookResponse addBook(BookRequest request, Long userId) {
        validationProfile(userId);
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

    public void removeBook(Long bookId, Long userId) {
        validationProfile(userId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        book.setVisible(false);
        bookRepository.save(book);
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

    private void validationProfile(Long userId) {
        User user = userRepository.findById(userId)
                .filter(u -> ROLES.contains(u.getRole()) && u.isVisible())
                .orElseThrow(() -> new ResourceNotFoundException("user not found with id: " + userId));

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UserBlockedException("Blocked users cannot add or remove books");
        }

    }
}
