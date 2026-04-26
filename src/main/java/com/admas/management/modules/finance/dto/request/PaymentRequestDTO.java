package com.admas.management.modules.finance.dto.request;

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

    private Long feeId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @DecimalMin(value = "0.01", message = "Minimum payment amount is 0.01")
    private Double amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Size(max = 100, message = "Reference number max 100 characters")
    private String referenceNumber;

    private String bankName;
    private String chequeNumber;

    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @Size(max = 500, message = "Remarks max 500 characters")
    private String remarks;

    private LocalDateTime paymentDate;
}