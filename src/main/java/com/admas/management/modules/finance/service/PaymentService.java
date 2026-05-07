package com.admas.management.modules.finance.service;


import com.admas.management.modules.finance.dto.request.PaymentRequestDTO;
import com.admas.management.modules.finance.dto.response.FinancialReportDTO;
import com.admas.management.modules.finance.dto.response.PaymentResponseDTO;
import com.admas.management.modules.finance.model.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    PaymentResponseDTO processPayment(PaymentRequestDTO request, String receivedBy);
    PaymentResponseDTO processPartialPayment(PaymentRequestDTO request, String receivedBy);
    PaymentResponseDTO refundPayment(Long paymentId, String reason);

    List<PaymentResponseDTO> getStudentPayments(Long studentId);
    List<PaymentResponseDTO> getAllPayments();
    PaymentResponseDTO getPaymentById(Long paymentId);
    List<PaymentResponseDTO> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<PaymentResponseDTO> getPaymentsByMethod(PaymentMethod paymentMethod);

    byte[] generateReceipt(Long paymentId);
    String getReceiptNumber();

    FinancialReportDTO generateDailyReport(LocalDateTime date);
    FinancialReportDTO generateMonthlyReport(Integer year, Integer month);
    FinancialReportDTO generateSemesterReport(String semester, Integer academicYear);
    FinancialReportDTO generateDepartmentReport(String department, LocalDateTime startDate, LocalDateTime endDate);
}
