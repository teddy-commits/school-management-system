package com.admas.management.modules.infrastructure.security.controller;

import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.model.Role;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/admin-check")
    public ResponseEntity<Map<String, Object>> checkAdmin() {
        Map<String, Object> result = new HashMap<>();

        User byEmail = userRepository.findByEmail("admin@university.com").orElse(null);

        Map<String, Object> adminByEmail = new HashMap<>();
        if (byEmail != null) {
            adminByEmail.put("id", byEmail.getId());
            adminByEmail.put("email", byEmail.getEmail());
            adminByEmail.put("role", byEmail.getRole() != null ? byEmail.getRole().toString() : "null");
            adminByEmail.put("employeeId", byEmail.getEmployeeId());
            adminByEmail.put("studentId", byEmail.getStudentId());
            adminByEmail.put("isActive", byEmail.getIsActive());
        } else {
            adminByEmail.put("status", "NOT FOUND");
        }
        result.put("adminByEmail", adminByEmail);

        User byEmployeeId = userRepository.findByEmployeeId("ADMIN001").orElse(null);
        Map<String, Object> adminByEmployeeId = new HashMap<>();
        if (byEmployeeId != null) {
            adminByEmployeeId.put("id", byEmployeeId.getId());
            adminByEmployeeId.put("email", byEmployeeId.getEmail());
            adminByEmployeeId.put("employeeId", byEmployeeId.getEmployeeId());
            adminByEmployeeId.put("role", byEmployeeId.getRole() != null ? byEmployeeId.getRole().toString() : "null");
        } else {
            adminByEmployeeId.put("status", "NOT FOUND");
        }
        result.put("adminByEmployeeId", adminByEmployeeId);

        List<User> allUsers = userRepository.findAll();
        result.put("totalUsers", allUsers.size());

        List<Map<String, Object>> userList = new ArrayList<>();
        for (User user : allUsers) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("role", user.getRole() != null ? user.getRole().toString() : "null");
            userMap.put("employeeId", user.getEmployeeId() != null ? user.getEmployeeId() : "null");
            userMap.put("studentId", user.getStudentId() != null ? user.getStudentId() : "null");
            userMap.put("isActive", user.getIsActive());
            userList.add(userMap);
        }
        result.put("allUsers", userList);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/test-password")
    public ResponseEntity<Map<String, Object>> testPassword(
            @RequestParam String email,
            @RequestParam String password) {

        Map<String, Object> result = new HashMap<>();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            result.put("error", "User not found with email: " + email);
            return ResponseEntity.ok(result);
        }

        boolean matches = passwordEncoder.matches(password, user.getPassword());

        result.put("email", user.getEmail());
        result.put("passwordHash", user.getPassword());
        result.put("providedPassword", password);
        result.put("matches", matches);
        result.put("role", user.getRole() != null ? user.getRole().toString() : "null");
        result.put("isActive", user.getIsActive());
        result.put("employeeId", user.getEmployeeId());
        result.put("studentId", user.getStudentId());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/generate-hash")
    public ResponseEntity<Map<String, String>> generateHash(@RequestParam String password) {
        Map<String, String> result = new HashMap<>();
        String encoded = passwordEncoder.encode(password);
        result.put("rawPassword", password);
        result.put("encodedPassword", encoded);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/create-admin")
    public ResponseEntity<Map<String, String>> createAdmin() {
        Map<String, String> result = new HashMap<>();

        try {
            if (userRepository.findByEmail("admin@university.com").isPresent()) {
                result.put("error", "Admin already exists!");
                return ResponseEntity.ok(result);
            }
            User admin = new User();
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("admin@university.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhoneNumber("1234567890");
            admin.setRole(Role.ADMIN);
            admin.setEmployeeId("ADMIN001");
            admin.setDesignation("System Administrator");
            admin.setIsActive(true);
            admin.setIsEmailVerified(true);

            User savedAdmin = userRepository.save(admin);

            result.put("message", "Admin created successfully!");
            result.put("email", savedAdmin.getEmail());
            result.put("employeeId", savedAdmin.getEmployeeId());
            result.put("password", "admin123");

        } catch (Exception e) {
            result.put("error", "Failed to create admin: " + e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/check-auth")
    public ResponseEntity<Map<String, Object>> checkAuthentication() {
        Map<String, Object> result = new HashMap<>();

        long adminCount = userRepository.countByRole(Role.ADMIN);
        result.put("adminCount", adminCount);

        Map<String, Long> roleCounts = new HashMap<>();
        for (Role role : Role.values()) {
            long count = userRepository.countByRole(role);
            if (count > 0) {
                roleCounts.put(role.toString(), count);
            }
        }
        result.put("userCountsByRole", roleCounts);

        try {
            long totalUsers = userRepository.count();
            result.put("databaseConnected", true);
            result.put("totalUsers", totalUsers);
        } catch (Exception e) {
            result.put("databaseConnected", false);
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }
}