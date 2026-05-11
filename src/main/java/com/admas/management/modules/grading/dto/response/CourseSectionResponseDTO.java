package com.admas.management.modules.grading.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionResponseDTO {
    private Long id;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String sectionCode;
    private Integer academicYearLevel;
    private String semester;
    private Integer academicYear;
    private Long instructorId;
    private String instructorName;
    private String instructorEmail;
    private Integer maxStudents;
    private Integer enrolledStudents;
    private String schedule;
    private String room;
    private String status;
    private Boolean hasAvailableSeats;
    private String formattedSectionName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
}