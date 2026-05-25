package com.admas.management.modules.shared.controller;

import com.admas.management.modules.shared.dto.ForgotPasswordRequestDTO;
import com.admas.management.modules.shared.dto.ResetPasswordWithOTPRequestDTO;
import com.admas.management.modules.shared.dto.VerifyOTPRequestDTO;
import com.admas.management.modules.shared.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        passwordResetService.sendPasswordResetOTP(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "If your email is registered, you will receive an OTP for password reset");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Boolean>> verifyOTP(@Valid @RequestBody VerifyOTPRequestDTO request) {
        boolean isValid = passwordResetService.verifyOTP(request);
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", isValid);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password-with-otp")
    public ResponseEntity<Map<String, String>> resetPasswordWithOTP(@Valid @RequestBody ResetPasswordWithOTPRequestDTO request) {
        passwordResetService.resetPasswordWithOTP(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully. You can now login with your new password.");
        return ResponseEntity.ok(response);
    }
}
