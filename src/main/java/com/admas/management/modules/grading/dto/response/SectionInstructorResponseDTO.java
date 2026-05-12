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
public class SectionInstructorResponseDTO {
    private Long id;
    private Long instructorId;
    private String instructorName;
    private String instructorEmail;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private LocalDateTime createdAt;
    private String message;
}
