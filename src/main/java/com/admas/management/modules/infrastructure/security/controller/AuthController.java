package com.admas.management.modules.infrastructure.security.controller;

import com.admas.management.modules.infrastructure.security.dto.request.LoginRequest;
import com.admas.management.modules.infrastructure.security.dto.response.LoginResponse;
import com.admas.management.modules.infrastructure.security.jwt.JwtTokenProvider;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:5173",
        "http://localhost:5174"
}, allowCredentials = "true")

@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        User user = findUserByIdOrEmail(loginRequest.getId());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        String userType = determineUserType(user);
        String loginId = user.getStudentId() != null ? user.getStudentId() : user.getEmployeeId();

        LoginResponse response = LoginResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .studentId(user.getStudentId())
                .employeeId(user.getEmployeeId())
                .loginId(loginId != null ? loginId : user.getEmail())
                .role(user.getRole())
                .additionalRoles(user.getAdditionalRoles())
                .userType(userType)
                .message("Login successful. Welcome " + user.getFullName())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful. Please clear your token.");
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String userType = determineUserType(user);
        String userLoginId = user.getStudentId() != null ? user.getStudentId() : user.getEmployeeId();

        LoginResponse response = LoginResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .studentId(user.getStudentId())
                .employeeId(user.getEmployeeId())
                .loginId(userLoginId)
                .role(user.getRole())
                .additionalRoles(user.getAdditionalRoles())
                .userType(userType)
                .message("Current user info")
                .build();

        return ResponseEntity.ok(response);
    }

    // Updated method to check email FIRST
    private User findUserByIdOrEmail(String id) {
        log.info("Looking for user with identifier: {}", id);

        // Try to find by email FIRST (important for admin)
        return userRepository.findByEmail(id)
                .orElseGet(() -> userRepository.findByStudentId(id)
                        .orElseGet(() -> userRepository.findByEmployeeId(id)
                                .orElseThrow(() -> {
                                    log.error("User not found with identifier: {}", id);
                                    return new RuntimeException("User not found with ID: " + id);
                                })));
    }

    private String determineUserType(User user) {
        if (user.isStudent()) return "STUDENT";
        if (user.isInstructor()) return "INSTRUCTOR";
        if (user.isAcademicAdministrator()) return "ACADEMIC_ADMINISTRATOR";
        if (user.isManagement()) return "MANAGEMENT";
        if (user.isAdmin()) return "ADMIN";
        return "USER";
    }
}