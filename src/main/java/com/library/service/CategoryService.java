package com.library.service;

import com.library.dto.request.CategoryRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.AuthorResponse;
import com.library.dto.response.CategoryResponse;
import com.library.dto.response.CreatedResponse;
import com.library.entity.Author;
import com.library.entity.Category;
import com.library.exception.ConflictException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public ApiResponse<CreatedResponse> create(CategoryRequest request) {
        if (categoryRepository.existsByNameContainingIgnoreCaseAndDeletedAtIsNull(request.name())) {
            throw new ConflictException("this category already exists");
        }

        Category category = Category.builder().name(request.name()).build();
        category.setCreatedAt(Instant.now());
        categoryRepository.save(category);
        return ApiResponse.success(new CreatedResponse(category.getId()));
    }

    @Transactional
    public ApiResponse<CategoryResponse> getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("author not found"));

        return ApiResponse.success(CategoryResponse.fromEntity(category, category.getBooks().size()));
    }

    @Transactional
    public ApiResponse<List<CategoryResponse>> getAll() {
        return ApiResponse.success(categoryRepository.findAll().stream()
                .map(category -> CategoryResponse.fromEntity(category, category.getBooks().size()))
                .toList());
    }

    public ApiResponse<Void> update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setName(request.name());
        category.setUpdatedAt(Instant.now());
        category.setUpdatedBy(customUserDetailsService.getCurrentUser().getUsername());
        categoryRepository.save(category);
        return ApiResponse.success("successfully updated");
    }

    public ApiResponse<Void> delete(Long id) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setDeletedAt(Instant.now());
        categoryRepository.save(category);
        return ApiResponse.success("successfully deleted");
    }
}
