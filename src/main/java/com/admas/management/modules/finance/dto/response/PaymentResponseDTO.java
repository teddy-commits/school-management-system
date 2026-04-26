package com.admas.management.modules.finance.dto.response;

import com.admas.management.modules.finance.model.enums.PaymentMethod;
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
public class PaymentResponseDTO {
    private Long id;
    private String transactionId;
    private Long studentId;
    private String studentName;
    private String studentIdNumber;
    private Long feeId;
    private String feeDescription;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String referenceNumber;
    private String receiptNumber;
    private String receivedBy;
    private String remarks;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private String message;
}
