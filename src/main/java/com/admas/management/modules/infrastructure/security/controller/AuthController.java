package com.admas.management.modules.infrastructure.security.controller;

import com.admas.management.modules.infrastructure.security.dto.request.LoginRequest;
import com.admas.management.modules.infrastructure.security.dto.response.LoginResponse;
import com.admas.management.modules.infrastructure.security.jwt.JwtTokenProvider;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Authenticate using the ID (studentId or employeeId)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getId(),  // Now using ID (studentId or employeeId)
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        // Find user by ID (studentId or employeeId)
        User user = findUserById(loginRequest.getId());

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
                .loginId(loginId)
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
        String loginId = authentication.getName();
        User user = findUserById(loginId);

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

    private User findUserById(String id) {
        // Try to find by Student ID first, then Employee ID
        return userRepository.findByStudentId(id)
                .orElseGet(() -> userRepository.findByEmployeeId(id)
                        .orElseThrow(() -> new RuntimeException("User not found with ID: " + id)));
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