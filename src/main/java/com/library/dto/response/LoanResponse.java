package com.library.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.library.entity.Loan;
import com.library.enums.LoanStatus;
import lombok.Builder;

import java.time.Duration;
import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoanResponse(
        Long id,
        Instant borrowedAt,
        Instant dueDate,
        Instant returnedAt,
        LoanStatus status,
        Long memberId,
        String memberFullName,
        Long copyId,
        String inventoryNumber,
        String bookTitle,
        Integer overdueDays
) {
    public static LoanResponse fromEntity(Loan loan) {
        int overdueDays = 0;
        if (loan.getStatus() == LoanStatus.OVERDUE) {
            long days = Duration.between(loan.getDueDate(), Instant.now()).toDays();
            overdueDays = (int) Math.max(days, 0);
        }

        return LoanResponse.builder()
                .id(loan.getId())
                .borrowedAt(loan.getBorrowedAt())
                .dueDate(loan.getDueDate())
                .returnedAt(loan.getReturnedAt())
                .status(loan.getStatus())
                .memberId(loan.getMember().getId())
                .memberFullName(loan.getMember().getFullName())
                .copyId(loan.getCopy().getId())
                .inventoryNumber(loan.getCopy().getInventoryNumber())
                .bookTitle(loan.getCopy().getBook().getTitle())
                .overdueDays(overdueDays)
                .build();
    }
}