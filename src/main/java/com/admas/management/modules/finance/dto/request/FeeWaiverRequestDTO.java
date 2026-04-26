package com.admas.management.modules.finance.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeWaiverRequestDTO {

    @NotNull(message = "Fee ID is required")
    private Long feeId;

    @NotNull(message = "Waiver amount is required")
    @Positive(message = "Waiver amount must be positive")
    @Max(value = 100000, message = "Waiver amount cannot exceed 100,000")
    private Double waiverAmount;

    @NotBlank(message = "Reason is required")
    @Size(min = 5, max = 500, message = "Reason must be between 5 and 500 characters")
    private String reason;

    private String approvedBy;
    private String remarks;
}