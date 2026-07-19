package com.library.dto.response;

import com.library.entity.Author;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AuthorResponse(
        Long id,
        String fullName,
        String bio,
        @Schema(description = "Shu muallifning nechta kitobi bor")
        Integer bookCount
) {
    /**
     * Diqqat: author.getBooks().size() LAZY collection'ni yuklaydi (N+1 xavfi).
     * Ro'yxat endpoint'ida bu metodni ishlatmang — Service qatlamida
     * bitta agregat query (COUNT + GROUP BY) bilan bookCount hisoblang.
     *
     */
    public static AuthorResponse fromEntity(Author author, int bookCount) {
        return AuthorResponse.builder()
                .id(author.getId())
                .fullName(author.getFullName())
                .bio(author.getBio())
                .bookCount(bookCount)
                .build();
    }
}