package com.admas.management.modules.registration.repository;

import com.admas.management.modules.registration.model.CourseEnrollment;
import com.admas.management.modules.registration.model.SemesterRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    List<CourseEnrollment> findBySemesterRegistration(SemesterRegistration registration);

    long countBySemesterRegistrationAndStatus(SemesterRegistration registration, CourseEnrollment.EnrollmentStatus status);
}
