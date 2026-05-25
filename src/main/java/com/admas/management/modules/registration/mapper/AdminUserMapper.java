package com.admas.management.modules.registration.mapper;


import com.admas.management.modules.department.model.Department;
import com.admas.management.modules.department.repository.DepartmentRepository;
import com.admas.management.modules.registration.dto.request.AdminUserCreationRequest;
import com.admas.management.modules.registration.dto.request.AdminUserUpdateRequest;
import com.admas.management.modules.registration.dto.response.UserProfileResponse;
import com.admas.management.modules.registration.dto.response.UserRegistrationResponse;
import com.admas.management.modules.shared.model.Role;
import com.admas.management.modules.shared.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminUserMapper {

    private final DepartmentRepository departmentRepository;

    public User toEntity(AdminUserCreationRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(request.getRole());
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.getDepartmentId()));
            user.setDepartment(department);
            user.setDepartmentName(department.getName());
            user.setFaculty(department.getFaculty());
        }

        switch (request.getRole()) {
            case INSTRUCTOR:
            case SENIOR_INSTRUCTOR:
            case PROFESSOR:
            case ASSOCIATE_PROFESSOR:
            case ASSISTANT_PROFESSOR:
                user.setDesignation(request.getDesignation());
                user.setQualification(request.getQualification());
                user.setJoiningDate(request.getJoiningDate());
                user.setSalary(request.getSalary());
                break;

            case ACADEMIC_ADMINISTRATOR:
            case HOD:
            case DEAN:
            case REGISTRAR:
                user.setDesignation(request.getDesignation());
                user.setJoiningDate(request.getJoiningDate());
                break;

            case MANAGEMENT:
            case FINANCE_MANAGER:
            case HR_MANAGER:
                user.setDesignation(request.getPosition());
                user.setJoiningDate(request.getJoiningDate());
                user.setSalary(request.getSalary());
                break;

            case ADMIN:
            case SUPER_ADMIN:
                user.setDesignation("System Administrator");
                user.setJoiningDate(LocalDateTime.now());
                break;
        }

        return user;
    }

    public UserRegistrationResponse toRegistrationResponse(User user, String message) {
        return UserRegistrationResponse.builder()
                .id(user.getId())
                .userId(user.getEmployeeId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .userType(determineUserType(user))
                .department(user.getDepartmentName())
                .designation(user.getDesignation())
                .registrationStatus("SUCCESS")
                .registrationDate(LocalDateTime.now())
                .message(message)
                .build();
    }

    public UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .userId(user.getEmployeeId() != null ? user.getEmployeeId() : user.getStudentId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(user.getRole())
                .additionalRoles(user.getAdditionalRoles())
                .userType(determineUserType(user))
                .department(user.getDepartmentName())
                .faculty(user.getFaculty())
                .designation(user.getDesignation())
                .qualification(user.getQualification())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .permissions(getPermissionsForRole(user.getRole()))
                .build();
    }

    public void updateEntityFromRequest(User user, AdminUserCreationRequest request) {
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getDesignation() != null) user.setDesignation(request.getDesignation());
        if (request.getQualification() != null) user.setQualification(request.getQualification());
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            user.setDepartment(department);
            user.setDepartmentName(department.getName());
            user.setFaculty(department.getFaculty());
        }
        if (request.getSalary() != null) user.setSalary(request.getSalary());
        if (request.getOfficeLocation() != null) user.setAddress(request.getOfficeLocation());
    }

    public void updateEntityFromRequest(User user, AdminUserUpdateRequest request) {
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getDesignation() != null) user.setDesignation(request.getDesignation());
        if (request.getQualification() != null) user.setQualification(request.getQualification());
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            user.setDepartment(department);
            user.setDepartmentName(department.getName());
            user.setFaculty(department.getFaculty());
        }
        if (request.getJoiningDate() != null) user.setJoiningDate(request.getJoiningDate());
        if (request.getSalary() != null) user.setSalary(request.getSalary());
        if (request.getOfficeLocation() != null) user.setAddress(request.getOfficeLocation());
        if (request.getPosition() != null) user.setDesignation(request.getPosition());
        if (request.getIsActive() != null) user.setIsActive(request.getIsActive());
    }

    private String determineUserType(User user) {
        if (user.isInstructor()) return "INSTRUCTOR";
        if (user.isAcademicAdministrator()) return "ACADEMIC_ADMINISTRATOR";
        if (user.isManagement()) return "MANAGEMENT";
        if (user.isAdmin()) return "ADMIN";
        if (user.isStudent()) return "STUDENT";
        return "USER";
    }

    private String getPermissionsForRole(Role role) {
        return switch (role) {
            case INSTRUCTOR, SENIOR_INSTRUCTOR, PROFESSOR ->
                    "CREATE_COURSES, ASSIGN_GRADES, VIEW_STUDENTS, MANAGE_COURSES";
            case ACADEMIC_ADMINISTRATOR, HOD, DEAN, REGISTRAR ->
                    "MANAGE_ACADEMICS, APPROVE_REGISTRATIONS, ISSUE_TRANSCRIPTS, MANAGE_DEPARTMENT";
            case MANAGEMENT, FINANCE_MANAGER, HR_MANAGER ->
                    "MANAGE_FEES, VIEW_REPORTS, MANAGE_STAFF, FINANCIAL_ACCESS";
            case ADMIN, SUPER_ADMIN -> "ALL_ACCESS";
            default -> "BASIC_ACCESS";
        };
    }
}