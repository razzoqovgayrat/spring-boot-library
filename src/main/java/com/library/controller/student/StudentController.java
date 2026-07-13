package com.library.controller.student;

import com.library.dto.response.BookResponse;
import com.library.dto.response.StudentBookResponse;
import com.library.dto.response.UserResponse;
import com.library.service.ProfileService;
import com.library.service.StudentBookService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/student")
@RestController
@RequiredArgsConstructor
public class StudentController {

    private final StudentBookService studentBookService;
    private final ProfileService profileService;

    @GetMapping()
    @Operation(summary = "all books")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> books = studentBookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search")
    @Operation(summary = "search by name")
    public ResponseEntity<List<BookResponse>> searchBooks(@RequestParam(required = false) String keyword) {
        List<BookResponse> books = studentBookService.searchBooks(keyword);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/")
    @Operation(summary = "take book")
    public ResponseEntity<StudentBookResponse> takeBook(
            @RequestParam Long bookId, @RequestParam Long studentId) {
        StudentBookResponse response = studentBookService.takeBook(studentId, bookId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{bookId}")
    @Operation(summary = "return book by id")
    public ResponseEntity<StudentBookResponse> returnBook(
            @PathVariable Long bookId, @RequestParam Long studentId) {
        StudentBookResponse response = studentBookService.returnBook(studentId, bookId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/on-hand")
    @Operation(summary = "books on hand")
    public ResponseEntity<List<StudentBookResponse>> getBooksOnHand(@RequestParam Long studentId) {
        List<StudentBookResponse> books = studentBookService.getBooksOnHand(studentId);
        return ResponseEntity.ok(books);
    }

    @DeleteMapping("/{studentId}")
    @Operation(summary = "delete student by id")
    public ResponseEntity<UserResponse> deleteProfile(@PathVariable Long studentId) {
        UserResponse response = profileService.removeStudent(studentId);
        return ResponseEntity.ok(response);
    }
}
