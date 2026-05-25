package com.admas.management.modules.shared.repository;

import com.admas.management.modules.shared.model.PasswordResetOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface PasswordResetOTPRepository extends JpaRepository<PasswordResetOTP, Long> {

    Optional<PasswordResetOTP> findByEmailAndOtpAndUsedFalse(String email, String otp);

    Optional<PasswordResetOTP> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetOTP p WHERE p.expiryDate < CURRENT_TIMESTAMP")
    void deleteExpiredOtps();

    @Modifying
    @Transactional
    void deleteByEmail(String email);
}
