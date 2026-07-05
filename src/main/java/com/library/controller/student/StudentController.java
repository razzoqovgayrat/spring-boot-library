package com.library.controller.student;

import com.library.dto.response.BookResponse;
import com.library.dto.response.StudentBookResponse;
import com.library.dto.response.UserResponse;
import com.library.service.ProfileService;
import com.library.service.StudentBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/library/student")
@RestController
@RequiredArgsConstructor
public class StudentController {

    private final StudentBookService studentBookService;
    private final ProfileService profileService;

    @GetMapping("/books")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> books = studentBookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search-books")
    public ResponseEntity<List<BookResponse>> searchBooks(@RequestParam(required = false) String keyword) {
        List<BookResponse> books = studentBookService.searchBooks(keyword);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/take-book/{bookId}")
    public ResponseEntity<StudentBookResponse> takeBook(
            @PathVariable Long bookId, @RequestParam Long studentId) {
        StudentBookResponse response = studentBookService.takeBook(studentId, bookId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/return-book/{bookId}")
    public ResponseEntity<StudentBookResponse> returnBook(
            @PathVariable Long bookId, @RequestParam Long studentId) {
        StudentBookResponse response = studentBookService.returnBook(studentId, bookId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/books-on-hand")
    public ResponseEntity<List<StudentBookResponse>> getBooksOnHand(@RequestParam Long studentId) {
        List<StudentBookResponse> books = studentBookService.getBooksOnHand(studentId);
        return ResponseEntity.ok(books);
    }

    @DeleteMapping("/delete-profile/{studentId}")
    public ResponseEntity<UserResponse> deleteProfile(@PathVariable Long studentId) {
        UserResponse response = profileService.removeStudent(studentId);
        return ResponseEntity.ok(response);
    }
}
