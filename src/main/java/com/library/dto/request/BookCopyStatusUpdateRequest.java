package com.library.dto.request;

import com.library.enums.BookCopyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BookCopyStatusUpdateRequest(

        // Diqqat: bu yerga faqat LOST/MAINTENANCE/AVAILABLE kelishi kerak.
        // BORROWED/RESERVED — bular faqat Loan/Reservation service'lari
        // tomonidan ICHKARIDAN o'zgartiriladi, bu endpoint orqali emas.
        // Service qatlamida shu ikkalasi kiritilsa 400 qaytariladi.
        @Schema(example = "MAINTENANCE")
        @NotNull(message = "status bo'sh bo'lmasligi kerak")
        BookCopyStatus status
) {
}