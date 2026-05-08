package com.admas.management.modules.finance.controller;

import com.admas.management.modules.finance.dto.response.FeeResponseDTO;
import com.admas.management.modules.finance.dto.response.FeeSummaryDTO;
import com.admas.management.modules.finance.dto.request.FeeStructureRequestDTO;
import com.admas.management.modules.finance.service.FeeService;
import com.admas.management.modules.infrastructure.security.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
public class FeeController {

    private final FeeService feeService;
    private final SecurityService securityService;

    @PostMapping("/fee-structures")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<FeeResponseDTO> createFeeStructure(@Valid @RequestBody FeeStructureRequestDTO request) {
        FeeResponseDTO response = feeService.createFeeStructure(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/fee-structures")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<List<FeeResponseDTO>> getAllFeeStructures() {
        return ResponseEntity.ok(feeService.getAllFeeStructures());
    }

    @PostMapping("/students/{studentId}/fees")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<FeeResponseDTO> generateStudentFee(
            @PathVariable Long studentId,
            @RequestParam Long feeStructureId,
            @RequestParam String semester,
            @RequestParam Integer academicYear) {
        FeeResponseDTO response = feeService.generateFeeForStudent(studentId, feeStructureId, semester, academicYear);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/students/{studentId}/fees")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<List<FeeResponseDTO>> getStudentFees(@PathVariable Long studentId) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        return ResponseEntity.ok(feeService.getStudentFees(studentId));
    }

    @GetMapping("/students/{studentId}/summary")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<FeeSummaryDTO> getStudentFeeSummary(@PathVariable Long studentId) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        return ResponseEntity.ok(feeService.getStudentFeeSummary(studentId));
    }

    @GetMapping("/fees/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<List<FeeResponseDTO>> getOverdueFees() {
        return ResponseEntity.ok(feeService.getOverdueFees());
    }

    @PostMapping("/fees/{feeId}/apply-late-fee")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<FeeResponseDTO> applyLateFee(@PathVariable Long feeId) {
        return ResponseEntity.ok(feeService.applyLateFee(feeId));
    }

    @PostMapping("/fees/{feeId}/waive")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER')")
    public ResponseEntity<FeeResponseDTO> waiveFee(
            @PathVariable Long feeId,
            @RequestParam Double amount,
            @RequestParam String reason) {
        return ResponseEntity.ok(feeService.waiveFee(feeId, amount, reason));
    }
}