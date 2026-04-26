package com.admas.management.modules.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponseDTO {
    private Long id;
    private String invoiceNumber;
    private Long studentId;
    private String studentName;
    private String studentIdNumber;
    private List<FeeResponseDTO> fees;
    private Double totalAmount;
    private Double paidAmount;
    private Double dueAmount;
    private String semester;
    private Integer academicYear;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private String status;
    private LocalDateTime createdAt;
    private String message;
}
