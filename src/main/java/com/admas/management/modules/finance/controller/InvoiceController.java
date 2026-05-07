package com.admas.management.modules.finance.controller;


import com.admas.management.modules.finance.dto.response.InvoiceResponseDTO;
import com.admas.management.modules.finance.service.InvoiceService;
import com.admas.management.modules.infrastructure.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final SecurityService securityService;

    @PostMapping("/students/{studentId}/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<InvoiceResponseDTO> generateInvoice(
            @PathVariable Long studentId,
            @RequestParam String semester,
            @RequestParam Integer academicYear) {
        return ResponseEntity.ok(invoiceService.generateInvoice(studentId, semester, academicYear));
    }

    @GetMapping("/students/{studentId}/invoices")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<List<InvoiceResponseDTO>> getStudentInvoices(@PathVariable Long studentId) {
        if (!securityService.isStudentOwner(studentId)) {
            throw new RuntimeException("Access denied");
        }
        return ResponseEntity.ok(invoiceService.getStudentInvoices(studentId));
    }

    @GetMapping("/invoices/{invoiceId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceById(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }

    @GetMapping("/invoices/number/{invoiceNumber}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(invoiceService.getInvoiceByNumber(invoiceNumber));
    }
    @GetMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<List<InvoiceResponseDTO>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/invoices/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<List<InvoiceResponseDTO>> getOverdueInvoices() {
        return ResponseEntity.ok(invoiceService.getOverdueInvoices());
    }

    @GetMapping("/invoices/{invoiceId}/download")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long invoiceId) {
        byte[] pdf = invoiceService.generateInvoicePDF(invoiceId);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + invoiceId + ".pdf");

        return new ResponseEntity<>(pdf, headers, org.springframework.http.HttpStatus.OK);
    }
}
