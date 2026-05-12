package com.admas.management.modules.grading.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionCourseRequestDTO {

    // Remove sectionId - it comes from URL path
    // @NotNull(message = "Section ID is required")
    // private Long sectionId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    private String schedule;
    private String room;
}