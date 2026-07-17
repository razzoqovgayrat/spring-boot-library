package com.library.controller;

import com.library.dto.request.BookCopyRequest;
import com.library.dto.request.BookRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.BookCopyResponse;
import com.library.dto.response.BookResponse;
import com.library.dto.response.CreatedResponse;
import com.library.enums.Permission;
import com.library.service.BookCopyService;
import com.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(BookController.BASE_URL)
@RequiredArgsConstructor
@Tag(name = "Book")
@SecurityRequirement(name = "bearerAuth")
public class BookController {
    public static final String BASE_URL = "/books";

    private final BookService bookService;
    private final BookCopyService bookCopyService;

    @Operation(summary = "Yangi kitob qo'shish")
    @PostMapping
    @PreAuthorize("hasAuthority('" + Permission.Fields.BOOK_CREATE + "')")
    public ApiResponse<CreatedResponse> create(@Valid @RequestBody BookRequest request) {
        return bookService.create(request);
    }

    @Operation(summary = "Kitob detali")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.BOOK_READ + "')")
    public ApiResponse<BookResponse> getById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @Operation(summary = "Kitoblarni qidirish")
    @GetMapping
    @PreAuthorize("hasAuthority('" + Permission.Fields.BOOK_READ + "')")
    public ApiResponse<Page<BookResponse>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) String categoryName,
            Pageable pageable
    ) {
        return bookService.search(title, authorName, categoryName, pageable);
    }

    @Operation(summary = "Kitobni yangilash")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.BOOK_UPDATE + "')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        return bookService.update(id, request);
    }

    @Operation(summary = "Kitobni o'chirish")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.BOOK_DELETE + "')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return bookService.delete(id);
    }

    @Operation(summary = "Kitobga nusxa qo'shish")
    @PostMapping("/{bookId}/copies")
    @PreAuthorize("hasAuthority('" + Permission.Fields.BOOK_COPY_CREATE + "')")
    public ApiResponse<CreatedResponse> addCopy(
            @PathVariable Long bookId, @Valid @RequestBody BookCopyRequest request
    ) {
        return bookCopyService.create(bookId, request);
    }

    @Operation(summary = "Kitobning nusxalari")
    @GetMapping("/{bookId}/copies")
    @PreAuthorize("hasAuthority('" + Permission.Fields.BOOK_COPY_READ + "')")
    public ApiResponse<Page<BookCopyResponse>> getCopies(@PathVariable Long bookId, Pageable pageable) {
        return bookCopyService.getByBook(bookId, pageable);
    }
}