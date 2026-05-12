package com.admas.management.modules.grading.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionInstructorRequestDTO {
    @NotNull(message = "Section ID is required")
    private Long sectionId;

    @NotNull(message = "Instructor ID is required")
    private Long instructorId;

    private Long courseId;  // Optional: specific course this instructor teaches
}
