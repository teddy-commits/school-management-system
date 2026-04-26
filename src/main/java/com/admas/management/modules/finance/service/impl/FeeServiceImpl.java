package com.admas.management.modules.finance.service.impl;

import com.admas.management.modules.finance.model.dto.request.FeeStructureRequestDTO;
import com.admas.management.modules.finance.model.dto.response.FeeResponseDTO;
import com.admas.management.modules.finance.model.dto.response.FeeSummaryDTO;
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
        return mapToResponseDTO(saved);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT', 'FINANCE_MANAGER')")
    public FeeResponseDTO generateFeeForStudent(Long studentId, Long feeStructureId, String semester, Integer academicYear) {
        log.info("Generating fee for student: {}, structure: {}", studentId, feeStructureId);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        FeeStructure feeStructure = feeStructureRepository.findById(feeStructureId)
                .orElseThrow(() -> new RuntimeException("Fee structure not found"));

        // Check if fee already exists for this student
        boolean exists = feeRepository.existsByStudentAndFeeTypeAndSemester(
                student, feeStructure.getFeeType().name(), semester);

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
        Double totalFees = feeRepository.getTotalFees(studentId);
        Double totalPaid = feeRepository.getTotalOutstandingFees(studentId);
        Double outstanding = feeRepository.getTotalOutstandingFees(studentId);

        return FeeSummaryDTO.builder()
                .studentId(studentId)
                .totalFees(totalFees != null ? totalFees : 0.0)
                .totalPaid(totalPaid != null ? totalFees - outstanding : 0.0)
                .totalOutstanding(outstanding != null ? outstanding : 0.0)
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

        fee.applyLateFee();
        Fee updatedFee = feeRepository.save(fee);
        return mapToResponseDTO(updatedFee);
    }

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
                .feeType(fee.getFeeType())
                .description(fee.getDescription())
                .amount(fee.getAmount())
                .paidAmount(fee.getPaidAmount())
                .dueAmount(fee.getDueAmount())
                .status(fee.getStatus())
                .dueDate(fee.getDueDate())
                .invoiceNumber(fee.getInvoiceNumber())
                .semester(fee.getSemester())
                .academicYear(fee.getAcademicYear())
                .build();
    }

    // Other methods implementation...
    @Override public List<FeeResponseDTO> getAllFeeStructures() { return null; }
    @Override public FeeResponseDTO updateFeeStructure(Long id, FeeStructureRequestDTO request) { return null; }
    @Override public void deleteFeeStructure(Long id) {}
    @Override public List<FeeResponseDTO> generateAllFeesForSemester(String semester, Integer academicYear) { return null; }
    @Override public FeeResponseDTO getFeeById(Long feeId) { return null; }
    @Override public List<FeeResponseDTO> getPendingFeesByStudent(Long studentId) { return null; }
    @Override public FeeResponseDTO waiveFee(Long feeId, Double waiveAmount, String reason) { return null; }
}