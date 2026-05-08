package com.admas.management.modules.registration.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterRegistrationRequestDTO {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Semester is required")
    private String semester;

    @NotNull(message = "Academic year is required")
    private Integer academicYear;

    @NotNull(message = "Course IDs are required")
    private List<Long> courseIds;
}
