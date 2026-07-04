package com.library.library.controller.admin;

import com.library.library.dto.request.BookRequest;
import com.library.library.dto.response.ApiResponse;
import com.library.library.dto.response.BookResponse;
import com.library.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/library/admin/book-controller")
@RestController
@RequiredArgsConstructor
public class AdminBookController {

    private final BookService bookService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<BookResponse>> addBook(@Valid @RequestBody BookRequest request) {
        BookResponse response = bookService.addBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book added successfully", response));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<ApiResponse<Void>> removeBook(@PathVariable Long id) {
        bookService.removeBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book removed successfully"));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        List<BookResponse> books = bookService.getAllBooks();
        return ResponseEntity.ok(ApiResponse.success("Books retrieved successfully", books));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchBooks(
            @RequestParam(required = false) String keyword) {
        List<BookResponse> books = bookService.searchBooks(keyword);
        return ResponseEntity.ok(ApiResponse.success("Books retrieved successfully", books));
    }

}
