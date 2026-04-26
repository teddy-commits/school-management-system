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

    // Basic Information
    private Long id;

    private String userId;  // studentId or employeeId

    private String firstName;

    private String lastName;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String address;

    // Role Information
    private Role role;

    private Set<Role> additionalRoles;

    private String userType;  // STUDENT, INSTRUCTOR, ACADEMIC_ADMINISTRATOR, MANAGEMENT, ADMIN

    // Academic Information (for students & instructors)
    private String department;

    private String faculty;

    private Integer enrollmentYear;

    private Integer graduationYear;

    private String currentSemester;

    private Double cgpa;  // Cumulative GPA

    private Integer totalCredits;

    // Professional Information (for staff & faculty)
    private String designation;  // Professor, Registrar, Manager, etc.

    private String qualification;  // PhD, Masters, etc.

    private LocalDateTime joiningDate;

    private Double salary;

    private String specialization;

    private String officeLocation;

    // Management/Admin specific
    private String position;

    private String division;

    private String adminLevel;

    // Status
    private Boolean isActive;

    private Boolean isEmailVerified;

    private LocalDateTime lastLoginAt;

    // Audit Information
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Permissions
    private String permissions;  // Comma-separated permissions

    private String message;
}
