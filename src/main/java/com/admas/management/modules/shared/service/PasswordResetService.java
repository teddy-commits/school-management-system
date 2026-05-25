package com.admas.management.modules.shared.service;

import com.admas.management.modules.shared.dto.ForgotPasswordRequestDTO;
import com.admas.management.modules.shared.dto.ResetPasswordWithOTPRequestDTO;
import com.admas.management.modules.shared.dto.VerifyOTPRequestDTO;
import com.admas.management.modules.shared.model.PasswordResetOTP;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.PasswordResetOTPRepository;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetOTPRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.otp.expiration.minutes:10}")
    private int otpExpirationMinutes;

    private static final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void sendPasswordResetOTP(ForgotPasswordRequestDTO request) {
        // Check if user exists (don't reveal existence for security)
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        // Even if user doesn't exist, we still return success to prevent email enumeration
        if (user == null) {
            log.warn("Password reset requested for non-existent email: {}", request.getEmail());
            // Simulate delay to prevent timing attacks
            simulateDelay();
            return;
        }

        // Delete any existing OTP for this email
        otpRepository.deleteByEmail(user.getEmail());

        // Generate 6-digit OTP
        String otp = generateOTP();

        // Create new OTP record
        PasswordResetOTP resetOTP = PasswordResetOTP.builder()
                .email(user.getEmail())
                .otp(otp)
                .expiryDate(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .used(false)
                .attempts(0)
                .build();

        otpRepository.save(resetOTP);

        // Send OTP via email
        try {
            emailService.sendPasswordResetOTP(user.getEmail(), otp, user.getFirstName());
        } catch (Exception e) {
            // Fallback for development - log OTP to console
            emailService.sendOTPForDevelopment(user.getEmail(), otp);
        }

        log.info("Password reset OTP sent to user: {} (Role: {})", user.getEmail(), user.getRole());
    }

    @Transactional
    public boolean verifyOTP(VerifyOTPRequestDTO request) {
        PasswordResetOTP otpRecord = otpRepository
                .findByEmailAndOtpAndUsedFalse(request.getEmail(), request.getOtp())
                .orElse(null);

        if (otpRecord == null) {
            log.warn("Invalid OTP attempt for email: {}", request.getEmail());
            return false;
        }

        // Check if OTP is expired
        if (otpRecord.isExpired()) {
            log.warn("Expired OTP used for email: {}", request.getEmail());
            otpRepository.delete(otpRecord);
            return false;
        }

        return true;
    }

    @Transactional
    public void resetPasswordWithOTP(ResetPasswordWithOTPRequestDTO request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Validate password strength
        if (request.getNewPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        // Find and validate OTP
        PasswordResetOTP otpRecord = otpRepository
                .findByEmailAndOtpAndUsedFalse(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP"));

        // Check if OTP is expired
        if (otpRecord.isExpired()) {
            otpRepository.delete(otpRecord);
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }

        // Check attempts
        otpRecord.setAttempts(otpRecord.getAttempts() + 1);
        if (otpRecord.hasExceededMaxAttempts()) {
            otpRepository.delete(otpRecord);
            throw new RuntimeException("Too many failed attempts. Please request a new OTP.");
        }
        otpRepository.save(otpRecord);

        // Find user (works for ALL roles)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Mark OTP as used and delete it
        otpRecord.setUsed(true);
        otpRepository.save(otpRecord);

        // Clean up expired OTPs
        otpRepository.deleteExpiredOtps();

        log.info("Password reset successful for user: {} (Role: {})", user.getEmail(), user.getRole());
    }

    private String generateOTP() {
        // Generate 6-digit OTP
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    private void simulateDelay() {
        try {
            Thread.sleep(1000); // 1 second delay to prevent timing attacks
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}