package com.admas.management.modules.grading.repository;

import com.admas.management.modules.grading.model.entity.SectionCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionCourseRepository extends JpaRepository<SectionCourse, Long> {
    List<SectionCourse> findBySectionId(Long sectionId);
    List<SectionCourse> findByCourseId(Long courseId);
    boolean existsBySectionIdAndCourseId(Long sectionId, Long courseId);
    long countBySectionId(Long sectionId);
    @Query("SELECT sc FROM SectionCourse sc " +
            "JOIN SectionInstructor si ON sc.section.id = si.section.id " +
            "WHERE si.instructor.email = :instructorEmail " +
            "AND sc.section.semester = :semester " +
            "AND sc.section.academicYear = :academicYear")
    List<SectionCourse> findByInstructorEmailAndSemesterAndAcademicYear(
            @Param("instructorEmail") String instructorEmail,
            @Param("semester") String semester,
            @Param("academicYear") Integer academicYear
    );
}