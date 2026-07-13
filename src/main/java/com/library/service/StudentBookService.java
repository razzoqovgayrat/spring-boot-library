package com.library.service;

import com.library.dto.response.BookResponse;
import com.library.dto.response.StudentBookResponse;
import com.library.entity.Book;
import com.library.entity.StudentBook;
import com.library.entity.User;
import com.library.enums.BookStatus;
import com.library.enums.Role;
import com.library.enums.UserStatus;
import com.library.exception.ResourceNotFoundException;
import com.library.exception.UserBlockedException;
import com.library.repository.BookRepository;
import com.library.repository.StudentBookRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentBookService {

    private final BookRepository bookRepository;
    private final StudentBookRepository studentBookRepository;
    private final UserRepository userRepository;

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .filter(Book::isVisible)
                .map(BookResponse::fromEntity).toList();
    }

    public List<BookResponse> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .filter(Book::isVisible)
                .map(BookResponse::fromEntity).toList();
    }

    public StudentBookResponse takeBook(Long studentId, Long bookId) {
        User student = getActiveStudentOrThrow(studentId);
        Book book = bookRepository.findById(bookId)
                .filter(Book::isVisible)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        StudentBook studentBook = StudentBook.builder()
                .studentId(student.getId())
                .bookId(book.getId())
                .studentName(student.getFullName())
                .bookTitle(book.getTitle())
                .takenDate(LocalDateTime.now())
                .status(BookStatus.TAKEN)
                .build();

        StudentBook saved = studentBookRepository.save(studentBook);
        return StudentBookResponse.fromEntity(saved);
    }

    public StudentBookResponse returnBook(Long studentId, Long bookId) {
        getActiveStudentOrThrow(studentId);

        StudentBook studentBook = studentBookRepository
                .findByStudentIdAndBookIdAndStatus(studentId, bookId, BookStatus.TAKEN)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active borrow record found for this student and book"));

        studentBook.setStatus(BookStatus.RETURNED);
        studentBook.setReturnedDate(LocalDateTime.now());
        studentBookRepository.save(studentBook);

        return StudentBookResponse.fromEntity(studentBook);
    }

    public List<StudentBookResponse> getBooksOnHand(Long studentId) {
        return studentBookRepository.findByStudentId(studentId).stream()
                .filter(studentBook -> studentBook.getStatus().equals(BookStatus.TAKEN))
                .map(StudentBookResponse::fromEntity).toList();
    }

    private User getActiveStudentOrThrow(Long studentId) {
        User student = userRepository.findById(studentId)
                .filter(u -> u.getRole() == Role.ROLE_STUDENT && u.isVisible())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (student.getStatus() == UserStatus.BLOCKED) {
            throw new UserBlockedException("Blocked students cannot take or return books");
        }

        return student;
    }
}
