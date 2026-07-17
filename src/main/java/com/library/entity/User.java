package com.library.entity;

import com.library.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_username", columnNames = "username")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Override
    @Transient
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getPermissions();
    }

    @Override
    @Transient
    public String getPassword() {
        return passwordHash;
    }

    @Override
    @Transient
    public String getUsername() {
        return username;
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return status != UserStatus.BLOCKED;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}
