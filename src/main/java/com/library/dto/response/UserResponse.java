package com.library.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.library.entity.User;
import com.library.enums.Role;
import com.library.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    @Schema(description = "Full Name", example = "Ali Aliyev")
    private String fullName;
    @Schema(description = "username", example = "example@gmail.com")
    private String username;
    @Schema(description = "User role", example = "STAFF")
    private Role role;
    @Schema(description = "User status", example = "ACTIVE")
    private UserStatus status;
    private String accessToken;
    private String refreshToken;
    private SessionResponse sessionResponse;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
