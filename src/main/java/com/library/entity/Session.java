package com.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Embedded
    private DeviceInfo deviceInfo;
    private String refreshToken;
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt = null;
}
