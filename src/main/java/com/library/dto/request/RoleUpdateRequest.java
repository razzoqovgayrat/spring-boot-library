package com.library.dto.request;

import java.util.Set;

public record RoleUpdateRequest(Set<String> permissions) {
}
