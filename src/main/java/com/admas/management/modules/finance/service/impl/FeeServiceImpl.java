package com.admas.management.modules.finance.service.impl;

import com.admas.management.modules.finance.dto.response.FeeResponseDTO;
import com.admas.management.modules.finance.dto.response.FeeSummaryDTO;
import com.admas.management.modules.finance.dto.request.FeeStructureRequestDTO;
import com.admas.management.modules.finance.model.entity.Fee;
import com.admas.management.modules.finance.model.entity.FeeStructure;
import com.admas.management.modules.finance.model.enums.PaymentStatus;
import com.admas.management.modules.finance.repository.FeeRepository;
import com.admas.management.modules.finance.repository.FeeStructureRepository;
import com.admas.management.modules.finance.service.FeeService;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final UserRepository userRepository;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public FeeResponseDTO createFeeStructure(FeeStructureRequestDTO request) {
        log.info("Creating fee structure: {}", request.getDescription());

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
        return mapFeeStructureToResponseDTO(saved);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public FeeResponseDTO generateFeeForStudent(Long studentId, Long feeStructureId, String semester, Integer academicYear) {
        log.info("Generating fee for student: {}, structure: {}", studentId, feeStructureId);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        FeeStructure feeStructure = feeStructureRepository.findById(feeStructureId)
                .orElseThrow(() -> new RuntimeException("Fee structure not found"));

        // Check if fee already exists
        boolean exists = feeRepository.existsByStudentAndFeeTypeAndSemester(
                student, feeStructure.getFeeType(), semester);

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
        return mapFeeToResponseDTO(savedFee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeResponseDTO> getStudentFees(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return feeRepository.findByStudent(student)
                .stream()
                .map(this::mapFeeToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FeeSummaryDTO getStudentFeeSummary(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Fee> fees = feeRepository.findByStudent(student);

        double totalFees = fees.stream().mapToDouble(Fee::getAmount).sum();
        double totalPaid = fees.stream().mapToDouble(Fee::getPaidAmount).sum();
        double totalOutstanding = fees.stream().mapToDouble(Fee::getDueAmount).sum();

        long pendingCount = fees.stream()
                .filter(f -> f.getStatus() == PaymentStatus.PENDING)
                .count();

        return FeeSummaryDTO.builder()
                .studentId(studentId)
                .studentName(student.getFullName())
                .studentIdNumber(student.getStudentId())
                .totalFees(totalFees)
                .totalPaid(totalPaid)
                .totalOutstanding(totalOutstanding)
                .pendingFeesCount((int) pendingCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeResponseDTO> getOverdueFees() {
        return feeRepository.findByDueDateBeforeAndStatus(LocalDateTime.now(), PaymentStatus.PENDING)
                .stream()
                .map(this::mapFeeToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public FeeResponseDTO applyLateFee(Long feeId) {
        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new RuntimeException("Fee not found"));

        fee.applyLateFee();
        Fee updatedFee = feeRepository.save(fee);
        return mapFeeToResponseDTO(updatedFee);
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

        if (fee.getDueAmount() <= 0) {
            fee.setStatus(PaymentStatus.PAID);
        }

        Fee updatedFee = feeRepository.save(fee);
        return mapFeeToResponseDTO(updatedFee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeResponseDTO> getAllFeeStructures() {
        return feeStructureRepository.findAll()
                .stream()
                .map(this::mapFeeStructureToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public FeeResponseDTO updateFeeStructure(Long id, FeeStructureRequestDTO request) {
        FeeStructure feeStructure = feeStructureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee structure not found"));

        feeStructure.setDescription(request.getDescription());
        feeStructure.setAmount(request.getAmount());
        feeStructure.setDepartment(request.getDepartment());
        feeStructure.setFaculty(request.getFaculty());
        feeStructure.setIsMandatory(request.getIsMandatory());
        feeStructure.setAcademicYear(request.getAcademicYear());
        feeStructure.setSemester(request.getSemester());
        feeStructure.setDueDate(request.getDueDate());
        feeStructure.setGracePeriodDays(request.getGracePeriodDays());
        feeStructure.setLateFeePercentage(request.getLateFeePercentage());

        FeeStructure updated = feeStructureRepository.save(feeStructure);
        return mapFeeStructureToResponseDTO(updated);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public void deleteFeeStructure(Long id) {
        feeStructureRepository.deleteById(id);
        log.info("Deleted fee structure with id: {}", id);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public List<FeeResponseDTO> generateAllFeesForSemester(String semester, Integer academicYear) {
        // This would generate fees for all students
        log.info("Generating fees for all students for semester: {} {}", semester, academicYear);
        return List.of(); // Placeholder
    }

    @Override
    @Transactional(readOnly = true)
    public FeeResponseDTO getFeeById(Long feeId) {
        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new RuntimeException("Fee not found"));
        return mapFeeToResponseDTO(fee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeResponseDTO> getPendingFeesByStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return feeRepository.findByStudentAndStatus(student, PaymentStatus.PENDING)
                .stream()
                .map(this::mapFeeToResponseDTO)
                .collect(Collectors.toList());
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    private FeeResponseDTO mapFeeStructureToResponseDTO(FeeStructure feeStructure) {
        return FeeResponseDTO.builder()
                .id(feeStructure.getId())
                .feeType(feeStructure.getFeeType())
                .description(feeStructure.getDescription())
                .amount(feeStructure.getAmount())
                .isMandatory(feeStructure.getIsMandatory())
                .dueDate(feeStructure.getDueDate())
                .build();
    }

    private FeeResponseDTO mapFeeToResponseDTO(Fee fee) {
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