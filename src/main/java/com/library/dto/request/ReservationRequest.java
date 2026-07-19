package com.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReservationRequest(

        @Schema(description = "Band qilinayotgan kitob id", example = "7")
        @NotNull(message = "bookId bo'sh bo'lmasligi kerak")
        Long bookId,

        @Schema(description = "A'zo id", example = "5")
        @NotNull(message = "memberId bo'sh bo'lmasligi kerak")
        Long memberId
) {
}