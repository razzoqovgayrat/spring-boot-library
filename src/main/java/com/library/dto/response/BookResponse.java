package com.library.dto.response;

import com.library.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private Long id;
    @Schema(description = "Book title", example = "Java")
    private String title;
    @Schema(description = "Book author", example = "Ali Aliyev")
    private String author;
    @Schema(description = "Book category", example = "Dasturlash")
    private String category;
    private boolean visible;

    public static BookResponse fromEntity(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .category(book.getCategory())
                .visible(book.isVisible())
                .build();
    }
}
