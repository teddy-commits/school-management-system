package com.admas.management.modules.registration.dto.response;

import com.admas.management.modules.shared.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationResponse {

    private Long id;
    private String userId;
    private String fullName;
    private String email;
    private Role role;
    private String userType;
    private String department;
    private String designation;
    private String registrationStatus;
    private LocalDateTime registrationDate;
    private String message;
}