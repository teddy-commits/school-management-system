package com.admas.management.modules.registration.controller;

import com.admas.management.modules.registration.dto.request.AssignmentRequestDTO;
import com.admas.management.modules.registration.dto.response.AssignmentResultDTO;
import com.admas.management.modules.registration.dto.response.StudentPreviewDTO;
import com.admas.management.modules.registration.service.CourseAssignmentService;
import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.dto.response.CourseResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/registration/course-assignments")
@RequiredArgsConstructor
public class CourseAssignmentController {

    private final CourseAssignmentService assignmentService;

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<AssignmentResultDTO> assignCourses(@RequestBody AssignmentRequestDTO request) {
        AssignmentResultDTO result = assignmentService.assignCoursesToDepartmentYear(
                request.getDepartmentId(),
                request.getAcademicYearLevel(),
                request.getSemester(),
                request.getAcademicYear(),
                request.getCourseIds()
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/students/preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<StudentPreviewDTO>> getStudentsPreview(
            @RequestParam Long departmentId,
            @RequestParam Integer academicYearLevel) {
        return ResponseEntity.ok(assignmentService.getStudentsByDepartmentAndYear(departmentId, academicYearLevel));
    }

    @GetMapping("/assigned-courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<CourseResponseDTO>> getAssignedCourses(
            @RequestParam Long departmentId,
            @RequestParam Integer academicYearLevel,
            @RequestParam String semester,
            @RequestParam Integer academicYear) {

        List<Course> courses = assignmentService.getAssignedCoursesForDepartmentYear(
                departmentId, academicYearLevel, semester, academicYear);

        List<CourseResponseDTO> response = courses.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private CourseResponseDTO toDTO(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .credits(course.getCredits())
                .build();
    }
}
