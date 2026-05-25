package com.admas.management.modules.registration.validator;

import com.admas.management.modules.department.repository.DepartmentRepository;
import com.admas.management.modules.registration.dto.request.StudentRegistrationRequest;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StudentValidator {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public List<String> validateStudentRegistration(StudentRegistrationRequest request) {
        List<String> errors = new ArrayList<>();

        if (userRepository.existsByEmail(request.getEmail())) {
            errors.add("Email already registered: " + request.getEmail());
        }

        int currentYear = java.time.Year.now().getValue();
        if (request.getEnrollmentYear() > currentYear) {
            errors.add("Enrollment year cannot be in the future");
        }

        if (request.getEnrollmentYear() < currentYear - 5) {
            errors.add("Enrollment year is too old. Maximum 5 years ago.");
        }

        if (request.getDepartmentId() == null) {
            errors.add("Department is required");
        } else if (!departmentRepository.existsById(request.getDepartmentId())) {
            errors.add("Invalid department selected. Department not found in the system.");
        }

        if (request.getFaculty() != null && !isValidFaculty(request.getFaculty())) {
            errors.add("Invalid faculty: " + request.getFaculty());
        }

        return errors;
    }
    private boolean isValidFaculty(String faculty) {
        List<String> validFaculties = List.of(
                "Faculty of Computing and Informatics",
                "Faculty of Engineering",
                "Faculty of Business and Economics",
                "Faculty of Science",
                "Faculty of Arts and Humanities"
        );
        return validFaculties.contains(faculty);
    }
}