package com.admas.management.modules.infrastructure.security.service;

import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Attempting to load user by username: {}", username);

        // Try to find by email first (most common), then studentId, then employeeId
        User user = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findByStudentId(username)
                        .orElseGet(() -> userRepository.findByEmployeeId(username)
                                .orElseThrow(() -> {
                                    log.error("User not found with username: {}", username);
                                    return new UsernameNotFoundException("User not found with ID: " + username);
                                })));

        log.debug("User found: {} with role: {}", user.getEmail(), user.getRole());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Add primary role
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // Add additional roles
        if (user.getAdditionalRoles() != null && !user.getAdditionalRoles().isEmpty()) {
            user.getAdditionalRoles().forEach(role ->
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()))
            );
        }

        // IMPORTANT: Use email as the principal username for consistent authentication
        // This ensures authentication.getName() returns the email address
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),  // Use email as principal
                user.getPassword(),
                user.getIsActive(),
                true,
                true,
                true,
                authorities
        );
    }

    // Helper method to find user by ID (studentId, employeeId, or email)
    public User findUserById(String id) {
        return userRepository.findByEmail(id)
                .orElseGet(() -> userRepository.findByStudentId(id)
                        .orElseGet(() -> userRepository.findByEmployeeId(id)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id))));
    }
}