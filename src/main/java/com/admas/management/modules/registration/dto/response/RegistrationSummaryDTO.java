package com.admas.management.modules.registration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationSummaryDTO {
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String department;
    private Integer academicYearLevel;
    private String semester;
    private Integer academicYear;
    private Double totalCredits;
    private Double totalFees;
    private Double feesPaid;
    private Double feesDue;
    private Integer totalCourses;
    private String registrationStatus;
    private LocalDateTime registrationDate;
}
