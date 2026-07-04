package com.library.library.service;

import com.library.library.dto.request.AddProfileRequest;
import com.library.library.dto.request.StatusUpdateRequest;
import com.library.library.dto.response.UserResponse;
import com.library.library.entity.User;
import com.library.library.enums.Role;
import com.library.library.enums.UserStatus;
import com.library.library.exception.DuplicateResourceException;
import com.library.library.exception.ResourceNotFoundException;
import com.library.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final List<Role> ROLES = List.of(Role.ADMIN, Role.STAFF);
    private final UserRepository userRepository;

    public List<UserResponse> getAllProfiles() {
        return userRepository.findByRoleIn(ROLES).stream()
                .map(UserResponse::fromEntity).toList();
    }

    public List<UserResponse> searchProfiles(String keyword) {
        return userRepository.findByRoleInAndFullName(ROLES, keyword).stream()
                .map(UserResponse::fromEntity).toList();
    }

    public UserResponse addProfile(AddProfileRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }

        if (!(request.getRole() == Role.ADMIN || request.getRole() == Role.STAFF)) {
            throw new IllegalArgumentException("Role must be ADMIN or STAFF for profile creation");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(request.getPassword())
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .build();

        return UserResponse.fromEntity(userRepository.save(user));
    }

    public UserResponse changeProfileStatus(Long id, StatusUpdateRequest request) {
        User user = userRepository.findById(id)
                .filter(u -> ROLES.contains(u.getRole()))
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));
        user.setStatus(request.getStatus());
        return UserResponse.fromEntity(userRepository.save(user));
    }
}
