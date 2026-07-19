package com.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record FineWaiveRequest(

        @Schema(description = "Nega kechirilayotgani", example = "Kitob zararlanmagan holda topildi")
        @NotBlank(message = "reason bo'sh bo'lmasligi kerak")
        String reason
) {
}