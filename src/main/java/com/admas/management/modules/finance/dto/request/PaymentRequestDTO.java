package com.admas.management.modules.finance.model.dto.request;

import com.admas.management.modules.finance.model.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private Long feeId; // Optional - if paying specific fee

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String referenceNumber;
    private String bankName;
    private String chequeNumber;
    private String mobileNumber;
    private String remarks;
    private LocalDateTime paymentDate;
}