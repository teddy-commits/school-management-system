package com.admas.management.modules.registration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponse {
    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String department;
    private String faculty;
    private Integer enrollmentYear;
    private Double cgpa;
    private String currentSemester;
    private Integer totalCredits;
    private Boolean isActive;
    private LocalDateTime createdAt;
}