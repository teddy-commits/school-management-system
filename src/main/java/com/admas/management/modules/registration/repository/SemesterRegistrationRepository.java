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
}