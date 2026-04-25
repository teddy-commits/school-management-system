package com.admas.management.modules.grading.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptResponseDTO {
    private String studentId;
    private String studentName;
    private String department;
    private String faculty;
    private Double overallCGPA;
    private Integer totalCreditsEarned;
    private List<SemesterGradeDTO> semesterGrades;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SemesterGradeDTO {
        private String semester;
        private Integer academicYear;
        private Double semesterGPA;
        private List<CourseGradeDTO> courses;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseGradeDTO {
        private String courseCode;
        private String courseName;
        private Integer credits;
        private Double score;
        private String gradeLetter;
        private Double gradePoint;
    }
}