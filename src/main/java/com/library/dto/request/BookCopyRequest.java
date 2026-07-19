package com.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record BookCopyRequest(

        // bookId body'da emas — POST /api/books/{bookId}/copies path'idan keladi
        @Schema(example = "INV-000123")
        @NotBlank(message = "inventoryNumber bo'sh bo'lmasligi kerak")
        String inventoryNumber
) {
}