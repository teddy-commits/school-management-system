package com.admas.management.modules.shared.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetOTP(String to, String otp, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset OTP - Student Management System");

        String emailBody = String.format(
                "Dear %s,\n\n" +
                        "We received a request to reset your password for the Student Management System.\n\n" +
                        "Your OTP for password reset is: %s\n\n" +
                        "This OTP will expire in 10 minutes.\n\n" +
                        "If you did not request this password reset, please ignore this email or contact support.\n\n" +
                        "Best regards,\n" +
                        "Student Management System Team",
                firstName, otp
        );

        message.setText(emailBody);

        try {
            mailSender.send(message);
            log.info("Password reset OTP sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", to, e);
            throw new RuntimeException("Failed to send OTP email. Please try again.");
        }
    }

    // For development/testing - log OTP to console if email fails
    public void sendOTPForDevelopment(String to, String otp) {
        log.info("=========================================");
        log.info("PASSWORD RESET OTP FOR: {}", to);
        log.info("OTP: {}", otp);
        log.info("=========================================");
    }
}