/*package com.admas.management.modules.grading.controller;

import com.admas.management.modules.grading.dto.request.StudentEnrollmentRequestDTO;
import com.admas.management.modules.grading.dto.response.StudentEnrollmentResponseDTO;
import com.admas.management.modules.grading.service.StudentEnrollmentService;
import com.admas.management.modules.infrastructure.security.service.SecurityService;
import com.admas.management.modules.shared.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.admas.management.modules.shared.repository.UserRepository;
import java.util.List;

@RestController
@RequestMapping("/grading/enrollments")
@RequiredArgsConstructor
public class StudentEnrollmentController {

    private final StudentEnrollmentService enrollmentService;
    private final SecurityService securityService;
    private final UserRepository userRepository;

    // Enroll student in a section
    @PostMapping("/section")  // Changed to avoid conflict
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<StudentEnrollmentResponseDTO> enrollStudent(@Valid @RequestBody StudentEnrollmentRequestDTO request) {
        if (!securityService.isStudentOwner(request.getStudentId())) {
            throw new RuntimeException("Access denied");
        }
        StudentEnrollmentResponseDTO response = enrollmentService.enrollStudent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Drop a course
    @DeleteMapping("/section/{enrollmentId}")  // Changed to avoid conflict
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<StudentEnrollmentResponseDTO> dropCourse(@PathVariable Long enrollmentId) {
        StudentEnrollmentResponseDTO response = enrollmentService.dropCourse(enrollmentId);
        return ResponseEntity.ok(response);
    }

    // Get student's enrollments
    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<StudentEnrollmentResponseDTO>> getStudentEnrollments(@PathVariable Long studentId) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        List<StudentEnrollmentResponseDTO> enrollments = enrollmentService.getStudentEnrollments(studentId);
        return ResponseEntity.ok(enrollments);
    }

    // Get student's enrollments by semester
    @GetMapping("/students/{studentId}/semester")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<StudentEnrollmentResponseDTO>> getStudentEnrollmentsBySemester(
            @PathVariable Long studentId,
            @RequestParam String semester,
            @RequestParam Integer academicYear) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        List<StudentEnrollmentResponseDTO> enrollments = enrollmentService.getStudentEnrollmentsBySemester(studentId, semester, academicYear);
        return ResponseEntity.ok(enrollments);
    }

    // Get all enrollments for a section (instructor view)
    @GetMapping("/sections/{sectionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<StudentEnrollmentResponseDTO>> getSectionEnrollments(@PathVariable Long sectionId) {
        List<StudentEnrollmentResponseDTO> enrollments = enrollmentService.getSectionEnrollments(sectionId);
        return ResponseEntity.ok(enrollments);
    }

    // Get instructor's students
    @GetMapping("/instructor/students")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<List<StudentEnrollmentResponseDTO>> getInstructorStudents(
            @RequestParam String semester,
            @RequestParam Integer academicYear,
            Authentication authentication) {
        String instructorEmail = authentication.getName();
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        List<StudentEnrollmentResponseDTO> students = enrollmentService.getInstructorStudents(instructor.getId(), semester, academicYear);
        return ResponseEntity.ok(students);
    }

}*/