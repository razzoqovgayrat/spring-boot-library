package com.library.dto.request;

import com.library.enums.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MemberStatusUpdateRequest(

        @Schema(description = "Yangi status", example = "SUSPENDED")
        @NotNull(message = "status bo'sh bo'lmasligi kerak")
        MemberStatus status
) {
}