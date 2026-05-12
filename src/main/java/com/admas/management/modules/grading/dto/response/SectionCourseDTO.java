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
public class SectionCourseDTO {
    private Long id;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Integer credits;
    private String schedule;
    private String room;
    private LocalDateTime addedAt;
}
