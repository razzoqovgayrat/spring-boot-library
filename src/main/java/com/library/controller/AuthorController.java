package com.library.controller;

import com.library.dto.request.AuthorRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.AuthorResponse;
import com.library.dto.response.CreatedResponse;
import com.library.enums.Permission;
import com.library.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AuthorController.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Author")
@SecurityRequirement(name = "bearerAuth")
public class AuthorController {

    public static final String BASE_PATH = "/authors";
    private final AuthorService authorService;

    @Operation(summary = "Yangi muallif qo'shish")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('" + Permission.Fields.AUTHOR_CREATE + "')")
    public ApiResponse<CreatedResponse> create(@Valid @RequestBody AuthorRequest request) {
        return authorService.create(request);
    }

    @Operation(summary = "Muallif detali")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.AUTHOR_READ + "')")
    public ApiResponse<AuthorResponse> getById(@PathVariable Long id) {
        return authorService.getById(id);
    }

    @Operation(summary = "Mualliflar ro'yxati")
    @GetMapping
    @PreAuthorize("hasAuthority('" + Permission.Fields.AUTHOR_READ + "')")
    public ApiResponse<Page<AuthorResponse>> getAll(
            @RequestParam(required = false) String name,
            @ParameterObject Pageable pageable
    ) {
        return authorService.getAll(name, pageable);
    }

    @Operation(summary = "Muallifni yangilash")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.AUTHOR_UPDATE + "')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody AuthorRequest request) {
        return authorService.update(id, request);
    }

    @Operation(summary = "Muallifni o'chirish")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.AUTHOR_DELETE + "')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return authorService.delete(id);
    }
}