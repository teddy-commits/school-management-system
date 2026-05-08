package com.admas.management.modules.registration.mapper;

import com.admas.management.modules.department.model.Department;
import com.admas.management.modules.department.repository.DepartmentRepository;
import com.admas.management.modules.registration.dto.request.StudentRegistrationRequest;
import com.admas.management.modules.registration.dto.request.StudentUpdateRequest;
import com.admas.management.modules.registration.dto.response.StudentProfileResponse;
import com.admas.management.modules.registration.dto.response.StudentRegistrationResponse;
import com.admas.management.modules.shared.model.Role;
import com.admas.management.modules.shared.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class StudentMapper {

    private final DepartmentRepository departmentRepository;

    public User toUser(StudentRegistrationRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        // Handle department - find by name or create a simple reference
        if (request.getDepartment() != null) {
            // Try to find department by name
            Department department = departmentRepository.findByName(request.getDepartment())
                    .orElse(null);
            if (department != null) {
                user.setDepartment(department);
                user.setFaculty(department.getFaculty());
            } else {
                // If department not found, store the name in a separate field or log warning
                // For now, we'll just set it as a reference
                user.setDepartmentName(request.getDepartment());
                user.setFaculty(request.getFaculty());
            }
        } else {
            user.setFaculty(request.getFaculty());
        }

        user.setEnrollmentYear(request.getEnrollmentYear());
        user.setStudentType(request.getStudentType());
        user.setRole(Role.STUDENT);
        user.setIsActive(true);
        user.setCgpa(0.0);
        user.setCurrentSemester("Semester 1");
        return user;
    }

    public StudentRegistrationResponse toRegistrationResponse(User user, String message) {
        return StudentRegistrationResponse.builder()
                .id(user.getId())
                .studentId(user.getStudentId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .department(user.getDepartmentName())
                .faculty(user.getFaculty())
                .enrollmentYear(user.getEnrollmentYear())
                .studentType(user.getStudentType())
                .registrationStatus("SUCCESS")
                .registrationDate(LocalDateTime.now())
                .message(message)
                .build();
    }

    public StudentProfileResponse toProfileResponse(User user) {
        return StudentProfileResponse.builder()
                .id(user.getId())
                .studentId(user.getStudentId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .department(user.getDepartmentName())
                .faculty(user.getFaculty())
                .enrollmentYear(user.getEnrollmentYear())
                .cgpa(user.getCgpa())
                .currentSemester(user.getCurrentSemester())
                .totalCredits(calculateTotalCredits(user))
                .studentType(user.getStudentType())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private Integer calculateTotalCredits(User user) {
        return 0;
    }

    public void updateUserFromRequest(User user, StudentUpdateRequest request) {
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getDepartment() != null) {
            // Try to find department by name
            Department department = departmentRepository.findByName(request.getDepartment())
                    .orElse(null);
            if (department != null) {
                user.setDepartment(department);
                user.setFaculty(department.getFaculty());
            } else {
                user.setDepartmentName(request.getDepartment());
            }
        }
        if (request.getFaculty() != null) user.setFaculty(request.getFaculty());
        if (request.getEnrollmentYear() != null) user.setEnrollmentYear(request.getEnrollmentYear());
        if (request.getStudentType() != null) user.setStudentType(request.getStudentType());
    }
}