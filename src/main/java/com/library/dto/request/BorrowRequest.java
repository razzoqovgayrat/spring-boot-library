package com.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BorrowRequest(

        @Schema(description = "A'zo id", example = "5")
        @NotNull(message = "memberId bo'sh bo'lmasligi kerak")
        Long memberId,

        @Schema(description = "Ijaraga olinayotgan nusxa id", example = "12")
        @NotNull(message = "copyId bo'sh bo'lmasligi kerak")
        Long copyId
) {
}