package com.admas.management.modules.finance.service;


import com.admas.management.modules.finance.dto.request.FeeStructureRequestDTO;
import com.admas.management.modules.finance.dto.response.FeeResponseDTO;
import com.admas.management.modules.finance.dto.response.FeeSummaryDTO;

import java.util.List;

public interface FeeService {

    FeeResponseDTO createFeeStructure(FeeStructureRequestDTO request);
    List<FeeResponseDTO> getAllFeeStructures();
    FeeResponseDTO updateFeeStructure(Long id, FeeStructureRequestDTO request);
    void deleteFeeStructure(Long id);

    FeeResponseDTO generateFeeForStudent(Long studentId, Long feeStructureId, String semester, Integer academicYear);
    List<FeeResponseDTO> getStudentFees(Long studentId);
    FeeResponseDTO getFeeById(Long feeId);
    List<FeeResponseDTO> getPendingFeesByStudent(Long studentId);

    FeeSummaryDTO getStudentFeeSummary(Long studentId);
    List<FeeResponseDTO> getOverdueFees();

    List<FeeResponseDTO> generateAllFeesForSemester(String semester, Integer academicYear);

    FeeResponseDTO applyLateFee(Long feeId);
    FeeResponseDTO waiveFee(Long feeId, Double waiveAmount, String reason);
}