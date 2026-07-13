package com.library.dto.response;

import java.time.LocalDateTime;

public record SessionResponse(String deviceInfo, LocalDateTime lastUsedAt) {
}
