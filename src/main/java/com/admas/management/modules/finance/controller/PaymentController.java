package com.admas.management.modules.finance.controller;

import com.admas.management.modules.finance.dto.request.PaymentRequestDTO;
import com.admas.management.modules.finance.dto.response.FinancialReportDTO;
import com.admas.management.modules.finance.dto.response.PaymentResponseDTO;
import com.admas.management.modules.finance.service.PaymentService;
import com.admas.management.modules.finance.validator.PaymentValidator;
import com.admas.management.modules.infrastructure.security.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentValidator paymentValidator;
    private final SecurityService securityService;

    // Process Payment
    @PostMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<?> processPayment(@Valid @RequestBody PaymentRequestDTO request, Authentication authentication) {
        // Validate
        List<String> errors = paymentValidator.validatePaymentRequest(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        String receivedBy = authentication.getName();
        PaymentResponseDTO response = paymentService.processPayment(request, receivedBy);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Process Partial Payment
    @PostMapping("/payments/partial")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<PaymentResponseDTO> processPartialPayment(@Valid @RequestBody PaymentRequestDTO request, Authentication authentication) {
        String receivedBy = authentication.getName();
        PaymentResponseDTO response = paymentService.processPartialPayment(request, receivedBy);
        return ResponseEntity.ok(response);
    }

    // Refund Payment
    @PostMapping("/payments/{paymentId}/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER')")
    public ResponseEntity<PaymentResponseDTO> refundPayment(@PathVariable Long paymentId, @RequestParam String reason) {
        PaymentResponseDTO response = paymentService.refundPayment(paymentId, reason);
        return ResponseEntity.ok(response);
    }

    // Get Student Payments
    @GetMapping("/students/{studentId}/payments")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<List<PaymentResponseDTO>> getStudentPayments(@PathVariable Long studentId) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        return ResponseEntity.ok(paymentService.getStudentPayments(studentId));
    }

    // Get All Payments (Admin only)
    @GetMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // Get Payment by ID
    @GetMapping("/payments/{paymentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    // Get Payments by Date Range
    @GetMapping("/payments/report/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(paymentService.getPaymentsByDateRange(startDate, endDate));
    }

    // Generate Payment Receipt
    @GetMapping("/payments/{paymentId}/receipt")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<byte[]> generateReceipt(@PathVariable Long paymentId) {
        byte[] receipt = paymentService.generateReceipt(paymentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "receipt_" + paymentId + ".pdf");

        return new ResponseEntity<>(receipt, headers, HttpStatus.OK);
    }

    // Daily Financial Report
    @GetMapping("/reports/daily")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<FinancialReportDTO> generateDailyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(paymentService.generateDailyReport(date));
    }

    // Monthly Financial Report
    @GetMapping("/reports/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<FinancialReportDTO> generateMonthlyReport(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ResponseEntity.ok(paymentService.generateMonthlyReport(year, month));
    }

    // Semester Financial Report
    @GetMapping("/reports/semester")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<FinancialReportDTO> generateSemesterReport(
            @RequestParam String semester,
            @RequestParam Integer academicYear) {
        return ResponseEntity.ok(paymentService.generateSemesterReport(semester, academicYear));
    }

    // Department Financial Report
    @GetMapping("/reports/department")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<FinancialReportDTO> generateDepartmentReport(
            @RequestParam String department,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(paymentService.generateDepartmentReport(department, startDate, endDate));
    }
}
