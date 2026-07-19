package com.library.controller;

import com.library.dto.request.BookCopyStatusUpdateRequest;
import com.library.dto.response.ApiResponse;
import com.library.enums.Permission;
import com.library.service.BookCopyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(BookCopyController.BASE_URL)
@RequiredArgsConstructor
@Tag(name = "BookCopy")
@SecurityRequirement(name = "bearerAuth")
public class BookCopyController {
    public static final String BASE_URL = "/copies";
    private final BookCopyService bookCopyService;

    @Operation(summary = "Nusxa statusini o'zgartirish")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('" + Permission.Fields.BOOK_COPY_UPDATE + "')")
    public ApiResponse<Void> updateStatus(
            @PathVariable Long id, @Valid @RequestBody BookCopyStatusUpdateRequest request
    ) {
        return bookCopyService.updateStatus(id, request);
    }

    @Operation(summary = "Nusxani o'chirish")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.BOOK_COPY_DELETE + "')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return bookCopyService.delete(id);
    }
}