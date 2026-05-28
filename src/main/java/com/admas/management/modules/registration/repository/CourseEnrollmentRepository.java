package com.admas.management.modules.registration.repository;

import com.admas.management.modules.registration.model.CourseEnrollment;
import com.admas.management.modules.registration.model.SemesterRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    List<CourseEnrollment> findBySemesterRegistration(SemesterRegistration registration);

    long countBySemesterRegistrationAndStatus(SemesterRegistration registration, CourseEnrollment.EnrollmentStatus status);

    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.semesterRegistration.student.id = :studentId AND ce.semesterRegistration.semester = :semester AND ce.semesterRegistration.academicYear = :academicYear")
    List<CourseEnrollment> findByStudentAndSemesterAndAcademicYear(
            @Param("studentId") Long studentId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear);

    @Query("SELECT ce.course.id FROM CourseEnrollment ce WHERE ce.semesterRegistration.student.id = :studentId AND ce.semesterRegistration.semester = :semester AND ce.semesterRegistration.academicYear = :academicYear AND ce.status = 'ENROLLED'")
    List<Long> findRegisteredCourseIdsByStudentAndSemester(
            @Param("studentId") Long studentId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear);
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.semesterRegistration.student.id = :studentId AND ce.course.id = :courseId AND ce.semesterRegistration.semester = :semester AND ce.semesterRegistration.academicYear = :academicYear")
    Optional<CourseEnrollment> findByStudentAndCourseAndSemesterAndAcademicYear(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear);
    @Query("SELECT CASE WHEN COUNT(ce) > 0 THEN true ELSE false END FROM CourseEnrollment ce WHERE ce.semesterRegistration.student.id = :studentId AND ce.course.id = :courseId AND ce.semesterRegistration.semester = :semester AND ce.semesterRegistration.academicYear = :academicYear AND ce.status = 'ENROLLED'")
    boolean existsByStudentAndCourseAndSemesterAndAcademicYear(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear);

    @Query("SELECT SUM(ce.credits) FROM CourseEnrollment ce WHERE ce.semesterRegistration.student.id = :studentId AND ce.semesterRegistration.semester = :semester AND ce.semesterRegistration.academicYear = :academicYear AND ce.status = 'ENROLLED'")
    Double getTotalCreditsByStudentAndSemester(
            @Param("studentId") Long studentId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear);

    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.course.id = :courseId AND ce.semesterRegistration.semester = :semester AND ce.semesterRegistration.academicYear = :academicYear AND ce.status = 'ENROLLED'")
    List<CourseEnrollment> findEnrollmentsByCourseAndSemester(
            @Param("courseId") Long courseId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear);

    @Query("SELECT COUNT(ce) FROM CourseEnrollment ce WHERE ce.course.id = :courseId AND ce.semesterRegistration.semester = :semester AND ce.semesterRegistration.academicYear = :academicYear AND ce.status = 'ENROLLED'")
    Long countEnrolledStudentsByCourseAndSemester(
            @Param("courseId") Long courseId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear);

    List<CourseEnrollment> findBySemesterRegistrationId(Long semesterRegistrationId);
    List<CourseEnrollment> findByCourseId(Long courseId);
    List<CourseEnrollment> findByStatus(CourseEnrollment.EnrollmentStatus status);

    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.semesterRegistration.student.id = :studentId AND ce.status = :status")
    List<CourseEnrollment> findByStudentIdAndStatus(
            @Param("studentId") Long studentId,
            @Param("status") CourseEnrollment.EnrollmentStatus status);

    void deleteBySemesterRegistrationId(Long semesterRegistrationId);
    List<CourseEnrollment> findBySemesterRegistrationAndStatus(SemesterRegistration registration, CourseEnrollment.EnrollmentStatus status);
}