package com.library.repository;

import com.library.entity.User;
import com.library.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByRoleIn(List<Role> roles);
    List<User> findByRole(Role role);
    List<User> searchByRoleInAndFullNameContainingIgnoreCase(List<Role> roles, String fullName);
    List<User> findByRoleAndFullNameContainingIgnoreCase(Role role, String fullName);
}
