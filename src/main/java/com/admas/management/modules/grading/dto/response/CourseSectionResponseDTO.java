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
public class CourseSectionResponseDTO {
    private Long id;

    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private String faculty;
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
    private Boolean hasAvailableSeats;
    private String schedule;
    private String room;
    private String status;
    private String formattedSectionName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
}