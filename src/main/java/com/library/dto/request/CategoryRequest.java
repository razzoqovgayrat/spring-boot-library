package com.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CategoryRequest(

        @Schema(example = "Roman")
        @NotBlank(message = "name bo'sh bo'lmasligi kerak")
        @Size(max = 100)
        String name
) {
}