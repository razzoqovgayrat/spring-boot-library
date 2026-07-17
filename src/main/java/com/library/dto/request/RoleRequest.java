package com.library.dto.request;

import com.library.enums.Permission;

import java.util.Set;

public record RoleRequest(String name, Set<Permission> permissions) {
}
