package com.admas.management.modules.grading.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionCourseDetailDTO {
    private Long id;
    private String courseCode;
    private String courseName;
    private Long sectionId;
    private Integer credits;
    private String status;
    private String schedule;
    private String room;
    private String sectionCode;
    private String semester;
    private Integer academicYear;
    private Integer enrolledStudents;
    private Integer maxStudents;
    private String department;
}