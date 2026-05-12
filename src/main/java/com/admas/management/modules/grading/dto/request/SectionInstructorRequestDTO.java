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
public class SectionInstructorRequestDTO {

    @NotNull(message = "Instructor ID is required")
    private Long instructorId;

    private Long courseId;  // Optional
}