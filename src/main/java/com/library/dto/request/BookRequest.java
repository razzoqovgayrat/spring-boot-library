package com.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.Set;

@Builder
public record BookRequest(

        @Schema(example = "O'tkan kunlar")
        @NotBlank(message = "title bo'sh bo'lmasligi kerak")
        String title,

        @Schema(example = "978-9943-05-123-4")
        @NotBlank(message = "isbn bo'sh bo'lmasligi kerak")
        String isbn,

        @Schema(example = "O'zbek adabiyotining birinchi romani")
        String description,

        @Schema(example = "1925")
        @Positive(message = "publishedYear musbat son bo'lishi kerak")
        Integer publishedYear,

        @Schema(description = "Muallif id'lari", example = "[1, 2]")
        @NotEmpty(message = "kamida bitta muallif tanlanishi kerak")
        Set<Long> authorIds,

        @Schema(description = "Kategoriya id'lari", example = "[3]")
        @NotEmpty(message = "kamida bitta kategoriya tanlanishi kerak")
        Set<Long> categoryIds
) {
}