package com.admas.management.modules.infrastructure.security.service;

import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = userRepository.findByStudentId(username)
                .orElseGet(() -> userRepository.findByEmployeeId(username)
                        .orElseGet(() -> userRepository.findByEmail(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + username))));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        if (user.getAdditionalRoles() != null) {
            user.getAdditionalRoles().forEach(role ->
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()))
            );
        }
        String principal = user.getStudentId() != null ? user.getStudentId() :
                (user.getEmployeeId() != null ? user.getEmployeeId() : user.getEmail());

        return new org.springframework.security.core.userdetails.User(
                principal,
                user.getPassword(),
                user.getIsActive(),
                true,
                true,
                true,
                authorities
        );
    }
    public User findUserById(String id) {
        return userRepository.findByStudentId(id)
                .orElseGet(() -> userRepository.findByEmployeeId(id)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id)));
    }
}
