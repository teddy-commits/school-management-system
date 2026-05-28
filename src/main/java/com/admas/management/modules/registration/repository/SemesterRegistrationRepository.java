package com.admas.management.modules.registration.repository;

import com.admas.management.modules.registration.model.SemesterRegistration;
import com.admas.management.modules.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRegistrationRepository extends JpaRepository<SemesterRegistration, Long> {

    List<SemesterRegistration> findByStudent(User student);

    List<SemesterRegistration> findByStudentAndStatus(User student, SemesterRegistration.RegistrationStatus status);

    Optional<SemesterRegistration> findByStudentAndSemesterAndAcademicYear(
            User student, String semester, Integer academicYear);

    @Query("SELECT sr FROM SemesterRegistration sr WHERE sr.student.id = :studentId AND sr.academicYear = :year")
    List<SemesterRegistration> findByStudentIdAndYear(@Param("studentId") Long studentId, @Param("year") Integer year);

    boolean existsByStudentAndSemesterAndAcademicYear(User student, String semester, Integer academicYear);

    @Query("SELECT sr FROM SemesterRegistration sr WHERE sr.student.id = :studentId AND sr.semester = :semester AND sr.academicYear = :academicYear")
    Optional<SemesterRegistration> findByStudentIdAndSemesterAndAcademicYear(
            @Param("studentId") Long studentId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear);

    @Query("SELECT sr FROM SemesterRegistration sr WHERE sr.student.id = :studentId")
    List<SemesterRegistration> findByStudentId(@Param("studentId") Long studentId);
    List<SemesterRegistration> findByStatus(SemesterRegistration.RegistrationStatus status);

    @Query("SELECT sr FROM SemesterRegistration sr WHERE sr.student.id = :studentId AND sr.status = 'COMPLETED'")
    List<SemesterRegistration> findCompletedRegistrationsByStudentId(@Param("studentId") Long studentId);
    @Query("SELECT SUM(sr.totalCredits) FROM SemesterRegistration sr WHERE sr.student.id = :studentId AND sr.semester = :semester AND sr.academicYear = :academicYear")
    Double getTotalCreditsForSemester(
            @Param("studentId") Long studentId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear);
    List<SemesterRegistration> findBySemesterAndAcademicYear(String semester, Integer academicYear);
    @Query("SELECT sr FROM SemesterRegistration sr WHERE sr.student.id = :studentId AND sr.status = :status")
    List<SemesterRegistration> findByStudentIdAndStatus(
            @Param("studentId") Long studentId,
            @Param("status") SemesterRegistration.RegistrationStatus status);

    @Query("SELECT CASE WHEN COUNT(sr) > 0 THEN true ELSE false END FROM SemesterRegistration sr WHERE sr.student.id = :studentId AND sr.semester = :semester AND sr.academicYear = :academicYear")
    boolean existsByStudentIdAndSemesterAndAcademicYear(
            @Param("studentId") Long studentId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear);

    @Query("SELECT sr FROM SemesterRegistration sr WHERE sr.paymentReference IS NOT NULL AND sr.paymentDate IS NOT NULL")
    List<SemesterRegistration> findPaidRegistrations();
    @Query("SELECT sr FROM SemesterRegistration sr WHERE sr.feesDue > 0 AND sr.status != 'PAID'")
    List<SemesterRegistration> findRegistrationsWithOutstandingFees();
}