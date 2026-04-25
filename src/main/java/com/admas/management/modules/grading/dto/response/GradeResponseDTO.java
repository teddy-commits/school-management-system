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
public class GradeResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentIdNumber;
    private String courseCode;
    private String courseName;
    private Double score;
    private String gradeLetter;
    private Double gradePoint;
    private String semester;
    private Integer academicYear;
    private String remarks;
    private String gradedBy;
    private LocalDateTime gradedDate;
}
