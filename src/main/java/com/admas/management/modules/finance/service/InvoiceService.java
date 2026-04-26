package com.admas.management.modules.finance.service;


import com.admas.management.modules.finance.dto.response.InvoiceResponseDTO;

import java.util.List;

public interface InvoiceService {

    // Generate Invoice
    InvoiceResponseDTO generateInvoice(Long studentId, String semester, Integer academicYear);

    // Get Invoices
    List<InvoiceResponseDTO> getStudentInvoices(Long studentId);
    InvoiceResponseDTO getInvoiceById(Long invoiceId);
    InvoiceResponseDTO getInvoiceByNumber(String invoiceNumber);
    List<InvoiceResponseDTO> getAllInvoices();
    List<InvoiceResponseDTO> getOverdueInvoices();

    // Invoice Management
    InvoiceResponseDTO updateInvoiceStatus(Long invoiceId, String status);
    void sendInvoiceEmail(Long invoiceId);

    // PDF Generation
    byte[] generateInvoicePDF(Long invoiceId);

    // Delete Invoice (Admin only)
    void deleteInvoice(Long invoiceId);
}