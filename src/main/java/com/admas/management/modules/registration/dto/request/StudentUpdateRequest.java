package com.admas.management.modules.registration.dto.request;

import com.admas.management.modules.registration.model.StudentType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateRequest {

    private String firstName;
    private String lastName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    private String address;
    private String department;
    private String faculty;

    @Min(value = 2000, message = "Enrollment year must be 2000 or later")
    @Max(value = 2030, message = "Enrollment year must be 2030 or earlier")
    private Integer enrollmentYear;

    private StudentType studentType;

    private String emergencyContact;
}