package com.admas.management.modules.registration.dto.request;

import com.admas.management.modules.shared.model.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserCreationRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String phoneNumber;
    private String address;

    private Role role;
    private String designation;
    private String qualification;

    // Remove @NotNull - make departmentId optional
    // Only required for INSTRUCTOR role (validate in service layer)
    private Long departmentId;

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
}