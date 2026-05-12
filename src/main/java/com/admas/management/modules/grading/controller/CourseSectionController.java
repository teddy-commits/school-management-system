package com.admas.management.modules.grading.controller;

import com.admas.management.modules.grading.dto.request.CourseSectionRequestDTO;
import com.admas.management.modules.grading.dto.response.SectionCourseDetailDTO;
import com.admas.management.modules.grading.dto.request.SectionCourseRequestDTO;
import com.admas.management.modules.grading.dto.request.SectionInstructorRequestDTO;
import com.admas.management.modules.grading.dto.response.*;
import com.admas.management.modules.grading.model.entity.SectionCourse;
import com.admas.management.modules.grading.service.CourseSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.admas.management.modules.grading.service.EnrollmentService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/grading/sections")
@RequiredArgsConstructor
public class CourseSectionController {

    private final CourseSectionService sectionService;
    private final EnrollmentService enrollmentService;

    // Create section
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<CourseSectionResponseDTO> createSection(@Valid @RequestBody CourseSectionRequestDTO request) {
        CourseSectionResponseDTO response = sectionService.createSection(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all sections (for admin view)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<CourseSectionResponseDTO>> getAllSections() {
        // You need to implement this in service
        List<CourseSectionResponseDTO> sections = sectionService.getAllSections();
        return ResponseEntity.ok(sections);
    }
    // Add this method to your CourseSectionController

    // Get sections by semester and academic year
    @GetMapping("/semester")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR', 'MANAGEMENT')")
    public ResponseEntity<List<CourseSectionResponseDTO>> getSectionsBySemester(
            @RequestParam String semester,
            @RequestParam Integer academicYear) {
        List<CourseSectionResponseDTO> sections = sectionService.getSectionsBySemester(semester, academicYear);
        return ResponseEntity.ok(sections);
    }

    // Get sections by course (for admin view)
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseSectionResponseDTO>> getSectionsByCourse(@PathVariable Long courseId) {
        List<CourseSectionResponseDTO> sections = sectionService.getSectionsByCourse(courseId);
        return ResponseEntity.ok(sections);
    }

    // Get instructor's sections (for instructor dashboard)
    @GetMapping("/instructor/current")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<List<CourseSectionResponseDTO>> getMySections(
            @RequestParam String semester,
            @RequestParam Integer academicYear,
            Authentication authentication) {
        String instructorEmail = authentication.getName();
        List<CourseSectionResponseDTO> sections = sectionService.getSectionsByInstructorEmail(instructorEmail, semester, academicYear);
        return ResponseEntity.ok(sections);
    }

    // Get open sections for student registration
    @GetMapping("/open")
    public ResponseEntity<List<CourseSectionResponseDTO>> getOpenSections(
            @RequestParam String semester,
            @RequestParam Integer academicYear) {
        List<CourseSectionResponseDTO> sections = sectionService.getOpenSectionsBySemester(semester, academicYear);
        return ResponseEntity.ok(sections);
    }

    // Get section by ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseSectionResponseDTO> getSectionById(@PathVariable Long id) {
        CourseSectionResponseDTO response = sectionService.getSectionById(id);
        return ResponseEntity.ok(response);
    }

    // Update section
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<CourseSectionResponseDTO> updateSection(
            @PathVariable Long id,
            @Valid @RequestBody CourseSectionRequestDTO request) {
        CourseSectionResponseDTO response = sectionService.updateSection(id, request);
        return ResponseEntity.ok(response);
    }

    // Delete section
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }

    // Update section status
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<CourseSectionResponseDTO> updateSectionStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        sectionService.updateSectionStatus(id, status);
        CourseSectionResponseDTO section = sectionService.getSectionById(id);
        return ResponseEntity.ok(section);
    }
    // Add to CourseSectionController.java
    @PostMapping("/{sectionId}/instructors")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<SectionInstructorResponseDTO> addInstructorToSection(
            @PathVariable Long sectionId,
            @Valid @RequestBody SectionInstructorRequestDTO request) {

        SectionInstructorResponseDTO result = sectionService.addInstructorToSection(
                sectionId,
                request.getInstructorId(),
                request.getCourseId());
        return ResponseEntity.ok(result);
    }
    @DeleteMapping("/instructors/{sectionInstructorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<Void> removeInstructorFromSection(@PathVariable Long sectionInstructorId) {
        sectionService.removeInstructorFromSection(sectionInstructorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sectionId}/instructors")
    public ResponseEntity<List<SectionInstructorDTO>> getSectionInstructors(@PathVariable Long sectionId) {
        List<SectionInstructorDTO> instructors = sectionService.getSectionInstructors(sectionId);
        return ResponseEntity.ok(instructors);
    }
    @PostMapping("/{sectionId}/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<SectionCourseResponseDTO> addCourseToSection(
            @PathVariable Long sectionId,
            @Valid @RequestBody SectionCourseRequestDTO request) {

       SectionCourseResponseDTO result = sectionService.addCourseToSection(
                sectionId,
                request.getCourseId(),
                request.getSchedule(),
                request.getRoom());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/courses/{sectionCourseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<Void> removeCourseFromSection(@PathVariable Long sectionCourseId) {
        sectionService.removeCourseFromSection(sectionCourseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sectionId}/courses")
    public ResponseEntity<List<SectionCourseDTO>> getSectionCourses(@PathVariable Long sectionId) {
        List<SectionCourseDTO> courses = sectionService.getSectionCourses(sectionId);
        return ResponseEntity.ok(courses);
    }
    // Get instructor's courses across all their sections
    @GetMapping("/instructor/my-courses")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<List<SectionCourseDetailDTO>> getMyCourses(
            @RequestParam String semester,
            @RequestParam Integer academicYear,
            Authentication authentication) {
        String instructorEmail = authentication.getName();

        // ✅ Match the return types - both should be SectionCourseDetailDTO
        List<SectionCourseDetailDTO> courses = sectionService.getCoursesByInstructorEmail(instructorEmail, semester, academicYear);

        return ResponseEntity.ok(courses);
    }
    @GetMapping("/{sectionId}/enrollments")
    public ResponseEntity<List<EnrollmentResponseDTO>> getSectionEnrollments(
            @PathVariable Long sectionId,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Integer academicYear) {

        // Get all courses in this section
        List<SectionCourse> sectionCourses = sectionService.getSectionCoursesEntities(sectionId);

        List<EnrollmentResponseDTO> enrollments = new ArrayList<>();

        // Get enrollments for each course
        for (SectionCourse sc : sectionCourses) {
            List<EnrollmentResponseDTO> courseEnrollments = enrollmentService
                    .getEnrollmentsByCourseAndSemester(sc.getCourse().getId(), semester, academicYear);
            enrollments.addAll(courseEnrollments);
        }

        // Remove duplicates (same student enrolled in multiple courses of same section)
        enrollments = enrollments.stream()
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(enrollments);
    }
}