package com.library.entity;

import com.library.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "loans")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Loan extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "copy_id", nullable = false)
    private BookCopy copy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "borrowed_at", nullable = false)
    private Instant borrowedAt;

    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @Column(name = "returned_at")
    private Instant returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LoanStatus status = LoanStatus.ACTIVE;

    // Talabnoma: "Loan hech qachon o'chirilmaydi, faqat status o'zgaradi" —
    // shuning uchun bu entity'da hard-delete uchun hech qanday endpoint/metod
    // yozilmaydi (Service/Controller darajasida ham DELETE bo'lmaydi).
}