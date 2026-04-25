package com.admas.management.modules.grading.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeSubmissionDTO {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be between 0 and 100")
    @Max(value = 100, message = "Score must be between 0 and 100")
    private Double score;

    private String semester;
    private Integer academicYear;
    private String remarks;
}
