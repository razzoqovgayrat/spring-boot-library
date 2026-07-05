package com.library.entity;

import com.library.enums.BookStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "student_books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "book_title")
    private String bookTitle;

    @Column(name = "taken_date")
    private LocalDateTime takenDate;

    @Column(name = "returned_date")
    private LocalDateTime returnedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookStatus status;
}
