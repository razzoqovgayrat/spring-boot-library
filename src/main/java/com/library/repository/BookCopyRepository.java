package com.library.repository;

import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.enums.BookCopyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    Page<BookCopy> findAllByBookAndDeletedAtIsNull(Book book, Pageable pageable);

    Optional<BookCopy> findByIdAndDeletedAtIsNull(Long id);

    Optional<BookCopy> findFirstByBookIdAndStatus(Long bookId, BookCopyStatus status);
}
