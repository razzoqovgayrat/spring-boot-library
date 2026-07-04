package com.library.library.controller.student;

import com.library.library.dto.response.ApiResponse;
import com.library.library.dto.response.BookResponse;
import com.library.library.dto.response.StudentBookResponse;
import com.library.library.service.StudentBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/library/student")
@RestController
@RequiredArgsConstructor
public class StudentController {

    private final StudentBookService studentBookService;

    @GetMapping("/books")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        List<BookResponse> books = studentBookService.getAllBooks();
        return ResponseEntity.ok(ApiResponse.success("Books retrieved successfully", books));
    }

    @GetMapping("/books/search")
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchBooks(
            @RequestParam(required = false) String keyword) {
        List<BookResponse> books = studentBookService.searchBooks(keyword);
        return ResponseEntity.ok(ApiResponse.success("Books retrieved successfully", books));
    }

    @PostMapping("/books/take/{bookId}")
    public ResponseEntity<ApiResponse<StudentBookResponse>> takeBook(
            @PathVariable Long bookId, @RequestParam Long studentId) {
        StudentBookResponse response = studentBookService.takeBook(studentId, bookId);
        return ResponseEntity.ok(ApiResponse.success("Book taken successfully", response));
    }

    @PostMapping("/books/return/{bookId}")
    public ResponseEntity<ApiResponse<StudentBookResponse>> returnBook(
            @PathVariable Long bookId, @RequestParam Long studentId) {
        StudentBookResponse response = studentBookService.returnBook(studentId, bookId);
        return ResponseEntity.ok(ApiResponse.success("Book returned successfully", response));
    }

    @GetMapping("/books/on-hand")
    public ResponseEntity<ApiResponse<List<StudentBookResponse>>> getBooksOnHand(
            @RequestParam Long studentId) {
        List<StudentBookResponse> books = studentBookService.getBooksOnHand(studentId);
        return ResponseEntity.ok(ApiResponse.success("Books on hand retrieved successfully", books));
    }
}
