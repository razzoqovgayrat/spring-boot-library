package com.library.library.repository;

import com.library.library.entity.StudentBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentBookRepository extends JpaRepository<StudentBook, Long> {
    List<StudentBook> findByStudentId(Long studentId);
}
