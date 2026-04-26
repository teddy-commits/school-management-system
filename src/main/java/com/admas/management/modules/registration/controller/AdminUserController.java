package com.admas.management.modules.registration.controller;

import com.admas.management.modules.registration.dto.request.AdminUserCreationRequest;
import com.admas.management.modules.registration.dto.request.AdminUserUpdateRequest;
import com.admas.management.modules.registration.dto.response.UserProfileResponse;
import com.admas.management.modules.registration.dto.response.UserRegistrationResponse;
import com.admas.management.modules.registration.service.AdminUserService;
import com.admas.management.modules.shared.model.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminUserController {

    private final AdminUserService adminUserService;

    // Create Instructor (Admin only)
    @PostMapping("/instructors")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserRegistrationResponse> createInstructor(
            @Valid @RequestBody AdminUserCreationRequest request) {
        request.setRole(Role.INSTRUCTOR);
        UserRegistrationResponse response = adminUserService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Create Academic Administrator (Admin only)
    @PostMapping("/academic-administrators")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserRegistrationResponse> createAcademicAdministrator(
            @Valid @RequestBody AdminUserCreationRequest request) {
        request.setRole(Role.ACADEMIC_ADMINISTRATOR);
        UserRegistrationResponse response = adminUserService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Create Management Staff (Admin only)
    @PostMapping("/management")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserRegistrationResponse> createManagementStaff(
            @Valid @RequestBody AdminUserCreationRequest request) {
        request.setRole(Role.MANAGEMENT);
        UserRegistrationResponse response = adminUserService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Create Admin (Super Admin only)
    @PostMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserRegistrationResponse> createAdmin(
            @Valid @RequestBody AdminUserCreationRequest request) {
        request.setRole(Role.ADMIN);
        UserRegistrationResponse response = adminUserService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all instructors
    @GetMapping("/instructors")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<UserProfileResponse>> getAllInstructors() {
        List<UserProfileResponse> instructors = adminUserService.getUsersByRole(Role.INSTRUCTOR);
        return ResponseEntity.ok(instructors);
    }

    // Get all academic administrators
    @GetMapping("/academic-administrators")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getAllAcademicAdministrators() {
        List<UserProfileResponse> admins = adminUserService.getUsersByRole(Role.ACADEMIC_ADMINISTRATOR);
        return ResponseEntity.ok(admins);
    }

    // Get all management staff
    @GetMapping("/management")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getAllManagementStaff() {
        List<UserProfileResponse> staff = adminUserService.getUsersByRole(Role.MANAGEMENT);
        return ResponseEntity.ok(staff);
    }

    // Get all admins (Super Admin only)
    @GetMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getAllAdmins() {
        List<UserProfileResponse> admins = adminUserService.getUsersByRole(Role.ADMIN);
        return ResponseEntity.ok(admins);
    }

    // Get user by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        UserProfileResponse response = adminUserService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    // Update user
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserProfileResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserUpdateRequest request) {  // Changed to AdminUserUpdateRequest
        UserProfileResponse response = adminUserService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }
    // Deactivate user
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable Long id) {
        adminUserService.deactivateUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deactivated successfully");
        return ResponseEntity.ok(response);
    }

    // Activate user
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> activateUser(@PathVariable Long id) {
        adminUserService.activateUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User activated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(required = false) Role role) {
        List<UserProfileResponse> users = adminUserService.searchUsers(keyword, role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MANAGEMENT')")
    public ResponseEntity<Map<String, Long>> getUserStatistics() {
        Map<String, Long> stats = adminUserService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }
}