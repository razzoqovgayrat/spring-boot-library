package com.library.controller;

import com.library.dto.request.LoginRequest;
import com.library.dto.request.LogoutRequest;
import com.library.dto.request.RefreshTokenRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.RefreshTokenResponse;
import com.library.dto.response.UserResponse;
import com.library.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AuthController.BASE_URL)
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Ro'yxatdan o'tish, login, token yangilash, logout")
public class AuthController {

    public final static String BASE_URL = "/auth";
    private final AuthService authService;

    @Operation(summary = "Login — access va refresh token qaytaradi")
    @PostMapping("/login")
    public ApiResponse<UserResponse> login(
            @Valid @RequestBody LoginRequest request,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent
    ) {
        String deviceName = userAgent != null ? userAgent : "Unknown device";
        return authService.login(request, deviceName);
    }

    @Operation(summary = "Refresh token orqali yangi access token olish")
    @PostMapping("/refresh")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @Operation(summary = "Joriy qurilmadan chiqish")
    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Void> logout(
            Authentication authentication,
            @Valid @RequestBody LogoutRequest request
    ) {
        return authService.logout(authentication.getName(), request);
    }

    @Operation(summary = "Barcha qurilmalardan chiqish")
    @PostMapping("/logout-all")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Void> logoutAll(Authentication authentication) {
        return authService.logoutAll(authentication.getName());
    }
}