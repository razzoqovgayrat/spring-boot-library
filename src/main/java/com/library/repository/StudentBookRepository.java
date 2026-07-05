package com.library.repository;

import com.library.entity.StudentBook;
import com.library.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentBookRepository extends JpaRepository<StudentBook, Long> {
    List<StudentBook> findByStudentId(Long studentId);
    Optional<StudentBook> findByStudentIdAndBookIdAndStatus(Long studentId, Long bookId, BookStatus status);
}
