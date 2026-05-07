package com.admas.management.modules.registration.service.impl;

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
    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegistrationResponse createUser(AdminUserCreationRequest request) {
        log.info("Creating new user with role: {}", request.getRole());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        User user = adminUserMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmployeeId(generateEmployeeId());
        if (user.getJoiningDate() == null) {
            user.setJoiningDate(LocalDateTime.now());
        }
        user.setIsActive(true);
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
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getDesignation() != null) user.setDesignation(request.getDesignation());
        if (request.getQualification() != null) user.setQualification(request.getQualification());
        if (request.getDepartment() != null) user.setDepartment(request.getDepartment());
        if (request.getFaculty() != null) user.setFaculty(request.getFaculty());
        if (request.getJoiningDate() != null) user.setJoiningDate(request.getJoiningDate());
        if (request.getSalary() != null) user.setSalary(request.getSalary());
        if (request.getOfficeLocation() != null) user.setAddress(request.getOfficeLocation());
        if (request.getPosition() != null) user.setDesignation(request.getPosition());
        if (request.getIsActive() != null) user.setIsActive(request.getIsActive());

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
