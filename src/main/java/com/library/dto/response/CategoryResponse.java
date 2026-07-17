package com.library.dto.response;

import com.library.entity.Category;
import lombok.Builder;

@Builder
public record CategoryResponse(
        Long id,
        String name,
        Integer bookCount
) {
    public static CategoryResponse fromEntity(Category category, int bookCount) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .bookCount(bookCount)
                .build();
    }
}