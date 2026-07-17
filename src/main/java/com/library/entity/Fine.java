package com.library.entity;

import com.library.enums.FineStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "fines")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Fine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 255)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private FineStatus status = FineStatus.UNPAID;

    // createdAt — BaseEntity'dan meros. Fine ham hech qachon hard-delete
    // qilinmaydi (ledger printsipi), faqat status PAID/WAIVED'ga o'tadi.
}