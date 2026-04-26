package com.admas.management.modules.finance.repository;

import com.admas.management.modules.finance.model.entity.Payment;
import com.admas.management.modules.finance.model.enums.PaymentMethod;
import com.admas.management.modules.finance.model.enums.PaymentStatus;
import com.admas.management.modules.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStudent(User student);
    List<Payment> findByStudentAndStatus(User student, PaymentStatus status);
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
    List<Payment> findByPaymentMethod(PaymentMethod method);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.student.id = :studentId")
    Double getTotalPaymentsByStudent(@Param("studentId") Long studentId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    Double getTotalPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p.paymentMethod, SUM(p.amount) FROM Payment p GROUP BY p.paymentMethod")
    List<Object[]> getPaymentMethodSummary();
}
