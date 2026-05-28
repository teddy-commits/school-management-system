package com.admas.management.modules.grading.repository;

import com.admas.management.modules.registration.model.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

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
}
