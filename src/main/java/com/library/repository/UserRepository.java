package com.library.repository;

import com.library.entity.Role;
import com.library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByRoleId(Long roleId);

    Long role(Role role);

    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

    boolean existsByUsernameOrPhoneNumber(String username, String phoneNumber);

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    Optional<User> findById(Long id);

    boolean existsByUsername(String username);
}
