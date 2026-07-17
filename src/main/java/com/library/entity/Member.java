package com.library.entity;

import com.library.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "members", uniqueConstraints = {
        @UniqueConstraint(name = "uk_members_membership_number", columnNames = "membership_number")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "membership_number", nullable = false, unique = true, length = 30)
    private String membershipNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column(name = "joined_at", nullable = false)
    private LocalDate joinedAt;

    // Har bir Member majburiy emas — tizimga kira oladigan User bilan bog'lanishi mumkin (ixtiyoriy)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}