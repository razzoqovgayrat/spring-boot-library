package com.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record MemberRequest(

        @Schema(description = "A'zoning to'liq ismi", example = "Vali Valiyev")
        @NotBlank(message = "fullName bo'sh bo'lmasligi kerak")
        @Size(max = 150)
        String fullName,

        @Schema(description = "Unikal a'zolik raqami", example = "MB-2026-0001")
        @NotBlank(message = "membershipNumber bo'sh bo'lmasligi kerak")
        @Size(max = 30)
        String membershipNumber,

        @Schema(description = "Bog'lanadigan User id (ixtiyoriy)", example = "5")
        Long userId
) {
}