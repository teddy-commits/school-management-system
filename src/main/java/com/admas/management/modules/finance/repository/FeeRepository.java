package com.admas.management.modules.finance.repository;

import com.admas.management.modules.finance.model.entity.Fee;
import com.admas.management.modules.finance.model.enums.FeeType;
import com.admas.management.modules.finance.model.enums.PaymentStatus;
import com.admas.management.modules.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {
    List<Fee> findByStudent(User student);
    List<Fee> findByStudentAndStatus(User student, PaymentStatus status);
    List<Fee> findByDueDateBeforeAndStatus(LocalDateTime date, PaymentStatus status);
    List<Fee> findByStudentAndSemesterAndAcademicYear(User student, String semester, Integer academicYear);

    @Query("SELECT SUM(f.dueAmount) FROM Fee f WHERE f.student.id = :studentId")
    Double getTotalOutstandingFees(@Param("studentId") Long studentId);

    @Query("SELECT SUM(f.amount) FROM Fee f WHERE f.student.id = :studentId")
    Double getTotalFees(@Param("studentId") Long studentId);

    @Query("SELECT f FROM Fee f WHERE f.status = 'PENDING' OR f.status = 'PARTIAL'")
    List<Fee> findUnpaidFees();

    boolean existsByStudentAndFeeTypeAndSemester(User student, FeeType feeType, String semester);
}