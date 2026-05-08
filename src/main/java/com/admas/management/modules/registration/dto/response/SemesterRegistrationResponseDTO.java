package com.admas.management.modules.registration.dto.response;

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
public class SemesterRegistrationResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentIdNumber;
    private String semester;
    private Integer academicYear;
    private LocalDateTime registrationDate;
    private String status;
    private Double totalCredits;
    private Double totalFees;
    private Double feesPaid;
    private Double feesDue;
    private String paymentReference;
    private List<CourseEnrollmentDTO> courses;
    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseEnrollmentDTO {
        private Long courseId;
        private String courseCode;
        private String courseName;
        private Double credits;
        private Double fee;
        private String status;
    }
}
