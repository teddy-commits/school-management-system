package com.admas.management.modules.registration.service.impl;

import com.admas.management.modules.department.model.Department;
import com.admas.management.modules.department.repository.DepartmentRepository;
import com.admas.management.modules.registration.dto.request.AdminUserCreationRequest;
import com.admas.management.modules.registration.dto.request.AdminUserUpdateRequest;
import com.admas.management.modules.registration.dto.response.UserProfileResponse;
import com.admas.management.modules.registration.dto.response.UserRegistrationResponse;
import com.admas.management.modules.registration.mapper.AdminUserMapper;
import com.admas.management.modules.registration.service.AdminUserService;
import com.admas.management.modules.shared.model.Role;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;  // Add this
    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegistrationResponse createUser(AdminUserCreationRequest request) {
        log.info("Creating new user with role: {}", request.getRole());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        // Validate department ONLY for INSTRUCTOR role
        // Remove the check for academic administrators
        if (request.getRole() == Role.INSTRUCTOR && request.getDepartmentId() == null) {
            throw new RuntimeException("Department is required for instructors");
        }

        // Verify department exists only if provided (for any role)
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.getDepartmentId()));
        }

        // Map request to User entity
        User user = adminUserMapper.toEntity(request);

        // Encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Generate employee ID
        user.setEmployeeId(generateEmployeeId());

        // Set joining date if not provided
        if (user.getJoiningDate() == null) {
            user.setJoiningDate(LocalDateTime.now());
        }

        // Set active status
        user.setIsActive(true);

        // Save user
        User savedUser = userRepository.save(user);

        String message = String.format("%s created successfully with Employee ID: %s",
                getRoleDisplayName(request.getRole()), savedUser.getEmployeeId());

        return adminUserMapper.toRegistrationResponse(savedUser, message);
    }
    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(adminUserMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return adminUserMapper.toProfileResponse(user);
    }

    @Override
    public UserProfileResponse updateUser(Long id, AdminUserUpdateRequest request) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // If department ID is being updated, verify department exists
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.getDepartmentId()));
        }

        // Use mapper for updates to handle department relationship
        adminUserMapper.updateEntityFromRequest(user, request);

        User updatedUser = userRepository.save(user);

        return adminUserMapper.toProfileResponse(updatedUser);
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated with id: {}", id);
    }

    @Override
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(true);
        userRepository.save(user);
        log.info("User activated with id: {}", id);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        log.info("User deleted with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> searchUsers(String keyword, Role role) {
        List<User> users;

        if (role != null) {
            users = userRepository.findByRoleAndKeyword(role, keyword);
        } else {
            users = userRepository.findByFirstNameContainingOrLastNameContainingOrEmailContaining(
                    keyword, keyword, keyword);
        }

        return users.stream()
                .map(adminUserMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getUserStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalStudents", userRepository.countByRole(Role.STUDENT));
        stats.put("totalInstructors", userRepository.countByRole(Role.INSTRUCTOR));
        stats.put("totalAcademicAdministrators", userRepository.countByRole(Role.ACADEMIC_ADMINISTRATOR));
        stats.put("totalManagementStaff", userRepository.countByRole(Role.MANAGEMENT));
        stats.put("totalAdmins", userRepository.countByRole(Role.ADMIN));
        stats.put("totalActiveUsers", userRepository.countByIsActiveTrue());
        stats.put("totalUsers", userRepository.count());
        return stats;
    }

    private String generateEmployeeId() {
        int year = java.time.Year.now().getValue();
        long count = userRepository.count() + 1;
        return String.format("EMP%d%04d", year, count);
    }

    private String getRoleDisplayName(Role role) {
        return switch (role) {
            case INSTRUCTOR -> "Instructor";
            case ACADEMIC_ADMINISTRATOR -> "Academic Administrator";
            case MANAGEMENT -> "Management Staff";
            case ADMIN -> "Administrator";
            default -> "User";
        };
    }
}