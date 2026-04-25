package com.admas.management.modules.grading.controller;

import com.admas.management.modules.grading.dto.request.EnrollmentRequestDTO;
import com.admas.management.modules.grading.dto.response.EnrollmentResponseDTO;
import com.admas.management.modules.grading.service.EnrollmentService;
import com.admas.management.modules.infrastructure.security.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grading/enrollments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<EnrollmentResponseDTO> enrollStudent(@Valid @RequestBody EnrollmentRequestDTO requestDTO) {
        // Students can only enroll themselves
        if (!securityService.isStudentOwner(requestDTO.getStudentId())) {
            throw new RuntimeException("Access denied");
        }
        EnrollmentResponseDTO response = enrollmentService.enrollStudent(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{enrollmentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<EnrollmentResponseDTO> withdrawFromCourse(@PathVariable Long enrollmentId) {
        EnrollmentResponseDTO response = enrollmentService.withdrawFromCourse(enrollmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<EnrollmentResponseDTO>> getStudentEnrollments(@PathVariable Long studentId) {
        // Students can only view their own enrollments
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        List<EnrollmentResponseDTO> enrollments = enrollmentService.getStudentEnrollments(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/students/{studentId}/active")
    public ResponseEntity<List<EnrollmentResponseDTO>> getActiveEnrollments(@PathVariable Long studentId) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        List<EnrollmentResponseDTO> enrollments = enrollmentService.getActiveEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/courses/{courseCode}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<EnrollmentResponseDTO>> getCourseEnrollments(@PathVariable String courseCode) {
        List<EnrollmentResponseDTO> enrollments = enrollmentService.getCourseEnrollments(courseCode);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/courses/{courseCode}/count")
    public ResponseEntity<Long> getEnrollmentCount(@PathVariable String courseCode) {
        Long count = enrollmentService.getEnrollmentCountByCourse(courseCode);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isStudentEnrolled(
            @RequestParam Long studentId,
            @RequestParam String courseCode) {
        boolean enrolled = enrollmentService.isStudentEnrolled(studentId, courseCode);
        return ResponseEntity.ok(enrolled);
    }
}
