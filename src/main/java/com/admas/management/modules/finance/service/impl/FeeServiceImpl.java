package com.admas.management.modules.finance.service.impl;

import com.admas.management.modules.finance.dto.response.FeeResponseDTO;
import com.admas.management.modules.finance.dto.response.FeeSummaryDTO;
import com.admas.management.modules.finance.model.dto.request.FeeStructureRequestDTO;
import com.admas.management.modules.finance.model.entity.Fee;
import com.admas.management.modules.finance.model.entity.FeeStructure;
import com.admas.management.modules.finance.model.entity.Payment;
import com.admas.management.modules.finance.model.enums.PaymentStatus;
import com.admas.management.modules.finance.repository.FeeRepository;
import com.admas.management.modules.finance.repository.FeeStructureRepository;
import com.admas.management.modules.finance.repository.PaymentRepository;
import com.admas.management.modules.finance.service.FeeCalculationService;
import com.admas.management.modules.finance.service.FeeService;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final FeeCalculationService feeCalculationService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public FeeResponseDTO createFeeStructure(FeeStructureRequestDTO request) {

        FeeStructure feeStructure = new FeeStructure();
        feeStructure.setFeeType(request.getFeeType());
        feeStructure.setCategory(request.getCategory());
        feeStructure.setDescription(request.getDescription());
        feeStructure.setAmount(request.getAmount());
        feeStructure.setDepartment(request.getDepartment() != null ? request.getDepartment() : "ALL");
        feeStructure.setFaculty(request.getFaculty() != null ? request.getFaculty() : "ALL");
        feeStructure.setIsMandatory(request.getIsMandatory() != null ? request.getIsMandatory() : true);
        feeStructure.setAcademicYear(request.getAcademicYear());
        feeStructure.setSemester(request.getSemester() != null ? request.getSemester() : "ALL");
        feeStructure.setDueDate(request.getDueDate());
        feeStructure.setGracePeriodDays(request.getGracePeriodDays() != null ? request.getGracePeriodDays() : 15);
        feeStructure.setLateFeePercentage(request.getLateFeePercentage() != null ? request.getLateFeePercentage() : 5.0);
        feeStructure.setIsActive(true);

        FeeStructure saved = feeStructureRepository.save(feeStructure);
        return mapToResponseDTO(saved);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public FeeResponseDTO generateFeeForStudent(Long studentId, Long feeStructureId, String semester, Integer academicYear) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        FeeStructure feeStructure = feeStructureRepository.findById(feeStructureId)
                .orElseThrow(() -> new RuntimeException("Fee structure not found"));

        boolean exists = feeRepository.existsByStudentAndFeeTypeAndSemester(
                student,
                feeStructure.getFeeType(),
                semester
        );

        if (exists) {
            throw new RuntimeException("Fee already generated for this student and semester");
        }

        Fee fee = new Fee();
        fee.setStudent(student);
        fee.setFeeStructure(feeStructure);
        fee.setFeeType(feeStructure.getFeeType());
        fee.setAmount(feeStructure.getAmount());
        fee.setDueAmount(feeStructure.getAmount());
        fee.setDescription(feeStructure.getDescription());
        fee.setSemester(semester);
        fee.setAcademicYear(academicYear);
        fee.setDueDate(feeStructure.getDueDate());
        fee.setStatus(PaymentStatus.PENDING);
        fee.setInvoiceNumber(generateInvoiceNumber());

        Fee savedFee = feeRepository.save(fee);
        return mapToResponseDTO(savedFee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeResponseDTO> getStudentFees(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return feeRepository.findByStudent(student)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FeeSummaryDTO getStudentFeeSummary(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Fee> fees = feeRepository.findByStudent(student);
        List<Payment> payments = paymentRepository.findByStudent(student);

        double totalFees = fees.stream().mapToDouble(Fee::getAmount).sum();
        double totalPaid = feeCalculationService.calculateTotalPaid(payments);
        double totalOutstanding = feeCalculationService.calculateOutstandingBalance(totalFees, totalPaid);
        double totalLateFees = fees.stream().mapToDouble(f -> feeCalculationService.calculateLateFee(f, LocalDateTime.now())).sum();

        long pendingFeesCount = fees.stream()
                .filter(f -> f.getStatus() == PaymentStatus.PENDING || f.getStatus() == PaymentStatus.PARTIAL)
                .count();

        long overdueFeesCount = fees.stream()
                .filter(f -> f.getDueDate() != null && f.getDueDate().isBefore(LocalDateTime.now())
                        && f.getStatus() != PaymentStatus.PAID)
                .count();

        return FeeSummaryDTO.builder()
                .studentId(studentId)
                .studentName(student.getFullName())
                .studentIdNumber(student.getStudentId())
                .totalFees(totalFees)
                .totalPaid(totalPaid)
                .totalOutstanding(totalOutstanding)
                .totalLateFees(totalLateFees)
                .pendingFeesCount((int) pendingFeesCount)
                .overdueFeesCount((int) overdueFeesCount)
                .recentFees(fees.stream().limit(5).map(this::mapToResponseDTO).collect(Collectors.toList()))
                .message("Fee summary calculated successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeResponseDTO> getOverdueFees() {
        return feeRepository.findByDueDateBeforeAndStatus(LocalDateTime.now(), PaymentStatus.PENDING)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public FeeResponseDTO applyLateFee(Long feeId) {
        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new RuntimeException("Fee not found"));

        double lateFee = feeCalculationService.calculateLateFee(fee, LocalDateTime.now());

        if (lateFee > 0) {
            fee.setLateFee(lateFee);
            fee.setAmount(fee.getAmount() + lateFee);
            fee.setDueAmount(fee.getDueAmount() + lateFee);
            fee.setIsLate(true);
            Fee updatedFee = feeRepository.save(fee);
            return mapToResponseDTO(updatedFee);
        }

        return mapToResponseDTO(fee);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER')")
    public FeeResponseDTO waiveFee(Long feeId, Double waiveAmount, String reason) {
        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new RuntimeException("Fee not found"));

        if (waiveAmount > fee.getDueAmount()) {
            throw new RuntimeException("Waiver amount cannot exceed due amount");
        }

        fee.setAmount(fee.getAmount() - waiveAmount);
        fee.setDueAmount(fee.getDueAmount() - waiveAmount);
        fee.setRemarks("WAIVED: " + reason + " | Amount: " + waiveAmount);

        if (fee.getDueAmount() <= 0) {
            fee.setStatus(PaymentStatus.PAID);
        }

        Fee updatedFee = feeRepository.save(fee);
        return mapToResponseDTO(updatedFee);
    }

    @Override
    public List<FeeResponseDTO> getAllFeeStructures() {
        List<FeeStructure> feeStructures = feeStructureRepository.findAll();
        if (feeStructures == null || feeStructures.isEmpty()) {
            return new ArrayList<>();
        }

        return feeStructures.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    @Override public FeeResponseDTO updateFeeStructure(Long id, FeeStructureRequestDTO request) { return null; }
    @Override public void deleteFeeStructure(Long id) {}
    @Override public List<FeeResponseDTO> generateAllFeesForSemester(String semester, Integer academicYear) { return null; }
    @Override public FeeResponseDTO getFeeById(Long feeId) { return null; }
    @Override public List<FeeResponseDTO> getPendingFeesByStudent(Long studentId) { return null; }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    private FeeResponseDTO mapToResponseDTO(FeeStructure feeStructure) {
        return FeeResponseDTO.builder()
                .id(feeStructure.getId())
                .feeType(feeStructure.getFeeType())
                .description(feeStructure.getDescription())
                .amount(feeStructure.getAmount())
                .isMandatory(feeStructure.getIsMandatory())
                .build();
    }

    private FeeResponseDTO mapToResponseDTO(Fee fee) {
        return FeeResponseDTO.builder()
                .id(fee.getId())
                .studentId(fee.getStudent().getId())
                .studentName(fee.getStudent().getFullName())
                .studentIdNumber(fee.getStudent().getStudentId())
                .feeType(fee.getFeeType())
                .description(fee.getDescription())
                .amount(fee.getAmount())
                .paidAmount(fee.getPaidAmount())
                .dueAmount(fee.getDueAmount())
                .lateFee(fee.getLateFee())
                .status(fee.getStatus())
                .dueDate(fee.getDueDate())
                .invoiceNumber(fee.getInvoiceNumber())
                .semester(fee.getSemester())
                .academicYear(fee.getAcademicYear())
                .isLate(fee.getIsLate())
                .isMandatory(fee.getFeeStructure() != null ? fee.getFeeStructure().getIsMandatory() : true)
                .createdAt(fee.getCreatedAt())
                .message("Fee processed successfully")
                .build();
    }
}