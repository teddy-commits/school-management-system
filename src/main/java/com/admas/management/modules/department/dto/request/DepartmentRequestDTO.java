package com.admas.management.modules.department.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequestDTO {

    @NotBlank(message = "Department code is required")
    @Pattern(regexp = "^[A-Z]{2,5}$", message = "Department code must be 2-5 uppercase letters (e.g., CS, ENG, BUS)")
    private String code;

    @NotBlank(message = "Department name is required")
    @Size(min = 3, max = 100, message = "Department name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private String faculty;

    private String headOfDepartment;

    private String headEmail;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String contactPhone;

    private String officeLocation;

    private Boolean isActive;
}