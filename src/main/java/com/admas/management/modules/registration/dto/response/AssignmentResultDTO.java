// Package: com.admas.management.modules.registration.dto.response
package com.admas.management.modules.registration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResultDTO {

    private Integer totalStudents;

    private Integer totalAssignments;

    private List<String> errors;
}