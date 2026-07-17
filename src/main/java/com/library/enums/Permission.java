package com.library.enums;

import lombok.experimental.FieldNameConstants;
import org.springframework.security.core.GrantedAuthority;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum Permission implements GrantedAuthority {
    @FieldNameConstants.Include USER_CREATE,
    @FieldNameConstants.Include USER_READ,
    @FieldNameConstants.Include USER_UPDATE,
    @FieldNameConstants.Include USER_DELETE,

    @FieldNameConstants.Include AUTHOR_CREATE,
    @FieldNameConstants.Include AUTHOR_READ,
    @FieldNameConstants.Include AUTHOR_UPDATE,
    @FieldNameConstants.Include AUTHOR_DELETE,

    @FieldNameConstants.Include CATEGORY_CREATE,
    @FieldNameConstants.Include CATEGORY_READ,
    @FieldNameConstants.Include CATEGORY_UPDATE,
    @FieldNameConstants.Include CATEGORY_DELETE,

    @FieldNameConstants.Include BOOK_CREATE,
    @FieldNameConstants.Include BOOK_READ,
    @FieldNameConstants.Include BOOK_UPDATE,
    @FieldNameConstants.Include BOOK_DELETE,

    @FieldNameConstants.Include ROLE_CREATE,
    @FieldNameConstants.Include ROLE_UPDATE,
    @FieldNameConstants.Include ROLE_READ,
    @FieldNameConstants.Include ROLE_DELETE,

    @FieldNameConstants.Include BOOK_COPY_CREATE,
    @FieldNameConstants.Include BOOK_COPY_UPDATE,
    @FieldNameConstants.Include BOOK_COPY_READ,
    @FieldNameConstants.Include BOOK_COPY_DELETE,

    @FieldNameConstants.Include MEMBER_CREATE,
    @FieldNameConstants.Include MEMBER_UPDATE,
    @FieldNameConstants.Include MEMBER_READ,
    @FieldNameConstants.Include MEMBER_DELETE,

    @FieldNameConstants.Include LOAN_CREATE,
    @FieldNameConstants.Include LOAN_UPDATE,
    @FieldNameConstants.Include LOAN_READ,
    @FieldNameConstants.Include LOAN_DELETE,

    @FieldNameConstants.Include FINE_CREATE,
    @FieldNameConstants.Include FINE_UPDATE,
    @FieldNameConstants.Include FINE_READ,
    @FieldNameConstants.Include FINE_DELETE,

    @FieldNameConstants.Include RESERVATION_CREATE,
    @FieldNameConstants.Include RESERVATION_UPDATE,
    @FieldNameConstants.Include RESERVATION_READ,
    @FieldNameConstants.Include RESERVATION_DELETE;

    @Override
    public String getAuthority() {
        return this.name();
    }
}