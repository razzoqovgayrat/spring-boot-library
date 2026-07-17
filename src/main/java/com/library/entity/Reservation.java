package com.library.entity;

import com.library.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.WAITING;

    @Column(name = "ready_until")
    private Instant readyUntil;

    // "createdAt" alohida yozilmadi — BaseEntity'dagi @CreatedDate
    // (createdAt) talabnomadagi "createdAt"ning o'zi, meros orqali keladi.
}