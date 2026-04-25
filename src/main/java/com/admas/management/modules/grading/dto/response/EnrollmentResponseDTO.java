package com.admas.management.modules.grading.dto.response;

import com.admas.management.modules.grading.model.entity.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentIdNumber;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Integer credits;
    private LocalDateTime enrollmentDate;
    private Enrollment.EnrollmentStatus status;
    private String semester;
    private Integer academicYear;
    private LocalDateTime createdAt;
    private String message;
}
