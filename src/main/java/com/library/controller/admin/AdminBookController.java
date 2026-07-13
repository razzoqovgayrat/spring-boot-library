package com.library.controller.admin;

import com.library.dto.request.BookRequest;
import com.library.dto.response.BookResponse;
import com.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/book")
@RestController
@RequiredArgsConstructor
public class AdminBookController {
    private final BookService bookService;

    @PostMapping("/{userId}")
    public ResponseEntity<BookResponse> addBook(
            @PathVariable Long userId, @Valid @RequestBody BookRequest request) {
        BookResponse response = bookService.addBook(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeBook(@RequestParam Long bookId, @RequestParam Long userId) {
        bookService.removeBook(bookId, userId);
        return ResponseEntity.ok("Book removed successfully");
    }

    @GetMapping()
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(@RequestParam(required = false) String keyword) {
        List<BookResponse> books = bookService.searchBooks(keyword);
        return ResponseEntity.ok(books);
    }
}
