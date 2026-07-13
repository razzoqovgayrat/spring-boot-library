package com.library.enums;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_ADMIN,
    ROLE_STAFF,
    ROLE_STUDENT;

    @Override
    public @NonNull String getAuthority() {
        return this.name();
    }
}
