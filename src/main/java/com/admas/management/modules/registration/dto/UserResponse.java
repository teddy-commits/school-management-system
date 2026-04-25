package com.admas.management.modules.registration.dto;

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
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String studentId;
    private String employeeId;
    private Role primaryRole;
    private Set<Role> additionalRoles;
    private String department;
    private String faculty;
    private String designation;
    private String phoneNumber;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String userType;  // "Student", "Instructor", "Administrator", etc.

    // For students
    private Integer enrollmentYear;
    private Double cgpa;
    private String currentSemester;

    // For dashboard
    private String permissions;

    public static UserResponse fromUser(com.admas.management.modules.shared.model.User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .studentId(user.getStudentId())
                .employeeId(user.getEmployeeId())
                .primaryRole(user.getRole())
                .additionalRoles(user.getAdditionalRoles())
                .department(user.getDepartment())
                .faculty(user.getFaculty())
                .designation(user.getDesignation())
                .phoneNumber(user.getPhoneNumber())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .userType(determineUserType(user))
                .enrollmentYear(user.getEnrollmentYear())
                .cgpa(user.getCgpa())
                .currentSemester(user.getCurrentSemester())
                .permissions(getPermissionsForRole(user.getRole()))
                .build();
    }

    private static String determineUserType(com.admas.management.modules.shared.model.User user) {
        if (user.isStudent()) return "Student";
        if (user.isInstructor()) return "Instructor";
        if (user.isAcademicAdministrator()) return "Academic Administrator";
        if (user.isManagement()) return "Management Staff";
        if (user.isAdmin()) return "System Administrator";
        return "User";
    }

    private static String getPermissionsForRole(Role role) {
        return switch (role) {
            case STUDENT, UNDERGRADUATE_STUDENT, POSTGRADUATE_STUDENT, RESEARCH_STUDENT ->
                    "VIEW_COURSES, VIEW_GRADES, PAY_FEES";
            case INSTRUCTOR, SENIOR_INSTRUCTOR, PROFESSOR, ASSOCIATE_PROFESSOR, ASSISTANT_PROFESSOR ->
                    "CREATE_COURSES, ASSIGN_GRADES, VIEW_STUDENTS";
            case ACADEMIC_ADMINISTRATOR, HOD, DEAN, REGISTRAR ->
                    "MANAGE_ACADEMICS, APPROVE_REGISTRATIONS, ISSUE_TRANSCRIPTS";
            case MANAGEMENT, FINANCE_MANAGER, HR_MANAGER ->
                    "MANAGE_FEES, VIEW_REPORTS, MANAGE_STAFF";
            case ADMIN, SUPER_ADMIN ->
                    "ALL_ACCESS";
            default -> "BASIC_ACCESS";
        };
    }
}