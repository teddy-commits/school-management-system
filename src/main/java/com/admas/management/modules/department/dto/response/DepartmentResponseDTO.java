package com.admas.management.modules.department.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponseDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String faculty;
    private String headOfDepartment;
    private String headEmail;
    private String contactPhone;
    private String officeLocation;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
}