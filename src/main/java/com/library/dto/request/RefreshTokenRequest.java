package com.library.dto.request;

public record RefreshTokenRequest(String refreshToken, String deviceId) {
}
