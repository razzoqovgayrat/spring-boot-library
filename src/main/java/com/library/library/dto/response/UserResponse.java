package com.library.library.dto.response;

import com.library.library.enums.Role;
import com.library.library.enums.UserStatus;
import com.library.library.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String fullName;
    private String username;
    private Role role;
    private UserStatus status;

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
