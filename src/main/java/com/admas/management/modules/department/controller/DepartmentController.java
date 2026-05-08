package com.admas.management.modules.department.controller;

import com.admas.management.modules.department.dto.request.DepartmentRequestDTO;
import com.admas.management.modules.department.dto.response.DepartmentResponseDTO;
import com.admas.management.modules.department.service.DepartmentService;
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
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@Valid @RequestBody DepartmentRequestDTO request) {
        DepartmentResponseDTO response = departmentService.createDepartment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequestDTO request) {
        DepartmentResponseDTO response = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentById(@PathVariable Long id) {
        DepartmentResponseDTO response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentByCode(@PathVariable String code) {
        DepartmentResponseDTO response = departmentService.getDepartmentByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        List<DepartmentResponseDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/active")
    public ResponseEntity<List<DepartmentResponseDTO>> getActiveDepartments() {
        List<DepartmentResponseDTO> departments = departmentService.getActiveDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/faculty/{faculty}")
    public ResponseEntity<List<DepartmentResponseDTO>> getDepartmentsByFaculty(@PathVariable String faculty) {
        List<DepartmentResponseDTO> departments = departmentService.getDepartmentsByFaculty(faculty);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DepartmentResponseDTO>> searchDepartments(@RequestParam String keyword) {
        List<DepartmentResponseDTO> departments = departmentService.searchDepartments(keyword);
        return ResponseEntity.ok(departments);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<Map<String, String>> activateDepartment(@PathVariable Long id) {
        departmentService.activateDepartment(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Department activated successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<Map<String, String>> deactivateDepartment(@PathVariable Long id) {
        departmentService.deactivateDepartment(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Department deactivated successfully");
        return ResponseEntity.ok(response);
    }
}