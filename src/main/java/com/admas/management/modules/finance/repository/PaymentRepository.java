package com.admas.management.modules.finance.repository;

import com.admas.management.modules.finance.model.entity.Payment;
import com.admas.management.modules.finance.model.enums.PaymentMethod;
import com.admas.management.modules.finance.model.enums.PaymentStatus;
import com.admas.management.modules.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    // FIXED: Use enum parameter instead of String
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Payment p WHERE p.student.id = :studentId AND p.semester = :semester AND p.academicYear = :academicYear AND p.status = :status")
    boolean existsByStudentIdAndSemesterAndAcademicYearAndStatus(
            @Param("studentId") Long studentId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear,
            @Param("status") PaymentStatus status  // This is now enum, not String
    );

    @Query("SELECT p FROM Payment p WHERE p.student.id = :studentId AND p.semester = :semester AND p.academicYear = :academicYear")
    List<Payment> findByStudentIdAndSemesterAndAcademicYear(
            @Param("studentId") Long studentId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear
    );
    // Alternative: Use named parameter with enum
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.student.id = :studentId AND p.semester = :semester AND p.academicYear = :academicYear AND p.status = :status")
    BigDecimal getTotalPaidForStudentSemester(
            @Param("studentId") Long studentId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear,
            @Param("status") PaymentStatus status
    );
}