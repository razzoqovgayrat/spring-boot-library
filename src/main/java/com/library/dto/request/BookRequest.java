package com.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Book info")
public class BookRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    @Schema(description = "Book title", example = "Java")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 150, message = "Author must be at most 150 characters")
    @Schema(description = "Book author", example = "Vali Valiyev")
    private String author;

    @Schema(description = "Book category", example = "Dasturlash")
    private String category;
}
