package com.library.service;

import com.library.dto.request.AddProfileRequest;
import com.library.dto.request.StatusUpdateRequest;
import com.library.dto.response.UserResponse;
import com.library.entity.User;
import com.library.enums.Role;
import com.library.enums.UserStatus;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final List<Role> ROLES = List.of(Role.ROLE_ADMIN, Role.ROLE_STAFF);
    private final UserRepository userRepository;

    public UserResponse addProfile(AddProfileRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }

        if (!(request.getRole() == Role.ROLE_ADMIN || request.getRole() == Role.ROLE_STAFF)) {
            throw new IllegalArgumentException("Role must be ADMIN or STAFF for profile creation");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(request.getPassword())
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .visible(true)
                .build();

        return UserResponse.fromEntity(userRepository.save(user));
    }

    public List<UserResponse> getAllProfiles() {
        return userRepository.findByRoleIn(ROLES).stream()
                .map(UserResponse::fromEntity).toList();
    }

    public List<UserResponse> searchProfiles(String keyword) {
        return userRepository.searchByRoleInAndFullNameContainingIgnoreCase(ROLES, keyword).stream()
                .map(UserResponse::fromEntity).toList();
    }

    public UserResponse changeProfileStatus(Long id, StatusUpdateRequest request) {
        User user = userRepository.findById(id)
                .filter(u -> ROLES.contains(u.getRole()))
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));
        user.setStatus(request.getStatus());
        return UserResponse.fromEntity(userRepository.save(user));
    }

    public UserResponse removeStudent(Long studentId) {
        User user = userRepository.findById(studentId).filter(User::isVisible)
                .filter(user1 -> user1.getRole().equals(Role.ROLE_STUDENT))
                .orElseThrow(() -> new ResourceNotFoundException("student not found with this id or already deleted"));
        user.setVisible(false);
        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    public UserResponse removeProfile(Long userId) {
        User user = userRepository.findById(userId).filter(User::isVisible)
                .filter(user1 -> ROLES.contains(user1.getRole()))
                .orElseThrow(() -> new ResourceNotFoundException("profile not found with this id or already deleted"));
        user.setVisible(false);
        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }
}
