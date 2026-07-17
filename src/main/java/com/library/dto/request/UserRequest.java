package com.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "ADMIN tomonidan xodim (ADMIN/LIBRARIAN) yaratish/yangilash uchun so'rov")
public record UserRequest(

        @Schema(example = "Ali Aliyev")
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100)
        String fullName,

        @Schema(example = "ali.librarian")
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50)
        String username,

        // Faqat CREATE paytida majburiy. UPDATE'da bo'sh kelsa — parol o'zgartirilmaydi
        // (buni Service qatlamida tekshirasiz: agar null/bo'sh bo'lsa, eski passwordHash saqlanadi).
        @Schema(example = "StrongPass123")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @Schema(example = "+998901234567")
        String phoneNumber,

        @Schema(description = "Berilishi kerak bo'lgan role id (ADMIN yoki LIBRARIAN)", example = "2")
        @NotNull(message = "roleId is required")
        Long roleId
) {
}