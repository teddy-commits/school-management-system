package com.admas.management.modules.registration.repository;

import com.admas.management.modules.registration.model.RegistrationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationSessionRepository extends JpaRepository<RegistrationSession, Long> {

    Optional<RegistrationSession> findBySemesterAndAcademicYear(String semester, Integer academicYear);

    List<RegistrationSession> findByIsActiveTrue();

    // Fix: Use proper date comparison
    @Query("SELECT rs FROM RegistrationSession rs WHERE rs.isActive = true AND rs.startDate <= :now AND rs.endDate >= :now")
    Optional<RegistrationSession> findCurrentOpenSession(@Param("now") LocalDateTime now);

    @Query("SELECT rs FROM RegistrationSession rs WHERE rs.startDate > :now AND rs.isActive = true ORDER BY rs.startDate ASC")
    List<RegistrationSession> findUpcomingSessions(@Param("now") LocalDateTime now);

    boolean existsBySemesterAndAcademicYear(String semester, Integer academicYear);

    // Add this for debugging
    @Query("SELECT rs FROM RegistrationSession rs WHERE rs.isActive = true")
    List<RegistrationSession> findAllActiveSessions();
}