package com.library.library.dto.response;

import com.library.library.entity.StudentBook;
import com.library.library.enums.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentBookResponse {

    private Long id;
    private Long studentId;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime takeDate;
    private LocalDateTime returnDate;
    private BookStatus status;

    public static StudentBookResponse fromEntity(StudentBook studentBook) {
        return StudentBookResponse.builder()
                .id(studentBook.getId())
                .studentId(studentBook.getId())
                .bookId(studentBook.getBookId())
                .bookTitle(studentBook.getBookTitle())
                .takeDate(studentBook.getTakenDate())
                .returnDate(studentBook.getReturnedDate())
                .status(studentBook.getStatus())
                .build();
    }
}

