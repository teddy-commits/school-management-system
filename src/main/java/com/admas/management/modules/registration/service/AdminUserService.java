package com.admas.management.modules.registration.service;

import com.admas.management.modules.registration.dto.request.AdminUserCreationRequest;
import com.admas.management.modules.registration.dto.request.AdminUserUpdateRequest;
import com.admas.management.modules.registration.dto.response.UserProfileResponse;
import com.admas.management.modules.registration.dto.response.UserRegistrationResponse;
import com.admas.management.modules.shared.model.Role;


import java.util.List;
import java.util.Map;

public interface AdminUserService {
    UserRegistrationResponse createUser(AdminUserCreationRequest request);
    List<UserProfileResponse> getUsersByRole(Role role);
    UserProfileResponse getUserById(Long id);
    UserProfileResponse updateUser(Long id, AdminUserUpdateRequest request);
    void deactivateUser(Long id);
    void activateUser(Long id);
    void deleteUser(Long id);
    List<UserProfileResponse> searchUsers(String keyword, Role role);
    Map<String, Long> getUserStatistics();
}
