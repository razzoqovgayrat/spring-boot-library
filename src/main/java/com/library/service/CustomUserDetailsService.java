package com.library.service;

import com.library.entity.User;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));
    }

    public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));
    }

    public User getCurrentUser() {
        return (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }
}
