package com.admas.management.modules.registration.controller;

import com.admas.management.modules.registration.dto.request.StudentRegistrationRequest;
import com.admas.management.modules.registration.dto.request.StudentUpdateRequest;
import com.admas.management.modules.registration.dto.response.StudentProfileResponse;
import com.admas.management.modules.registration.dto.response.StudentRegistrationResponse;
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
@RequestMapping("/api/v1/registration/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentRegistrationController {

    private final StudentRegistrationService studentService;

    // Register a new student
    @PostMapping("/register")
    public ResponseEntity<StudentRegistrationResponse> registerStudent(
            @Valid @RequestBody StudentRegistrationRequest request) {
        StudentRegistrationResponse response = studentService.registerStudent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<StudentProfileResponse> getStudentById(@PathVariable Long id) {
        StudentProfileResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }

    // Get student by Student ID (STU20240001)
    @GetMapping("/student-id/{studentId}")
    public ResponseEntity<StudentProfileResponse> getStudentByStudentId(@PathVariable String studentId) {
        StudentProfileResponse response = studentService.getStudentByStudentId(studentId);
        return ResponseEntity.ok(response);
    }

    // Get student by email
    @GetMapping("/email/{email}")
    public ResponseEntity<StudentProfileResponse> getStudentByEmail(@PathVariable String email) {
        StudentProfileResponse response = studentService.getStudentByEmail(email);
        return ResponseEntity.ok(response);
    }

    // Get all students
    @GetMapping
    public ResponseEntity<List<StudentProfileResponse>> getAllStudents() {
        List<StudentProfileResponse> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    // Get students by department
    @GetMapping("/department/{department}")
    public ResponseEntity<List<StudentProfileResponse>> getStudentsByDepartment(@PathVariable String department) {
        List<StudentProfileResponse> students = studentService.getStudentsByDepartment(department);
        return ResponseEntity.ok(students);
    }

    // Get students by faculty
    @GetMapping("/faculty/{faculty}")
    public ResponseEntity<List<StudentProfileResponse>> getStudentsByFaculty(@PathVariable String faculty) {
        List<StudentProfileResponse> students = studentService.getStudentsByFaculty(faculty);
        return ResponseEntity.ok(students);
    }

    // Get students by enrollment year
    @GetMapping("/year/{year}")
    public ResponseEntity<List<StudentProfileResponse>> getStudentsByEnrollmentYear(@PathVariable Integer year) {
        List<StudentProfileResponse> students = studentService.getStudentsByEnrollmentYear(year);
        return ResponseEntity.ok(students);
    }

    // Update student
    @PutMapping("/{id}")
    public ResponseEntity<StudentProfileResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentUpdateRequest request) {
        StudentProfileResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(response);
    }

    // Deactivate student
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateStudent(@PathVariable Long id) {
        studentService.deactivateStudent(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Student deactivated successfully");
        return ResponseEntity.ok(response);
    }

    // Activate student
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Map<String, String>> activateStudent(@PathVariable Long id) {
        studentService.activateStudent(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Student activated successfully");
        return ResponseEntity.ok(response);
    }

    // Get total student count
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalStudentCount() {
        long count = studentService.getTotalStudentCount();
        Map<String, Long> response = new HashMap<>();
        response.put("totalStudents", count);
        return ResponseEntity.ok(response);
    }

    // Search students
    @GetMapping("/search")
    public ResponseEntity<List<StudentProfileResponse>> searchStudents(@RequestParam String keyword) {
        List<StudentProfileResponse> students = studentService.searchStudents(keyword);
        return ResponseEntity.ok(students);
    }
}