package com.admas.management.modules.grading.controller;

import com.admas.management.modules.grading.dto.request.CourseSectionRequestDTO;
import com.admas.management.modules.grading.model.dto.response.CourseSectionResponseDTO;
import com.admas.management.modules.grading.service.CourseSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grading/sections")
@RequiredArgsConstructor
public class CourseSectionController {

    private final CourseSectionService sectionService;

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
}