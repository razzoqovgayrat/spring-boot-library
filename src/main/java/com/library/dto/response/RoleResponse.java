package com.library.dto.response;

import com.library.enums.Permission;
import lombok.Builder;

import java.util.Set;

@Builder
public record RoleResponse(Long id, String name, Set<Permission> permissions) {
}
