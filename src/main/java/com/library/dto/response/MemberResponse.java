package com.library.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.library.entity.Member;
import com.library.enums.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MemberResponse(

        Long id,

        @Schema(example = "Vali Valiyev")
        String fullName,

        @Schema(example = "MB-2026-0001")
        String membershipNumber,

        @Schema(example = "ACTIVE")
        MemberStatus status,

        LocalDate joinedAt,

        @Schema(description = "Hozirgi aktiv ijaralar soni", example = "2")
        Integer activeLoanCount,

        @Schema(description = "To'lanmagan jami jarima summasi", example = "15000")
        java.math.BigDecimal unpaidFineTotal
) {

    // asosiy field'lardan tuzilgan oddiy javob (list/detail uchun)
    public static MemberResponse fromEntity(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .fullName(member.getFullName())
                .membershipNumber(member.getMembershipNumber())
                .status(member.getStatus())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}