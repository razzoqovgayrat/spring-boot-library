package com.library.controller;

import com.library.dto.request.UserRequest;
import com.library.dto.request.UserStatusUpdateRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.CreatedResponse;
import com.library.dto.response.UserResponse;
import com.library.enums.Permission;
import com.library.service.UserService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UserController.BASE_URL)
@RequiredArgsConstructor
@Tag(name = "User", description = "Xodimlarni (ADMIN/LIBRARIAN) boshqarish — faqat ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    public static final String BASE_URL = "/users";

    private final UserService userService;

    @Operation(summary = "Joriy foydalanuvchi profili")
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('" + Permission.Fields.USER_READ + "')")
    public ApiResponse<UserResponse> me(Authentication authentication) {
        return userService.getByUsername(authentication.getName());
    }

    @Operation(summary = "Xodim (ADMIN/LIBRARIAN) yaratish")
    @PostMapping
    @PreAuthorize("hasAuthority('" + Permission.Fields.USER_CREATE + "')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreatedResponse> create(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }

    @Operation(summary = "Bitta userni ko'rish")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.USER_READ + "')")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @Operation(summary = "Userlar ro'yxati (pagination + filter)")
    @GetMapping
    @PreAuthorize("hasAuthority('" + Permission.Fields.USER_READ + "')")
    public ApiResponse<Page<UserResponse>> getAll(
            @RequestParam(required = false) String username,
            @ParameterObject Pageable pageable
    ) {
        return userService.getAll(username, pageable);
    }

    @Operation(summary = "Userni yangilash")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.USER_UPDATE + "')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return userService.update(id, request);
    }

    @Operation(summary = "User statusini o'zgartirish (ACTIVE/BLOCKED)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('" + Permission.Fields.USER_UPDATE + "')")
    public ApiResponse<Void> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        return userService.updateStatus(id, request);
    }

    @Operation(summary = "Memberni o'chirish (soft delete)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.USER_DELETE + "')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return userService.delete(id);
    }
}