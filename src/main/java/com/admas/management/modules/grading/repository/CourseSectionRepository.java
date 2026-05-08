package com.admas.management.modules.grading.repository;

import com.admas.management.modules.grading.model.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {

    List<CourseSection> findByCourseId(Long courseId);

    List<CourseSection> findByInstructorId(Long instructorId);

    List<CourseSection> findBySemesterAndAcademicYear(String semester, Integer academicYear);

    List<CourseSection> findByCourseIdAndSemesterAndAcademicYear(Long courseId, String semester, Integer academicYear);

    Optional<CourseSection> findByCourseIdAndSectionCodeAndSemesterAndAcademicYear(
            Long courseId, String sectionCode, String semester, Integer academicYear);

    @Query("SELECT cs FROM CourseSection cs WHERE cs.instructor.email = :instructorEmail AND cs.semester = :semester AND cs.academicYear = :year")
    List<CourseSection> findSectionsByInstructorEmail(@Param("instructorEmail") String instructorEmail,
                                                      @Param("semester") String semester,
                                                      @Param("year") Integer year);

    @Query("SELECT cs FROM CourseSection cs WHERE cs.status = 'OPEN' AND cs.semester = :semester AND cs.academicYear = :year")
    List<CourseSection> findOpenSectionsBySemester(@Param("semester") String semester, @Param("year") Integer year);

    List<CourseSection> findByStatus(CourseSection.SectionStatus status);

    boolean existsByCourseIdAndSectionCodeAndSemesterAndAcademicYear(
            Long courseId, String sectionCode, String semester, Integer academicYear);
}