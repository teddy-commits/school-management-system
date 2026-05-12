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

    // Department fields (NEW)
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private String faculty;

    // Course fields (optional - can be null)
    private Long courseId;
    private String courseCode;
    private String courseName;

    // Section fields
    private String sectionCode;
    private Integer academicYearLevel;
    private String semester;
    private Integer academicYear;

    // Instructor fields (optional)
    private Long instructorId;
    private String instructorName;
    private String instructorEmail;

    // Capacity fields
    private Integer maxStudents;
    private Integer enrolledStudents;
    private Boolean hasAvailableSeats;

    // Schedule fields (will be added per course later)
    private String schedule;
    private String room;

    // Status
    private String status;

    // Helper
    private String formattedSectionName;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
}