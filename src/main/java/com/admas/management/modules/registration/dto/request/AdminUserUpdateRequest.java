package com.admas.management.modules.registration.dto.request;

import com.admas.management.modules.shared.model.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserUpdateRequest {

    private String firstName;
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;
    private String address;
    private Role role;
    private String designation;
    private String qualification;
    private String department;
    private String faculty;
    private LocalDateTime joiningDate;
    private Double salary;
    private String specialization;
    private String officeLocation;
    private String position;
    private String division;
    private String adminLevel;
    private String permissions;
    private Boolean isActive;
}