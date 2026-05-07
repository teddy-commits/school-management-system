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
public class RegistrationSessionResponseDTO {
    private Long id;
    private String semester;
    private Integer academicYear;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private Boolean isCurrentlyOpen;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}