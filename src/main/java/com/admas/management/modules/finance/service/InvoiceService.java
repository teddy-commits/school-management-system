package com.admas.management.modules.finance.service;


import com.admas.management.modules.finance.dto.response.InvoiceResponseDTO;

import java.util.List;

public interface InvoiceService {

    InvoiceResponseDTO generateInvoice(Long studentId, String semester, Integer academicYear);

    List<InvoiceResponseDTO> getStudentInvoices(Long studentId);
    InvoiceResponseDTO getInvoiceById(Long invoiceId);
    InvoiceResponseDTO getInvoiceByNumber(String invoiceNumber);
    List<InvoiceResponseDTO> getAllInvoices();
    List<InvoiceResponseDTO> getOverdueInvoices();

    InvoiceResponseDTO updateInvoiceStatus(Long invoiceId, String status);
    void sendInvoiceEmail(Long invoiceId);

    byte[] generateInvoicePDF(Long invoiceId);
    void deleteInvoice(Long invoiceId);
}