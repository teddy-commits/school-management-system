package com.admas.management.modules.finance.dto.response;

import com.admas.management.modules.finance.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReportDTO {
    private LocalDateTime reportDate;
    private String reportPeriod;

    // Summary
    private Double totalFeesGenerated;
    private Double totalPaymentsReceived;
    private Double totalOutstanding;
    private Double totalOverdue;

    // Breakdowns
    private Map<String, Double> feesByType;
    private Map<PaymentMethod, Double> paymentsByMethod;
    private Map<String, Double> collectionsByDepartment;

    // Counts
    private Integer totalStudentsWithOutstanding;
    private Integer totalOverdueAccounts;
    private Integer totalTransactions;

    private String message;
}