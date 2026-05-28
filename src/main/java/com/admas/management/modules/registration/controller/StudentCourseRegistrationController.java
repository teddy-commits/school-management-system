package com.admas.management.modules.registration.controller;

import com.admas.management.modules.grading.dto.request.CourseRegistrationRequestDTO;
import com.admas.management.modules.grading.dto.response.StudentAvailableCourseDTO;
import com.admas.management.modules.grading.dto.response.RegisteredCourseDTO;
import com.admas.management.modules.registration.dto.response.RegistrationSummaryDTO;
import com.admas.management.modules.registration.service.StudentCourseRegistrationService;
import com.admas.management.modules.infrastructure.security.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class StudentCourseRegistrationController {

    private final StudentCourseRegistrationService registrationService;
    private final SecurityService securityService;

    /**
     * Get available courses for a student (filtered by department and academic year)
     */
    @GetMapping("/students/{studentId}/available-courses")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<StudentAvailableCourseDTO>> getAvailableCourses(
            @PathVariable Long studentId,
            @RequestParam String semester,
            @RequestParam Integer academicYear) {

        // Security check - students can only see their own courses
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }

        List<StudentAvailableCourseDTO> courses = registrationService.getAvailableCoursesForStudent(studentId, semester, academicYear);
        return ResponseEntity.ok(courses);
    }

    /**
     * Register for a course
     */
    @PostMapping("/courses/register")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<RegisteredCourseDTO> registerCourse(@Valid @RequestBody CourseRegistrationRequestDTO request) {
        // Security check - students can only register themselves
        if (!securityService.isStudentOwner(request.getStudentId())) {
            throw new RuntimeException("Access denied");
        }

        RegisteredCourseDTO response = registrationService.registerCourse(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get student's registered courses for a semester
     */
    @GetMapping("/students/{studentId}/registered-courses")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<RegisteredCourseDTO>> getRegisteredCourses(
            @PathVariable Long studentId,
            @RequestParam String semester,
            @RequestParam Integer academicYear) {

        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }

        List<RegisteredCourseDTO> courses = registrationService.getStudentRegisteredCourses(studentId, semester, academicYear);
        return ResponseEntity.ok(courses);
    }

    /**
     * Get registration summary for a student
     */
    @GetMapping("/students/{studentId}/summary")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<RegistrationSummaryDTO> getRegistrationSummary(
            @PathVariable Long studentId,
            @RequestParam String semester,
            @RequestParam Integer academicYear) {

        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }

        RegistrationSummaryDTO summary = registrationService.getRegistrationSummary(studentId, semester, academicYear);
        return ResponseEntity.ok(summary);
    }

    /**
     * Drop a course
     */
    @DeleteMapping("/courses/drop")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<Void> dropCourse(
            @RequestParam Long studentId,
            @RequestParam Long courseId,
            @RequestParam String semester,
            @RequestParam Integer academicYear,
            @RequestParam(required = false) String reason) {

        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }

        registrationService.dropCourse(studentId, courseId, semester, academicYear, reason);
        return ResponseEntity.noContent().build();
    }
}