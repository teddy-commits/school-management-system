package com.admas.management.modules.grading.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionRequestDTO {

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotBlank(message = "Section code is required")
    @Size(min = 1, max = 10, message = "Section code must be between 1 and 10 characters")
    private String sectionCode;

    @NotNull(message = "Academic year level is required")
    @Min(value = 1, message = "Academic year level must be between 1 and 5")
    @Max(value = 5, message = "Academic year level must be between 1 and 5")
    private Integer academicYearLevel;

    @NotBlank(message = "Semester is required")
    private String semester;

    @NotNull(message = "Academic year is required")
    private Integer academicYear;

    @Min(value = 5, message = "Minimum 5 students per section")
    @Max(value = 100, message = "Maximum 100 students per section")
    private Integer maxStudents = 40;

    private String status;
}