package com.admas.management.modules.registration.dto.response;

import com.admas.management.modules.shared.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private Role role;
    private Set<Role> additionalRoles;
    private String userType;
    private String department;
    private String faculty;
    private Integer enrollmentYear;
    private Integer graduationYear;
    private String currentSemester;
    private Double cgpa;
    private Integer totalCredits;
    private String designation;
    private String qualification;
    private LocalDateTime joiningDate;
    private Double salary;
    private String specialization;
    private String officeLocation;
    private String position;
    private String division;
    private String adminLevel;
    private Boolean isActive;
    private Boolean isEmailVerified;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String permissions;
    private String message;
}
