package com.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AuthorRequest(

        @Schema(example = "Abdulla Qodiriy")
        @NotBlank(message = "fullName bo'sh bo'lmasligi kerak")
        @Size(max = 150)
        String fullName,

        @Schema(example = "O'zbek yozuvchisi, 'O'tkan kunlar' muallifi")
        @Size(max = 2000)
        String bio
) {
}