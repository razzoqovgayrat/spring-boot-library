package com.library.service;

import com.library.dto.request.RoleRequest;
import com.library.dto.request.RoleUpdateRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.CreatedResponse;
import com.library.dto.response.RoleResponse;
import com.library.entity.Role;
import com.library.enums.Permission;
import com.library.exception.AppBadRequestException;
import com.library.exception.ConflictException;
import com.library.exception.InvalidCredentialsException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public ApiResponse<CreatedResponse> create(RoleRequest roleRequest) {
        Role role = roleRepository.findByName(roleRequest.name()).orElseGet(Role::new);

        role.setName(roleRequest.name());
        role.setPermissions(roleRequest.permissions());
        roleRepository.save(role);

        return ApiResponse.success(new CreatedResponse(role.getId()));
    }

    public ApiResponse<List<RoleResponse>> all() {
        return ApiResponse.success(roleRepository.findAll().stream()
                .map(role -> RoleResponse.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .permissions(role.getPermissions())
                        .build())
                .toList());
    }

    @Transactional
    public ApiResponse<Void> addPermission(Long roleId, RoleUpdateRequest roleRequest) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("role not found"));
        Set<Permission> allPermissions = Arrays.stream(Permission.values()).collect(Collectors.toSet());
        Set<Permission> validPermissions = getValidPermission(roleRequest.permissions(), allPermissions);

        if (validPermissions.isEmpty()) {
            throw new AppBadRequestException("No valid permissions given");
        }
        role.getPermissions().addAll(validPermissions);
        roleRepository.save(role);

        return ApiResponse.success("successfully updated");
    }

    public ApiResponse<Void> subtractPermission(Long roleId, RoleUpdateRequest roleRequest) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("role not found"));

        if (role.getPermissions().isEmpty()) {
            throw new InvalidCredentialsException("Permission not found in this role");
        }

        Set<Permission> validPermissions = getValidPermission(roleRequest.permissions(), role.getPermissions());
        if (validPermissions.isEmpty()) {
            throw new AppBadRequestException("No valid permissions given");
        }
        role.getPermissions().removeAll(validPermissions);
        roleRepository.save(role);

        return ApiResponse.success("successfully updated");
    }

    @Transactional
    public ApiResponse<Void> delete(Long roleId) {
        if (userRepository.existsByRoleId(roleId)) {
            throw new ConflictException(
                    "Cannot delete role because it is assigned to existing users.");
        }
        roleRepository.deleteById(roleId);

        return ApiResponse.success("successfully deleted");
    }

    private Set<Permission> getValidPermission(Set<String> requestPermissions, Set<Permission> rolePermissions) {
        return requestPermissions.stream().map(permission -> {
                    try {
                        return Permission.valueOf(permission);
                    } catch (IllegalArgumentException e) {
                        throw new AppBadRequestException("Invalid permission: " + permission);
                    }
                })
                .filter(rolePermissions::contains).collect(Collectors.toSet());
    }
}
