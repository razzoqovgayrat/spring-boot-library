package com.library.controller;

import com.library.dto.request.BorrowRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.CreatedResponse;
import com.library.dto.response.LoanResponse;
import com.library.enums.Permission;
import com.library.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(LoanController.BASE_URL)
@RequiredArgsConstructor
@Tag(name = "Loan")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {
    public static final String BASE_URL = "/loans";

    private final LoanService loanService;

    @Operation(summary = "Ijaraga berish")
    @PostMapping
    @PreAuthorize("hasAuthority('" + Permission.Fields.LOAN_CREATE + "')")
    public ApiResponse<CreatedResponse> borrow(@Valid @RequestBody BorrowRequest request) {
        return loanService.borrow(request);
    }

    @Operation(summary = "Qaytarish")
    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.LOAN_DELETE + "')")
    public ApiResponse<Void> returnLoan(@PathVariable Long id) {
        return loanService.returnLoan(id);
    }


    @Operation(summary = "Kitob yo'qolgan deb belgilash")
    @PostMapping("/{id}/mark-lost")
    @PreAuthorize("hasAuthority('" + Permission.Fields.LOAN_UPDATE + "')")
    public ApiResponse<LoanResponse> markAsLost(@PathVariable Long id) {
        return loanService.markAsLost(id);
    }

    @Operation(summary = "Ijara detali")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.LOAN_READ + "')")
    public ApiResponse<LoanResponse> getById(@PathVariable Long id) {
        return loanService.getById(id);
    }

    @Operation(summary = "Barcha ijaralar")
    @GetMapping
    @PreAuthorize("hasAuthority('" + Permission.Fields.LOAN_READ + "')")
    public ApiResponse<Page<LoanResponse>> getAll(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String status,
            @ParameterObject Pageable pageable
    ) {
        return loanService.getAll(memberId, status, pageable);
    }
}