package com.admas.management.modules.grading.repository;

import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.model.entity.Enrollment;
import com.admas.management.modules.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(User student);
    List<Enrollment> findByCourse(Course course);
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status = 'ENROLLED'")
    List<Enrollment> findActiveEnrollmentsByStudent(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'ENROLLED'")
    Long countActiveEnrollmentsByCourse(@Param("courseId") Long courseId);

    long countByStudentIdAndSemesterAndAcademicYear(Long studentId, String semester, Integer academicYear);
    boolean existsByStudentAndCourseAndSemesterAndAcademicYear(
            User student, Course course, String semester, Integer academicYear);

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.semester = :semester AND e.academicYear = :academicYear")
    List<Enrollment> findByCourseIdAndSemesterAndAcademicYear(
            @Param("courseId") Long courseId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.semester = :semester AND e.academicYear = :academicYear")
    List<Enrollment> findByStudentIdAndSemesterAndAcademicYear(
            @Param("studentId") Long studentId,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear);

    boolean existsByStudentAndCourseAndStatus(User student, Course course, Enrollment.EnrollmentStatus status);
}
