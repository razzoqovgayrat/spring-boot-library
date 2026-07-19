package com.library.dto.response;

import com.library.entity.Fine;
import com.library.enums.FineStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record FineResponse(
        Long id,
        BigDecimal amount,
        String reason,
        FineStatus status,
        Instant createdAt,
        Long loanId,
        Long memberId,
        String memberFullName
) {
    public static FineResponse fromEntity(Fine fine) {
        return FineResponse.builder()
                .id(fine.getId())
                .amount(fine.getAmount())
                .reason(fine.getReason())
                .status(fine.getStatus())
                .createdAt(fine.getCreatedAt())
                .loanId(fine.getLoan().getId())
                .memberId(fine.getMember().getId())
                .memberFullName(fine.getMember().getFullName())
                .build();
    }
}