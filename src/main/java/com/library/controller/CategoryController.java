package com.library.controller;

import com.library.dto.request.CategoryRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.CategoryResponse;
import com.library.dto.response.CreatedResponse;
import com.library.enums.Permission;
import com.library.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(CategoryController.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Category")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    public static final String BASE_PATH = "/categories";
    private final CategoryService categoryService;

    @Operation(summary = "Yangi kategoriya qo'shish")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('" + Permission.Fields.CATEGORY_CREATE + "')")
    public ApiResponse<CreatedResponse> create(@Valid @RequestBody CategoryRequest request) {
        return categoryService.create(request);
    }

    @Operation(summary = "Category detali")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.CATEGORY_READ + "')")
    public ApiResponse<CategoryResponse> getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @Operation(summary = "Barcha kategoriyalar")
    @GetMapping
    @PreAuthorize("hasAuthority('" + Permission.Fields.CATEGORY_READ + "')")
    public ApiResponse<List<CategoryResponse>> getAll() {
        return categoryService.getAll();
    }

    @Operation(summary = "Kategoriyani yangilash")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.CATEGORY_UPDATE + "')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @Operation(summary = "Kategoriyani o'chirish")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.CATEGORY_DELETE + "')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return categoryService.delete(id);
    }
}