package com.library.dto.response;

import com.library.entity.Reservation;
import com.library.enums.ReservationStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ReservationResponse(
        Long id,
        ReservationStatus status,
        Instant createdAt,
        Instant readyUntil,
        Long bookId,
        String bookTitle,
        Long memberId,
        String memberFullName,
        // faqat WAITING holatida ma'noli, service tomonidan hisoblanadi va beriladi
        Integer queuePosition
) {
    public static ReservationResponse fromEntity(Reservation reservation, Integer queuePosition) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .readyUntil(reservation.getReadyUntil())
                .bookId(reservation.getBook().getId())
                .bookTitle(reservation.getBook().getTitle())
                .memberId(reservation.getMember().getId())
                .memberFullName(reservation.getMember().getFullName())
                .queuePosition(queuePosition)
                .build();
    }
}