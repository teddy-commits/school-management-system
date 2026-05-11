package com.admas.management.modules.grading.controller;

import com.admas.management.modules.grading.dto.request.EnrollmentRequestDTO;
import com.admas.management.modules.grading.dto.request.StudentEnrollmentRequestDTO;
import com.admas.management.modules.grading.dto.response.EnrollmentResponseDTO;
import com.admas.management.modules.grading.dto.response.StudentEnrollmentResponseDTO;
import com.admas.management.modules.grading.service.EnrollmentService;
import com.admas.management.modules.grading.service.StudentEnrollmentService;
import com.admas.management.modules.infrastructure.security.service.SecurityService;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grading/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentEnrollmentService studentEnrollmentService;
    private final SecurityService securityService;
    private final UserRepository userRepository;

    @PostMapping("/course")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<EnrollmentResponseDTO> enrollInCourse(@Valid @RequestBody EnrollmentRequestDTO requestDTO) {
        if (!securityService.isStudentOwner(requestDTO.getStudentId())) {
            throw new RuntimeException("Access denied");
        }
        EnrollmentResponseDTO response = enrollmentService.enrollStudent(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/course/{enrollmentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<EnrollmentResponseDTO> withdrawFromCourse(@PathVariable Long enrollmentId) {
        EnrollmentResponseDTO response = enrollmentService.withdrawFromCourse(enrollmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/students/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<EnrollmentResponseDTO>> getStudentCourseEnrollments(@PathVariable Long studentId) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        List<EnrollmentResponseDTO> enrollments = enrollmentService.getStudentEnrollments(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/students/{studentId}/active")
    public ResponseEntity<List<EnrollmentResponseDTO>> getActiveCourseEnrollments(@PathVariable Long studentId) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        List<EnrollmentResponseDTO> enrollments = enrollmentService.getActiveEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/courses/{courseCode}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<EnrollmentResponseDTO>> getCourseEnrollments(@PathVariable String courseCode) {
        List<EnrollmentResponseDTO> enrollments = enrollmentService.getCourseEnrollments(courseCode);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/courses/{courseCode}/count")
    public ResponseEntity<Long> getEnrollmentCountByCourse(@PathVariable String courseCode) {
        Long count = enrollmentService.getEnrollmentCountByCourse(courseCode);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/course/check")
    public ResponseEntity<Boolean> isStudentEnrolledInCourse(
            @RequestParam Long studentId,
            @RequestParam String courseCode) {
        boolean enrolled = enrollmentService.isStudentEnrolled(studentId, courseCode);
        return ResponseEntity.ok(enrolled);
    }

    // ========== Section-based Enrollment (New) ==========
    @PostMapping("/section")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<StudentEnrollmentResponseDTO> enrollInSection(@Valid @RequestBody StudentEnrollmentRequestDTO request) {
        // Remove the student owner check for admin roles
        // Only check if the user is trying to enroll themselves when they are a student
        // For ADMIN and ACADEMIC_ADMINISTRATOR, they can enroll any student

        StudentEnrollmentResponseDTO response = studentEnrollmentService.enrollStudent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/section/{enrollmentId}")
    @PreAuthorize("hasAnyRole( 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<StudentEnrollmentResponseDTO> dropSection(@PathVariable Long enrollmentId) {
        StudentEnrollmentResponseDTO response = studentEnrollmentService.dropCourse(enrollmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/section/students/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<StudentEnrollmentResponseDTO>> getStudentSectionEnrollments(@PathVariable Long studentId) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        List<StudentEnrollmentResponseDTO> enrollments = studentEnrollmentService.getStudentEnrollments(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/section/students/{studentId}/semester")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<StudentEnrollmentResponseDTO>> getStudentSectionEnrollmentsBySemester(
            @PathVariable Long studentId,
            @RequestParam String semester,
            @RequestParam Integer academicYear) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        List<StudentEnrollmentResponseDTO> enrollments = studentEnrollmentService.getStudentEnrollmentsBySemester(studentId, semester, academicYear);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/section/sections/{sectionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<StudentEnrollmentResponseDTO>> getSectionEnrollments(@PathVariable Long sectionId) {
        List<StudentEnrollmentResponseDTO> enrollments = studentEnrollmentService.getSectionEnrollments(sectionId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/section/instructor/students")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<List<StudentEnrollmentResponseDTO>> getInstructorStudents(
            @RequestParam String semester,
            @RequestParam Integer academicYear,
            Authentication authentication) {
        String instructorEmail = authentication.getName();
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        List<StudentEnrollmentResponseDTO> students = studentEnrollmentService.getInstructorStudents(instructor.getId(), semester, academicYear);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/section/check")
    public ResponseEntity<Boolean> isStudentEnrolledInSection(
            @RequestParam Long studentId,
            @RequestParam Long sectionId) {
        boolean enrolled = studentEnrollmentService.isStudentEnrolled(studentId, sectionId);
        return ResponseEntity.ok(enrolled);
    }
}