package com.admas.management.modules.registration.dto.response;

import com.admas.management.modules.registration.model.StudentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegistrationResponse {
    private Long id;
    private String studentId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String department;
    private String faculty;
    private Integer enrollmentYear;
    private StudentType studentType;
    private String registrationStatus;
    private LocalDateTime registrationDate;
    private String message;
}