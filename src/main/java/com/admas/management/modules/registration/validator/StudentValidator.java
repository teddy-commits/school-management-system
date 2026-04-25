package com.admas.management.modules.registration.validator;

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

    public List<String> validateStudentRegistration(StudentRegistrationRequest request) {
        List<String> errors = new ArrayList<>();

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            errors.add("Email already registered: " + request.getEmail());
        }

        // Validate enrollment year
        int currentYear = java.time.Year.now().getValue();
        if (request.getEnrollmentYear() > currentYear) {
            errors.add("Enrollment year cannot be in the future");
        }

        if (request.getEnrollmentYear() < currentYear - 5) {
            errors.add("Enrollment year is too old. Maximum 5 years ago.");
        }

        // Validate department
        if (!isValidDepartment(request.getDepartment())) {
            errors.add("Invalid department: " + request.getDepartment());
        }

        // Validate faculty
        if (!isValidFaculty(request.getFaculty())) {
            errors.add("Invalid faculty: " + request.getFaculty());
        }

        return errors;
    }

    private boolean isValidDepartment(String department) {
        // Add your university's departments
        List<String> validDepartments = List.of(
                "Computer Science",
                "Software Engineering",
                "Information Technology",
                "Electrical Engineering",
                "Mechanical Engineering",
                "Civil Engineering",
                "Business Administration",
                "Economics",
                "Mathematics",
                "Physics",
                "Chemistry",
                "Biology"
        );
        return validDepartments.contains(department);
    }

    private boolean isValidFaculty(String faculty) {
        // Add your university's faculties
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