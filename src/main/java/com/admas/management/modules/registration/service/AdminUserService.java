package com.admas.management.modules.registration.service;

import com.admas.management.modules.registration.dto.request.AdminUserCreationRequest;
import com.admas.management.modules.registration.dto.request.AdminUserUpdateRequest;
import com.admas.management.modules.registration.dto.response.UserProfileResponse;
import com.admas.management.modules.registration.dto.response.UserRegistrationResponse;
import com.admas.management.modules.shared.model.Role;


import java.util.List;
import java.util.Map;

public interface AdminUserService {

    // Create user (admin only)
    UserRegistrationResponse createUser(AdminUserCreationRequest request);

    // Get users by role
    List<UserProfileResponse> getUsersByRole(Role role);

    // Get user by ID
    UserProfileResponse getUserById(Long id);

    // Update user
    // Update the method signature
    UserProfileResponse updateUser(Long id, AdminUserUpdateRequest request);

    // Activate/Deactivate user
    void deactivateUser(Long id);
    void activateUser(Long id);

    // Delete user
    void deleteUser(Long id);

    // Search users
    List<UserProfileResponse> searchUsers(String keyword, Role role);

    // Statistics
    Map<String, Long> getUserStatistics();
}
