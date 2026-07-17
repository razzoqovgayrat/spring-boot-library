package com.library.dto.response;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        SessionResponse sessionResponse
) {
}
