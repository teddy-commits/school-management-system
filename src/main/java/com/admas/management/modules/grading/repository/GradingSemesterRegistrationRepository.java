package com.admas.management.modules.grading.repository;


import com.admas.management.modules.registration.model.SemesterRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SemesterRegistrationRepository extends JpaRepository<SemesterRegistration, Long> {

    Optional<SemesterRegistration> findByStudentIdAndSemesterAndAcademicYear(
            Long studentId, String semester, Integer academicYear);

    List<SemesterRegistration> findByStudentId(Long studentId);

    @Query("SELECT sr FROM SemesterRegistration sr WHERE sr.student.id = :studentId AND sr.status = 'COMPLETED'")
    List<SemesterRegistration> findCompletedRegistrationsByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT SUM(sr.totalCredits) FROM SemesterRegistration sr WHERE sr.student.id = :studentId AND sr.semester = :semester AND sr.academicYear = :academicYear")
    Double getTotalCreditsForSemester(@Param("studentId") Long studentId,
                                      @Param("semester") String semester,
                                      @Param("academicYear") Integer academicYear);
}
