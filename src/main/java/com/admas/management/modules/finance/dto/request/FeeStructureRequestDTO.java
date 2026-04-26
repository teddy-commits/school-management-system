package com.admas.management.modules.finance.model.dto.request;

import com.admas.management.modules.finance.model.enums.FeeCategory;
import com.admas.management.modules.finance.model.enums.FeeType;
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
public class FeeStructureRequestDTO {

    @NotNull(message = "Fee type is required")
    private FeeType feeType;

    private FeeCategory category;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    private String department;
    private String faculty;
    private String program;
    private Boolean isMandatory;
    private Integer academicYear;
    private String semester;
    private LocalDateTime dueDate;
    private Integer gracePeriodDays;
    private Double lateFeePercentage;
}
