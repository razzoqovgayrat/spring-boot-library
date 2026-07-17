package com.library.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;

    @Schema(description = "Full Name", example = "Ali Aliyev")
    private String fullName;

    @Schema(description = "Username", example = "ali@gmail.com")
    private String username;

    @Schema(description = "Phone Number", example = "+998934445566")
    private String phoneNumber;

    @Schema(description = "User role")
    private RoleResponse role;

    @Schema(description = "User status", example = "ACTIVE")
    private UserStatus status;

    private String accessToken;
    private String refreshToken;
    private SessionResponse sessionResponse;
    private Instant deletedAt;
    private String updatedBy;
    private Instant updatedAt;

    /**
     * Oddiy holatlar uchun: GET /api/users/{id}, GET /api/users kabi.
     * Token/session ma'lumotlari yo'q.
     */
    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .role(toRoleResponse(user.getRole()))
                .status(user.getStatus())
                .deletedAt(user.getDeletedAt())
                .updatedBy(user.getUpdatedBy())
                .updatedAt(user.getUpdatedAt())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    /**
     * Login/refresh javobi uchun: token va session ma'lumotlari bilan birga.
     */
    public static UserResponse fromLogin(User user, String accessToken, String refreshToken) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .role(toRoleResponse(user.getRole()))
                .status(user.getStatus())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private static RoleResponse toRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .permissions(role.getPermissions())
                .build();
    }
}