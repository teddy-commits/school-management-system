package com.admas.management.modules.registration.service.impl;

import com.admas.management.modules.registration.dto.request.StudentRegistrationRequest;
import com.admas.management.modules.registration.dto.request.StudentUpdateRequest;
import com.admas.management.modules.registration.dto.response.StudentProfileResponse;
import com.admas.management.modules.registration.dto.response.StudentRegistrationResponse;
import com.admas.management.modules.registration.mapper.StudentMapper;
import com.admas.management.modules.registration.service.StudentRegistrationService;
import com.admas.management.modules.registration.validator.StudentValidator;
import com.admas.management.modules.shared.model.Role;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentRegistrationServiceImpl implements StudentRegistrationService {

    private final UserRepository userRepository;
    private final StudentMapper studentMapper;
    private final StudentValidator studentValidator;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public StudentRegistrationResponse registerStudent(StudentRegistrationRequest request) {
        // Validate request
        List<String> errors = studentValidator.validateStudentRegistration(request);
        if (!errors.isEmpty()) {
            throw new RuntimeException("Validation failed: " + String.join(", ", errors));
        }

        // Map request to User entity
        User user = studentMapper.toUser(request);

        // Encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Generate student ID
        user.setStudentId(generateStudentId());

        // Save user
        User savedUser = userRepository.save(user);

        // Return response
        return studentMapper.toRegistrationResponse(savedUser, "Student registered successfully. Student ID: " + savedUser.getStudentId());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        if (user.getRole() != Role.STUDENT) {
            throw new RuntimeException("User is not a student. Role: " + user.getRole());
        }

        return studentMapper.toProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentByStudentId(String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with student ID: " + studentId));

        return studentMapper.toProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));

        if (user.getRole() != Role.STUDENT) {
            throw new RuntimeException("User is not a student");
        }

        return studentMapper.toProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfileResponse> getAllStudents() {
        return userRepository.findByRole(Role.STUDENT)
                .stream()
                .map(studentMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfileResponse> getStudentsByDepartment(String department) {
        return userRepository.findByDepartment(department)
                .stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .map(studentMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfileResponse> getStudentsByFaculty(String faculty) {
        return userRepository.findByFaculty(faculty)
                .stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .map(studentMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfileResponse> getStudentsByEnrollmentYear(Integer year) {
        return userRepository.findByEnrollmentYear(year)
                .stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .map(studentMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StudentProfileResponse updateStudent(Long id, StudentUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        if (user.getRole() != Role.STUDENT) {
            throw new RuntimeException("User is not a student");
        }

        studentMapper.updateUserFromRequest(user, request);
        User updatedUser = userRepository.save(user);

        return studentMapper.toProfileResponse(updatedUser);
    }

    @Override
    public void deactivateStudent(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public void activateStudent(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        user.setIsActive(true);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalStudentCount() {
        return userRepository.countByRole(Role.STUDENT);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfileResponse> searchStudents(String keyword) {
        return userRepository.findByFirstNameContainingOrLastNameContainingOrEmailContaining(keyword, keyword, keyword)
                .stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .map(studentMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    private String generateStudentId() {
        int year = java.time.Year.now().getValue();
        long count = userRepository.countByRole(Role.STUDENT) + 1;
        return String.format("STU%d%04d", year, count);
    }
}