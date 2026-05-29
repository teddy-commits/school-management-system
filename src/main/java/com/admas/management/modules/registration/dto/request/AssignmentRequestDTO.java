package com.admas.management.modules.registration.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequestDTO {

    private Long departmentId;

    private Integer academicYearLevel;

    private String semester;

    private Integer academicYear;

    private List<Long> courseIds;
}
