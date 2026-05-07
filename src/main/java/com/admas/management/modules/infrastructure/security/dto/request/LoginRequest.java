package com.admas.management.modules.infrastructure.security.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "ID is required (Student ID or Employee ID)")
    private String id;

    @NotBlank(message = "Password is required")
    private String password;
}