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

        User user = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findByStudentId(username)
                        .orElseGet(() -> userRepository.findByEmployeeId(username)
                                .orElseThrow(() -> {
                                    log.error("User not found with username: {}", username);
                                    return new UsernameNotFoundException("User not found with ID: " + username);
                                })));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        if (user.getAdditionalRoles() != null && !user.getAdditionalRoles().isEmpty()) {
            user.getAdditionalRoles().forEach(role ->
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()))
            );
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getIsActive(),
                true,
                true,
                true,
                authorities
        );
    }
    public User findUserById(String id) {
        return userRepository.findByEmail(id)
                .orElseGet(() -> userRepository.findByStudentId(id)
                        .orElseGet(() -> userRepository.findByEmployeeId(id)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id))));
    }
}