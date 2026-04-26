package com.admas.management.modules.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeSummaryDTO {
    private Long studentId;
    private String studentName;
    private String studentIdNumber;
    private Double totalFees;
    private Double totalPaid;
    private Double totalOutstanding;
    private Double totalLateFees;
    private Integer pendingFeesCount;
    private Integer overdueFeesCount;
    private List<FeeResponseDTO> recentFees;
    private String message;
}
