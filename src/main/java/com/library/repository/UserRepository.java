package com.library.repository;

import com.library.entity.User;
import com.library.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByRoleIn(List<Role> roles);
    List<User> findByRole(Role role);
    List<User> findByRoleInAndFullName(List<Role> roles, String keyword);
    List<User> findByRoleAndFullNameContainingIgnoreCase(Role role, String fullName);
}
