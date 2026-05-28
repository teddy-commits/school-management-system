package com.admas.management.modules.grading.controller;

import com.admas.management.modules.grading.dto.request.CourseRequestDTO;
import com.admas.management.modules.grading.dto.response.CourseResponseDTO;
import com.admas.management.modules.grading.model.enums.Semester;
import com.admas.management.modules.grading.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/grading/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseRequestDTO requestDTO) {
        CourseResponseDTO response = courseService.createCourse(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<CourseResponseDTO> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequestDTO requestDTO) {
        CourseResponseDTO response = courseService.updateCourse(id, requestDTO);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        CourseResponseDTO response = courseService.getCourseById(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/instructor/my-courses")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<List<CourseResponseDTO>> getMyCourses(Authentication authentication) {
        String instructorEmail = authentication.getName();
        List<CourseResponseDTO> courses = courseService.getCoursesByInstructorDepartment(instructorEmail);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/instructor/available")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<List<CourseResponseDTO>> getAvailableCoursesForInstructor(Authentication authentication) {
        String instructorEmail = authentication.getName();
        List<CourseResponseDTO> courses = courseService.getAvailableCoursesForInstructor(instructorEmail);
        return ResponseEntity.ok(courses);
    }
    @GetMapping("/code/{courseCode}")
    public ResponseEntity<CourseResponseDTO> getCourseByCode(@PathVariable String courseCode) {
        CourseResponseDTO response = courseService.getCourseByCode(courseCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MANAGEMENT')")
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        List<CourseResponseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByDepartment(@PathVariable String department) {
        List<CourseResponseDTO> courses = courseService.getCoursesByDepartment(department);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/faculty/{faculty}")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByFaculty(@PathVariable String faculty) {
        List<CourseResponseDTO> courses = courseService.getCoursesByFaculty(faculty);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/semester")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesBySemester(
            @RequestParam Semester semester,
            @RequestParam Integer academicYear) {
        List<CourseResponseDTO> courses = courseService.getCoursesBySemester(semester, academicYear);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/instructor/{instructorEmail}")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByInstructor(@PathVariable String instructorEmail) {
        List<CourseResponseDTO> courses = courseService.getCoursesByInstructor(instructorEmail);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseResponseDTO>> searchCourses(@RequestParam String keyword) {
        List<CourseResponseDTO> courses = courseService.searchCourses(keyword);
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<Void> updateCourseStatus(@PathVariable Long id, @RequestParam String status) {
        courseService.updateCourseStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
