package com.library.library.service;

import com.library.library.dto.request.LoginRequest;
import com.library.library.dto.request.RegisterRequest;
import com.library.library.dto.response.UserResponse;
import com.library.library.entity.User;
import com.library.library.enums.UserStatus;
import com.library.library.exception.DuplicateResourceException;
import com.library.library.exception.InvalidCredentialsException;
import com.library.library.exception.UserBlockedException;
import com.library.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(request.getPassword())
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    public UserResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UserBlockedException("Your account has been blocked. Please contact the administrator");
        }

        return UserResponse.fromEntity(user);
    }
}
