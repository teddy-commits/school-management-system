package com.admas.management.modules.grading.repository;

import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.model.entity.Grade;
import com.admas.management.modules.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByStudentAndCourse(User student, Course course);
    List<Grade> findByStudent(User student);
    List<Grade> findByCourse(Course course);
    List<Grade> findByStudentAndSemester(User student, String semester);

    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.academicYear = :year")
    List<Grade> findGradesByStudentAndYear(@Param("studentId") Long studentId, @Param("year") Integer year);

    @Query("SELECT AVG(g.gradePoint) FROM Grade g WHERE g.student.id = :studentId")
    Double calculateCGPA(@Param("studentId") Long studentId);

    @Query("SELECT SUM(c.credits * g.gradePoint) / SUM(c.credits) FROM Grade g JOIN g.course c WHERE g.student.id = :studentId")
    Double calculateWeightedCGPA(@Param("studentId") Long studentId);

    boolean existsByStudentAndCourse(User student, Course course);
}