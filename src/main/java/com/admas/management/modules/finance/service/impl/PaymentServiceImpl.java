package com.admas.management.modules.finance.service.impl;

import com.admas.management.modules.finance.dto.request.PaymentRequestDTO;
import com.admas.management.modules.finance.dto.response.FinancialReportDTO;
import com.admas.management.modules.finance.dto.response.PaymentResponseDTO;
import com.admas.management.modules.finance.model.entity.Fee;
import com.admas.management.modules.finance.model.entity.Payment;
import com.admas.management.modules.finance.model.enums.PaymentMethod;
import com.admas.management.modules.finance.model.enums.PaymentStatus;
import com.admas.management.modules.finance.repository.FeeRepository;
import com.admas.management.modules.finance.repository.PaymentRepository;
import com.admas.management.modules.finance.service.PaymentService;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final FeeRepository feeRepository;
    private final UserRepository userRepository;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public PaymentResponseDTO processPayment(PaymentRequestDTO request, String receivedBy) {
        log.info("Processing payment for student: {}, amount: {}", request.getStudentId(), request.getAmount());

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Fee fee = null;
        if (request.getFeeId() != null) {
            fee = feeRepository.findById(request.getFeeId())
                    .orElseThrow(() -> new RuntimeException("Fee not found"));
        }

        Payment payment = new Payment();
        payment.setTransactionId(generateTransactionId());
        payment.setStudent(student);
        payment.setFee(fee);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PAID);
        payment.setReferenceNumber(request.getReferenceNumber());
        payment.setBankName(request.getBankName());
        payment.setChequeNumber(request.getChequeNumber());
        payment.setMobileNumber(request.getMobileNumber());
        payment.setRemarks(request.getRemarks());
        payment.setReceivedBy(receivedBy);
        payment.setPaymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDateTime.now());
        payment.setReceiptNumber(generateReceiptNumber());

        Payment savedPayment = paymentRepository.save(payment);
        if (fee != null) {
            fee.setPaidAmount(fee.getPaidAmount() + request.getAmount());
            fee.calculateDueAmount();
            feeRepository.save(fee);
        }

        return mapToResponseDTO(savedPayment, "Payment processed successfully");
    }

    @Override
    public PaymentResponseDTO processPartialPayment(PaymentRequestDTO request, String receivedBy) {
        PaymentResponseDTO response = processPayment(request, receivedBy);
        response.setMessage("Partial payment processed successfully");
        return response;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER')")
    public PaymentResponseDTO refundPayment(Long paymentId, String reason) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRemarks("REFUNDED: " + reason + " | " + (payment.getRemarks() != null ? payment.getRemarks() : ""));

        Payment updatedPayment = paymentRepository.save(payment);
        if (payment.getFee() != null) {
            Fee fee = payment.getFee();
            fee.setPaidAmount(fee.getPaidAmount() - payment.getAmount());
            fee.calculateDueAmount();
            feeRepository.save(fee);
        }

        return mapToResponseDTO(updatedPayment, "Payment refunded successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getStudentPayments(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return paymentRepository.findByStudent(student)
                .stream()
                .map(p -> mapToResponseDTO(p, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(p -> mapToResponseDTO(p, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return mapToResponseDTO(payment, null);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public List<PaymentResponseDTO> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate)
                .stream()
                .map(p -> mapToResponseDTO(p, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByMethod(PaymentMethod paymentMethod) {
        return paymentRepository.findByPaymentMethod(paymentMethod)
                .stream()
                .map(p -> mapToResponseDTO(p, null))
                .collect(Collectors.toList());
    }

    @Override
    public byte[] generateReceipt(Long paymentId) {
        log.info("Generating receipt for payment: {}", paymentId);
        return new byte[0];
    }

    @Override
    public String getReceiptNumber() {
        return "RCP-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public FinancialReportDTO generateDailyReport(LocalDateTime date) {
        LocalDateTime startOfDay = date.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = date.withHour(23).withMinute(59).withSecond(59);

        Double totalPayments = paymentRepository.getTotalPaymentsBetweenDates(startOfDay, endOfDay);
        List<Object[]> methodSummary = paymentRepository.getPaymentMethodSummary();

        Map<PaymentMethod, Double> paymentsByMethod = new HashMap<>();
        for (Object[] row : methodSummary) {
            paymentsByMethod.put((PaymentMethod) row[0], (Double) row[1]);
        }

        return FinancialReportDTO.builder()
                .reportDate(LocalDateTime.now())
                .reportPeriod("Daily - " + date.format(DateTimeFormatter.ISO_DATE))
                .totalPaymentsReceived(totalPayments != null ? totalPayments : 0.0)
                .paymentsByMethod(paymentsByMethod)
                .totalTransactions(paymentRepository.findByPaymentDateBetween(startOfDay, endOfDay).size())
                .message("Daily financial report generated")
                .build();
    }

    @Override
    public FinancialReportDTO generateMonthlyReport(Integer year, Integer month) {
        return FinancialReportDTO.builder()
                .reportDate(LocalDateTime.now())
                .reportPeriod(String.format("Monthly - %d-%02d", year, month))
                .message("Monthly report generated")
                .build();
    }

    @Override
    public FinancialReportDTO generateSemesterReport(String semester, Integer academicYear) {
        return FinancialReportDTO.builder()
                .reportDate(LocalDateTime.now())
                .reportPeriod(String.format("Semester - %s %d", semester, academicYear))
                .message("Semester report generated")
                .build();
    }

    @Override
    public FinancialReportDTO generateDepartmentReport(String department, LocalDateTime startDate, LocalDateTime endDate) {
        return FinancialReportDTO.builder()
                .reportDate(LocalDateTime.now())
                .reportPeriod(String.format("Department - %s (%s to %s)",
                        department, startDate.toLocalDate(), endDate.toLocalDate()))
                .message("Department report generated")
                .build();
    }

    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }

    private String generateReceiptNumber() {
        return "RCP-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    private PaymentResponseDTO mapToResponseDTO(Payment payment, String message) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .studentId(payment.getStudent().getId())
                .studentName(payment.getStudent().getFullName())
                .studentIdNumber(payment.getStudent().getStudentId())
                .feeId(payment.getFee() != null ? payment.getFee().getId() : null)
                .feeDescription(payment.getFee() != null ? payment.getFee().getDescription() : null)
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .referenceNumber(payment.getReferenceNumber())
                .receiptNumber(payment.getReceiptNumber())
                .receivedBy(payment.getReceivedBy())
                .remarks(payment.getRemarks())
                .paymentDate(payment.getPaymentDate())
                .createdAt(payment.getCreatedAt())
                .message(message != null ? message : "Payment processed")
                .build();
    }
}
