// Package: com.admas.management.modules.registration.dto.response
package com.admas.management.modules.registration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentPreviewDTO {

    private Long id;

    private String studentId;

    private String fullName;

    private String email;

    private String department;

    private Integer academicYearLevel;
}