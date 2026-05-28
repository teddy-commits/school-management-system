package com.admas.management.modules.grading.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAvailableCourseDTO {
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private Integer credits;
    private String department;
    private String faculty;
    private String semester;
    private Integer academicYear;
    private Integer academicYearLevel;
    private String instructorName;
    private String instructorEmail;
    private Integer maxStudents;
    private Integer enrolledStudents;
    private Integer availableSeats;
    private String prerequisites;
    private String schedule;
    private String room;
    private boolean isEligible;
    private String eligibilityMessage;
}

