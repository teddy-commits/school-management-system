package com.admas.management.modules.finance.service.impl;

import com.admas.management.modules.finance.dto.response.FeeResponseDTO;
import com.admas.management.modules.finance.dto.response.InvoiceResponseDTO;
import com.admas.management.modules.finance.model.entity.Fee;
import com.admas.management.modules.finance.model.entity.Invoice;
import com.admas.management.modules.finance.repository.FeeRepository;
import com.admas.management.modules.finance.repository.InvoiceRepository;
import com.admas.management.modules.finance.service.InvoiceService;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final FeeRepository feeRepository;
    private final UserRepository userRepository;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public InvoiceResponseDTO generateInvoice(Long studentId, String semester, Integer academicYear) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        List<Fee> studentFees = feeRepository.findByStudentAndSemesterAndAcademicYear(student, semester, academicYear);

        if (studentFees.isEmpty()) {
            throw new RuntimeException("No fees found for student in this semester");
        }
        if (invoiceRepository.existsByStudentAndSemesterAndAcademicYear(student, semester, academicYear)) {
            throw new RuntimeException("Invoice already exists for this student and semester");
        }
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setStudent(student);
        invoice.setFees(new ArrayList<>(studentFees));
        invoice.setSemester(semester);
        invoice.setAcademicYear(academicYear);
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setDueDate(calculateDueDate(semester, academicYear));
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.calculateTotals();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        return mapToResponseDTO(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getStudentInvoices(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return invoiceRepository.findByStudent(student)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));
        return mapToResponseDTO(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found with number: " + invoiceNumber));
        return mapToResponseDTO(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public List<InvoiceResponseDTO> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public List<InvoiceResponseDTO> getOverdueInvoices() {
        return invoiceRepository.findByDueDateBeforeAndStatusNot(LocalDateTime.now(), "PAID")
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER')")
    public InvoiceResponseDTO updateInvoiceStatus(Long invoiceId, String status) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setStatus(status);
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        return mapToResponseDTO(updatedInvoice);
    }

    @Override
    public void sendInvoiceEmail(Long invoiceId) {
        
    }

    @Override
    public byte[] generateInvoicePDF(Long invoiceId) {
        log.info("Generating PDF for invoice: {}", invoiceId);
        return new byte[0];
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteInvoice(Long invoiceId) {
        log.info("Deleting invoice: {}", invoiceId);
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        invoiceRepository.delete(invoice);
    }

    private String generateInvoiceNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String sequence = String.format("%06d", (int)(Math.random() * 1000000));
        return "INV-" + year + "-" + sequence;
    }

    private LocalDateTime calculateDueDate(String semester, Integer academicYear) {
        return LocalDateTime.now().plusDays(30);
    }

    private InvoiceResponseDTO mapToResponseDTO(Invoice invoice) {
        List<FeeResponseDTO> feeDTOs = invoice.getFees() != null ?
                invoice.getFees().stream()
                .map(this::mapFeeToDTO)
                .collect(Collectors.toList()) :
                new ArrayList<>();

        return InvoiceResponseDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .studentId(invoice.getStudent().getId())
                .studentName(invoice.getStudent().getFullName())
                .studentIdNumber(invoice.getStudent().getStudentId())
                .fees(feeDTOs)
                .totalAmount(invoice.getTotalAmount())
                .paidAmount(invoice.getPaidAmount())
                .dueAmount(invoice.getDueAmount())
                .semester(invoice.getSemester())
                .academicYear(invoice.getAcademicYear())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .status(invoice.getStatus())
                .createdAt(invoice.getCreatedAt())
                .message("Invoice retrieved successfully")
                .build();
    }

    private FeeResponseDTO mapFeeToDTO(Fee fee) {
        return FeeResponseDTO.builder()
                .id(fee.getId())
                .feeType(fee.getFeeType())
                .description(fee.getDescription())
                .amount(fee.getAmount())
                .paidAmount(fee.getPaidAmount())
                .dueAmount(fee.getDueAmount())
                .status(fee.getStatus())
                .dueDate(fee.getDueDate())
                .build();
    }
}
