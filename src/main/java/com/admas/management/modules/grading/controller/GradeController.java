package com.admas.management.modules.grading.controller;

import com.admas.management.modules.grading.dto.request.GradeSubmissionDTO;
import com.admas.management.modules.grading.dto.response.GradeResponseDTO;
import com.admas.management.modules.grading.dto.response.TranscriptResponseDTO;
import com.admas.management.modules.grading.service.GradeService;
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
@RequestMapping("/grading")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;
    private final SecurityService securityService;
    private final UserRepository userRepository;
    @PostMapping("/grades/submit")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<GradeResponseDTO> submitGrade(
            @Valid @RequestBody GradeSubmissionDTO gradeDTO,
            Authentication authentication) {
        String instructorEmail = authentication.getName();
        GradeResponseDTO response = gradeService.submitGrade(gradeDTO, instructorEmail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/grades/{gradeId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<GradeResponseDTO> updateGrade(
            @PathVariable Long gradeId,
            @Valid @RequestBody GradeSubmissionDTO gradeDTO) {
        GradeResponseDTO response = gradeService.updateGrade(gradeId, gradeDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/students/{studentId}/grades")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'PROFESSOR', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<GradeResponseDTO>> getStudentGrades(
            @PathVariable Long studentId,
            Authentication authentication) {

        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.isStudent() && !currentUser.getId().equals(studentId)) {
            throw new RuntimeException("Access denied");
        }

        List<GradeResponseDTO> grades = gradeService.getStudentGrades(studentId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/courses/{courseCode}/grades")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<?> getCourseGrades(@PathVariable String courseCode) {
        if ("ALL".equalsIgnoreCase(courseCode)) {
            return ResponseEntity.ok(List.of());
        }

        List<GradeResponseDTO> grades = gradeService.getCourseGrades(courseCode);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/students/{studentId}/transcript")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<TranscriptResponseDTO> generateTranscript(@PathVariable Long studentId) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        TranscriptResponseDTO transcript = gradeService.generateTranscript(studentId);
        return ResponseEntity.ok(transcript);
    }

    @GetMapping("/students/{studentId}/cgpa")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'PROFESSOR', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<Double> getStudentCGPA(
            @PathVariable Long studentId,
            Authentication authentication) {

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (currentUser.isStudent() && !currentUser.getId().equals(studentId)) {
            throw new RuntimeException("Access denied");
        }

        Double cgpa = gradeService.calculateStudentCGPA(studentId);
        return ResponseEntity.ok(cgpa);
    }

    @PostMapping("/courses/{courseCode}/publish")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'PROFESSOR', 'ADMIN')")
    public ResponseEntity<String> publishGrades(@PathVariable String courseCode) {
        gradeService.publishGrades(courseCode);
        return ResponseEntity.ok("Grades published successfully for course: " + courseCode);
    }
}
