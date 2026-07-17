package com.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "sessions",
        indexes = {
                @Index(name = "idx_sessions_device_id_revoked_at", columnList = "device_id, revoked_at")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Embedded
    private DeviceInfo deviceInfo;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "last_used_at", nullable = false)
    private LocalDateTime lastUsedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Transient
    public boolean isActive() {
        return revokedAt == null;
    }
}