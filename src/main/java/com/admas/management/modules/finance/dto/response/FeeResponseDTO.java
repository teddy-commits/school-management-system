package com.admas.management.modules.finance.dto.response;

import com.admas.management.modules.finance.model.enums.FeeType;
import com.admas.management.modules.finance.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentIdNumber;
    private FeeType feeType;
    private String description;
    private Double amount;
    private Double paidAmount;
    private Double dueAmount;
    private Double lateFee;
    private PaymentStatus status;
    private LocalDateTime dueDate;
    private String invoiceNumber;
    private String semester;
    private Integer academicYear;
    private Boolean isLate;
    private Boolean isMandatory;  // Add this field
    private LocalDateTime createdAt;
    private String message;
}