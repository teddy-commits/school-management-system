package com.admas.management.modules.grading.dto.response;

import com.admas.management.modules.grading.model.enums.CourseStatus;
import com.admas.management.modules.grading.model.enums.Semester;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDTO {
    private Long id;
    private String courseCode;
    private String courseName;
    private String description;
    private Integer credits;
    private String department;
    private String faculty;
    private Semester semester;
    private Integer academicYear;
    private CourseStatus status;
    private String instructorName;
    private String instructorEmail;
    private Integer maxStudents;
    private Integer enrolledStudents;
    private String prerequisites;
    private String syllabus;
    private String room;
    private String schedule;
    private Boolean hasAvailableSeats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
