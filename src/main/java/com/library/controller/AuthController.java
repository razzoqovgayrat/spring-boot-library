package com.library.controller;

import com.library.dto.request.LogOutRequest;
import com.library.dto.request.LoginRequest;
import com.library.dto.request.RefreshTokenRequest;
import com.library.dto.request.RegisterRequest;
import com.library.dto.response.TokenResponse;
import com.library.dto.response.UserResponse;
import com.library.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping()
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(description = "registration")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(description = "login")
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
        UserResponse response = authService.authorization(loginRequest, httpServletRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "refresh token")
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest, HttpServletRequest httpServletRequest) {
        TokenResponse response = authService.refreshToken(refreshTokenRequest, httpServletRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "login out")
    @GetMapping("/login-out")
    public ResponseEntity<String> loginOut(@Valid @RequestBody LogOutRequest logOutRequest) {
        authService.logout(logOutRequest);
        return ResponseEntity.ok("successfully logout");
    }
}
