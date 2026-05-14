package com.admas.management.modules.registration.controller;

import com.admas.management.modules.registration.dto.request.SemesterRegistrationRequestDTO;
import com.admas.management.modules.registration.dto.response.SemesterRegistrationResponseDTO;
import com.admas.management.modules.registration.service.SemesterRegistrationService;
import com.admas.management.modules.infrastructure.security.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/semester-registration")
@RequiredArgsConstructor
public class SemesterRegistrationController {

    private final SemesterRegistrationService registrationService;
    private final SecurityService securityService;
    @PostMapping("/initiate")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<SemesterRegistrationResponseDTO> initiateRegistration(
            @Valid @RequestBody SemesterRegistrationRequestDTO request) {

        if (!securityService.isStudentOwner(request.getStudentId())) {
            throw new RuntimeException("Access denied");
        }

        SemesterRegistrationResponseDTO response = registrationService.initiateRegistration(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/{registrationId}/courses")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<SemesterRegistrationResponseDTO> addCourses(
            @PathVariable Long registrationId,
            @RequestBody List<Long> courseIds) {

        SemesterRegistrationResponseDTO response = registrationService.addCourses(registrationId, courseIds);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{registrationId}/courses/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<SemesterRegistrationResponseDTO> removeCourse(
            @PathVariable Long registrationId,
            @PathVariable Long courseId) {

        SemesterRegistrationResponseDTO response = registrationService.removeCourse(registrationId, courseId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{registrationId}/complete")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<SemesterRegistrationResponseDTO> completeRegistration(@PathVariable Long registrationId) {
        SemesterRegistrationResponseDTO response = registrationService.completeRegistration(registrationId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{registrationId}/pay")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'MANAGEMENT')")
    public ResponseEntity<SemesterRegistrationResponseDTO> processPayment(
            @PathVariable Long registrationId,
            @RequestParam String paymentReference,
            @RequestParam Double amount) {

        SemesterRegistrationResponseDTO response = registrationService.processPayment(registrationId, paymentReference, amount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<SemesterRegistrationResponseDTO>> getStudentRegistrations(@PathVariable Long studentId) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        return ResponseEntity.ok(registrationService.getStudentRegistrations(studentId));
    }
    @GetMapping("/students/{studentId}/current")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<SemesterRegistrationResponseDTO> getCurrentRegistration(@PathVariable Long studentId) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        SemesterRegistrationResponseDTO response = registrationService.getCurrentSemesterRegistration(studentId);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @GetMapping("/students/{studentId}/can-register")
    public ResponseEntity<Map<String, Object>> canRegister(
            @PathVariable Long studentId,
            @RequestParam String semester,
            @RequestParam Integer academicYear) {

        boolean canRegister = registrationService.canRegisterForSemester(studentId, semester, academicYear);
        Map<String, Object> response = new HashMap<>();
        response.put("canRegister", canRegister);
        response.put("semester", semester);
        response.put("academicYear", academicYear);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<SemesterRegistrationResponseDTO>> getRegistrationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(registrationService.getRegistrationsByStatus(status));
    }
}