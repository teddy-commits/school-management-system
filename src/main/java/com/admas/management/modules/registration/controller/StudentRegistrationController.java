package com.admas.management.modules.registration.controller;

import com.admas.management.modules.registration.dto.request.StudentRegistrationRequest;
import com.admas.management.modules.registration.dto.request.StudentUpdateRequest;
import com.admas.management.modules.registration.dto.response.StudentProfileResponse;
import com.admas.management.modules.registration.dto.response.StudentRegistrationResponse;
import com.admas.management.modules.registration.service.RegistrationSessionService;
import com.admas.management.modules.registration.service.StudentRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/registration/students/")
@RequiredArgsConstructor
public class StudentRegistrationController {

    private final StudentRegistrationService studentService;
    private final RegistrationSessionService sessionService;

    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@Valid @RequestBody StudentRegistrationRequest request) {
        if (!sessionService.isRegistrationOpen()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Registration is currently CLOSED");
            error.put("message", "Student registration is only available during open registration periods");
            error.put("status", "CLOSED");
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        var currentSession = sessionService.getCurrentOpenSession();

        StudentRegistrationResponse response = studentService.registerStudent(request);
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("registration", response);
        successResponse.put("session", Map.of(
                "semester", currentSession.getSemester(),
                "academicYear", currentSession.getAcademicYear(),
                "endDate", currentSession.getEndDate()
        ));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
   @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getRegistrationStatus() {
        boolean isOpen = sessionService.isRegistrationOpen();
        Map<String, Object> response = new HashMap<>();
        response.put("isOpen", isOpen);

        if (isOpen) {
            var session = sessionService.getCurrentOpenSession();
            response.put("semester", session.getSemester());
            response.put("academicYear", session.getAcademicYear());
            response.put("startDate", session.getStartDate());
            response.put("endDate", session.getEndDate());
            response.put("message", "Registration is currently OPEN");
        } else {
            response.put("message", "Registration is currently CLOSED");
            var upcomingSessions = sessionService.getUpcomingSessions();
            if (!upcomingSessions.isEmpty()) {
                var nextSession = upcomingSessions.get(0);
                response.put("nextRegistrationStart", nextSession.getStartDate());
                response.put("nextSemester", nextSession.getSemester());
                response.put("nextAcademicYear", nextSession.getAcademicYear());
            }
        }

        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<StudentProfileResponse> getStudentById(@PathVariable Long id) {
        StudentProfileResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/student-id/{studentId}")
    public ResponseEntity<StudentProfileResponse> getStudentByStudentId(@PathVariable String studentId) {
        StudentProfileResponse response = studentService.getStudentByStudentId(studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<StudentProfileResponse> getStudentByEmail(@PathVariable String email) {
        StudentProfileResponse response = studentService.getStudentByEmail(email);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<List<StudentProfileResponse>> getAllStudents() {
        List<StudentProfileResponse> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<StudentProfileResponse>> getStudentsByDepartment(@PathVariable String department) {
        List<StudentProfileResponse> students = studentService.getStudentsByDepartment(department);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/faculty/{faculty}")
    public ResponseEntity<List<StudentProfileResponse>> getStudentsByFaculty(@PathVariable String faculty) {
        List<StudentProfileResponse> students = studentService.getStudentsByFaculty(faculty);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<StudentProfileResponse>> getStudentsByEnrollmentYear(@PathVariable Integer year) {
        List<StudentProfileResponse> students = studentService.getStudentsByEnrollmentYear(year);
        return ResponseEntity.ok(students);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentProfileResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentUpdateRequest request) {
        StudentProfileResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateStudent(@PathVariable Long id) {
        studentService.deactivateStudent(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Student deactivated successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Map<String, String>> activateStudent(@PathVariable Long id) {
        studentService.activateStudent(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Student activated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalStudentCount() {
        long count = studentService.getTotalStudentCount();
        Map<String, Long> response = new HashMap<>();
        response.put("totalStudents", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudentProfileResponse>> searchStudents(@RequestParam String keyword) {
        List<StudentProfileResponse> students = studentService.searchStudents(keyword);
        return ResponseEntity.ok(students);
    }
}