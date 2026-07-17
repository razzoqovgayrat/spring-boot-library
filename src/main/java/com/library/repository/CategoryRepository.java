package com.library.repository;

import com.library.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameContainingIgnoreCaseAndDeletedAtIsNull(String name);

    Optional<Category> findByIdAndDeletedAtIsNull(Long id);
}
