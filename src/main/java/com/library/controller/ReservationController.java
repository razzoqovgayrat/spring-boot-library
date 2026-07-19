package com.library.controller;

import com.library.dto.request.ReservationRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.ReservationResponse;
import com.library.enums.Permission;
import com.library.service.ReservationService;
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
@RequestMapping(ReservationController.BASE_URL)
@RequiredArgsConstructor
@Tag(name = "Reservation")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {
    public static final String BASE_URL = "/reservations";

    private final ReservationService reservationService;

    @Operation(summary = "Navbatga yozish")
    @PostMapping
    @PreAuthorize("hasAuthority('" + Permission.Fields.RESERVATION_CREATE + "')")
    public ApiResponse<ReservationResponse> create(@Valid @RequestBody ReservationRequest request) {
        return reservationService.create(request);
    }

    @Operation(summary = "Navbatni bekor qilish")
    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.RESERVATION_DELETE + "')")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        return reservationService.cancel(id);
    }

    @Operation(summary = "Navbat detali")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.RESERVATION_READ + "')")
    public ApiResponse<ReservationResponse> getById(@PathVariable Long id) {
        return reservationService.getById(id);
    }

    @Operation(summary = "A'zoning navbatlari")
    @GetMapping("/members/{memberId}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.RESERVATION_READ + "')")
    public ApiResponse<Page<ReservationResponse>> getByMember(@PathVariable Long memberId, @ParameterObject Pageable pageable) {
        return reservationService.getByMember(memberId, pageable);
    }

    @Operation(summary = "Kitobning navbatlari")
    @GetMapping("/books/{bookId}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.RESERVATION_READ + "')")
    public ApiResponse<Page<ReservationResponse>> getByBook(@PathVariable Long bookId, @ParameterObject Pageable pageable) {
        return reservationService.getByBook(bookId, pageable);
    }
}