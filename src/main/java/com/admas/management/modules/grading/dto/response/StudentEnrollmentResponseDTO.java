package com.admas.management.modules.grading.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentEnrollmentResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentIdNumber;
    private Long sectionId;
    private String courseCode;
    private String courseName;
    private String sectionCode;
    private String instructorName;
    private String schedule;
    private String room;
    private LocalDateTime enrollmentDate;
    private String status;
    private String semester;
    private Integer academicYear;
    private String message;
}