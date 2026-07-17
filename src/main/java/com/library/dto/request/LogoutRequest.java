package com.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LogoutRequest(

        @Schema(example = "device-uuid-1234")
        @NotBlank(message = "deviceId bo'sh bo'lmasligi kerak")
        String deviceId
) {
}