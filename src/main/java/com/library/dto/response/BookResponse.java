package com.library.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record BookResponse(
        Long id,
        String title,
        String isbn,
        String description,
        Integer publishedYear,
        List<AuthorResponse> authors,
        List<CategoryResponse> categories,
        Integer totalCopies,
        Integer availableCopies
) {
    // fromEntity() atayin yozilmadi: authors/categories -> AuthorResponse/CategoryResponse
    // konversiyasi, va totalCopies/availableCopies hisoblash uchun bookCopyRepository'ga
    // murojaat kerak — bularning barchasi BookService ichida (mapper orqali) yig'iladi,
    // entity-only holda to'g'ri hisoblab bo'lmaydi.
}