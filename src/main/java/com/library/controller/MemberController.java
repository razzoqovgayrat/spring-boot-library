package com.library.controller;

import com.library.dto.request.MemberRequest;
import com.library.dto.request.MemberStatusUpdateRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.CreatedResponse;
import com.library.dto.response.MemberProfileResponse;
import com.library.dto.response.MemberResponse;
import com.library.enums.Permission;
import com.library.service.MemberService;
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
@RequestMapping(MemberController.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Member")
@SecurityRequirement(name = "bearerAuth")
public class MemberController {

    public static final String BASE_PATH = "/members";
    private final MemberService memberService;

    @Operation(summary = "Yangi a'zo qo'shish")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('" + Permission.Fields.MEMBER_CREATE + "')")
    public ApiResponse<CreatedResponse> create(@RequestParam(required = false) Long userId, @Valid @RequestBody MemberRequest request) {
        return memberService.create(userId, request);
    }

    @Operation(summary = "A'zo detali")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.MEMBER_READ + "')")
    public ApiResponse<MemberResponse>  getById(@PathVariable Long id) {
        return memberService.getById(id);
    }

    @Operation(summary = "A'zolar ro'yxati (filter: fullName, status)")
    @GetMapping
    @PreAuthorize("hasAuthority('" + Permission.Fields.MEMBER_READ + "')")
    public ApiResponse<Page<MemberResponse>> getAll(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String status,
            @ParameterObject Pageable pageable
    ) {
        return memberService.getAll(fullName, status, pageable);
    }

    @Operation(summary = "A'zo ma'lumotlarini yangilash")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.MEMBER_UPDATE + "')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return memberService.update(id, request);
    }

    @Operation(summary = "A'zo statusi (ACTIVE/SUSPENDED) — faqat ADMIN")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('" + Permission.Fields.MEMBER_UPDATE + "')")
    public ApiResponse<Void> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody MemberStatusUpdateRequest request
    ) {
        return memberService.updateStatus(id, request);
    }

    @Operation(summary = "Userni o'chirish (soft delete)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.MEMBER_DELETE + "')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return memberService.delete(id);
    }
}