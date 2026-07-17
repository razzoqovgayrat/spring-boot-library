package com.library.dto.request;

import com.library.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserStatusUpdateRequest(

        @Schema(description = "Yangi status", example = "BLOCKED")
        @NotNull(message = "status bo'sh bo'lmasligi kerak")
        UserStatus status
) {
}