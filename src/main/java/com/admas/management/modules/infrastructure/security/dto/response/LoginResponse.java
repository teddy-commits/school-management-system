package com.admas.management.modules.infrastructure.security.dto.response;


import com.admas.management.modules.shared.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType;
    private Long id;
    private String fullName;
    private String email;
    private String studentId;
    private String employeeId;
    private String loginId;
    private Role role;
    private Set<Role> additionalRoles;
    private String userType;
    private String message;
}