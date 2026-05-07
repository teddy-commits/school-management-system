package com.admas.management.modules.finance.service;

import com.admas.management.modules.finance.dto.response.FeeResponseDTO;
import com.admas.management.modules.finance.dto.response.FeeSummaryDTO;
import com.admas.management.modules.finance.model.dto.request.FeeStructureRequestDTO;


import java.util.List;

public interface FeeService {
    FeeResponseDTO createFeeStructure(FeeStructureRequestDTO request);
    List<FeeResponseDTO> getAllFeeStructures();
    FeeResponseDTO updateFeeStructure(Long id, FeeStructureRequestDTO request);
    void deleteFeeStructure(Long id);

    FeeResponseDTO generateFeeForStudent(Long studentId, Long feeStructureId, String semester, Integer academicYear);
    List<FeeResponseDTO> generateAllFeesForSemester(String semester, Integer academicYear);
    List<FeeResponseDTO> getStudentFees(Long studentId);
    FeeResponseDTO getFeeById(Long feeId);

    FeeSummaryDTO getStudentFeeSummary(Long studentId);
    List<FeeResponseDTO> getOverdueFees();
    List<FeeResponseDTO> getPendingFeesByStudent(Long studentId);

    FeeResponseDTO applyLateFee(Long feeId);
    FeeResponseDTO waiveFee(Long feeId, Double waiveAmount, String reason);
}