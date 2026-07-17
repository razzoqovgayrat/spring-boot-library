package com.library.repository;

import com.library.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Page<Author> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    Page<Author> findAll(Pageable pageable);

    Optional<Author> findByIdAndDeletedAtIsNull(Long id);
}
