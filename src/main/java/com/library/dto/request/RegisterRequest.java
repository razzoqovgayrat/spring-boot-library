package com.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Public ro'yxatdan o'tish so'rovi — har doim MEMBER roli bilan yaratiladi")
public record RegisterRequest(

        @Schema(description = "Full Name", example = "Ali Aliyev")
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        String fullName,

        @Schema(example = "ali.aliyev")
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Schema(example = "StrongPass123")
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @Schema(example = "+998901234567")
        String phoneNumber

        // roleId QASDAN yo'q — register() har doim MEMBER role beradi (AuthService ichida, qattiq kodlangan).
        // ADMIN/LIBRARIAN yaratish faqat UserController orqali, ADMIN huquqi bilan.
) {
}