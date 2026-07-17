package com.library.controller;

import com.library.dto.request.RoleRequest;
import com.library.dto.request.RoleUpdateRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.CreatedResponse;
import com.library.dto.response.RoleResponse;
import com.library.enums.Permission;
import com.library.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(RoleController.BASE_URL)
@RestController
@RequiredArgsConstructor
@Tag(name = "Role", description = "Role larni boshqarish permission Required")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {
    public static final String BASE_URL = "/roles";

    private final RoleService roleService;

    @Operation(summary = "create role")
    @PostMapping
    @PreAuthorize("hasAuthority('" + Permission.Fields.ROLE_CREATE + "')")
    public ApiResponse<CreatedResponse> create(@RequestBody RoleRequest roleRequest) {
        return roleService.create(roleRequest);
    }

    @Operation(summary = "all roles")
    @GetMapping
    @PreAuthorize("hasAuthority('" + Permission.Fields.ROLE_READ + "')")
    public ApiResponse<List<RoleResponse>> all() {
        return roleService.all();
    }

    @Operation(summary = "add permission to role")
    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('" + Permission.Fields.ROLE_UPDATE + "')")
    public ApiResponse<Void> addPermission(@PathVariable Long roleId, @RequestBody RoleUpdateRequest roleUpdateRequest) {
        return roleService.addPermission(roleId, roleUpdateRequest);
    }

    @Operation(summary = "subtract permission from role")
    @DeleteMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('" + Permission.Fields.ROLE_UPDATE + "')")
    public ApiResponse<Void> subtractPermission(@PathVariable Long roleId, @RequestBody RoleUpdateRequest roleUpdateRequest) {
        return roleService.subtractPermission(roleId, roleUpdateRequest);
    }

    @Operation(summary = "delete role")
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('" + Permission.Fields.ROLE_DELETE + "')")
    public ApiResponse<Void> delete(@PathVariable Long roleId) {
        return roleService.delete(roleId);
    }
}
