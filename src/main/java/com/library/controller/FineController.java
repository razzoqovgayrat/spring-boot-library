package com.library.controller;

import com.library.dto.request.FineWaiveRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.FineResponse;
import com.library.enums.Permission;
import com.library.service.FineService;
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
@RequestMapping(FineController.BASE_URL)
@RequiredArgsConstructor
@Tag(name = "Fine")
@SecurityRequirement(name = "bearerAuth")
public class FineController {
    public static final String BASE_URL = "/fines";

    private final FineService fineService;

    @Operation(summary = "Barcha jarimalar")
    @GetMapping
    @PreAuthorize("hasAuthority('" + Permission.Fields.FINE_READ + "')")
    public ApiResponse<Page<FineResponse>> getAll(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String status,
            @ParameterObject Pageable pageable
    ) {
        return fineService.getAll(memberId, status, pageable);
    }

    @Operation(summary = "Jarimani to'lash")
    @PostMapping("/{id}/pay")
    @PreAuthorize("hasAuthority('" + Permission.Fields.FINE_PAY + "')")
    public ApiResponse<FineResponse> pay(@PathVariable Long id) {
        return fineService.pay(id);
    }

    @Operation(summary = "Jarimani kechirish")
    @PostMapping("/{id}/waive")
    @PreAuthorize("hasAuthority('" + Permission.Fields.FINE_UPDATE + "')")
    public ApiResponse<FineResponse> waive(@PathVariable Long id, @Valid @RequestBody FineWaiveRequest request) {
        return fineService.waive(id, request);
    }
}