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
public class RegisteredCourseDTO {
    private Long registrationId;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Integer credits;
    private String schedule;
    private String room;
    private String instructorName;
    private String status;
    private LocalDateTime enrollmentDate;
    private Double fee;
}

