package com.library.service;

import com.library.dto.request.UserRequest;
import com.library.dto.request.UserStatusUpdateRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.CreatedResponse;
import com.library.dto.response.UserResponse;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.enums.UserStatus;
import com.library.exception.ConflictException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import com.library.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApiResponse<UserResponse> getByUsername(String username) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        return ApiResponse.success(UserResponse.fromEntity(user));
    }

    public ApiResponse<CreatedResponse> create(UserRequest request) {
        if (userRepository.existsByUsernameOrPhoneNumber(request.username(), request.phoneNumber()))
            throw new ConflictException("username or phone already exists");

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("role not found"));

        User newUser = User.builder()
                .fullName(request.fullName())
                .username(request.username())
                .passwordHash(bCryptPasswordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
        newUser.setCreatedAt(Instant.now());
        userRepository.save(newUser);

        return ApiResponse.success("User created", new CreatedResponse(newUser.getId()));
    }

    public ApiResponse<UserResponse> getById(Long id) {
        return ApiResponse.success(UserResponse.fromEntity(userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"))));
    }

    public ApiResponse<Page<UserResponse>> getAll(String username, Pageable pageable) {
        Page<User> users;

        if (username == null || username.isBlank()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findByUsernameContainingIgnoreCase(username, pageable);
        }

        return ApiResponse.success(users.map(UserResponse::fromEntity));
    }

    public ApiResponse<Void> update(Long id, UserRequest request) {

        User user = userRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new UsernameNotFoundException("user not found"));
        if (!request.username().equals(user.getUsername())
                && !request.phoneNumber().equals(user.getPhoneNumber())
                && userRepository.existsByUsernameOrPhoneNumber(request.username(), request.phoneNumber())) {
            throw new ConflictException("username or phone already exists");
        }
        Role role = roleRepository.findById(request.roleId()).orElseThrow(() -> new ResourceNotFoundException("role not found"));

        user.setFullName(request.fullName());
        user.setUsername(request.username());
        user.setPasswordHash(HashUtil.sha256(request.password()));
        user.setPhoneNumber(request.phoneNumber());
        user.setRole(role);
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(customUserDetailsService.getCurrentUser().getUsername());
        userRepository.save(user);

        return ApiResponse.success("User updated");
    }

    public ApiResponse<Void> updateStatus(Long id, UserStatusUpdateRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new UsernameNotFoundException("user not found"));

        user.setStatus(request.status());
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(customUserDetailsService.getCurrentUser().getUsername());
        userRepository.save(user);

        return ApiResponse.success("status updated");
    }

    public ApiResponse<Void> delete(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new UsernameNotFoundException("user not found"));

        user.setDeletedAt(Instant.now());
        userRepository.save(user);

        return ApiResponse.success("User deleted");
    }
}
