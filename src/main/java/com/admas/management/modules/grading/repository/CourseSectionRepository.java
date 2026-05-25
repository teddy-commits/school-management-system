package com.admas.management.modules.grading.repository;

import com.admas.management.modules.grading.model.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {

    List<CourseSection> findByCourseId(Long courseId);

    List<CourseSection> findByDepartmentId(Long departmentId);

    @Query("SELECT cs FROM CourseSection cs WHERE cs.department.id = :departmentId AND cs.semester = :semester AND cs.academicYear = :year")
    List<CourseSection> findByDepartmentIdAndSemesterAndAcademicYear(
            @Param("departmentId") Long departmentId,
            @Param("semester") String semester,
            @Param("year") Integer year);

    List<CourseSection> findBySemesterAndAcademicYear(String semester, Integer academicYear);

    @Query("SELECT cs FROM CourseSection cs WHERE cs.status = 'OPEN' AND cs.semester = :semester AND cs.academicYear = :year")
    List<CourseSection> findOpenSectionsBySemester(
            @Param("semester") String semester,
            @Param("year") Integer year);

    @Query("SELECT DISTINCT si.section FROM SectionInstructor si " +
            "WHERE si.instructor.email = :instructorEmail " +
            "AND si.section.semester = :semester " +
            "AND si.section.academicYear = :year")
    List<CourseSection> findSectionsByInstructorEmail(
            @Param("instructorEmail") String instructorEmail,
            @Param("semester") String semester,
            @Param("year") Integer year);

    @Query("SELECT DISTINCT si.section FROM SectionInstructor si " +
            "WHERE si.instructor.email = :instructorEmail")
    List<CourseSection> findAllSectionsByInstructorEmail(
            @Param("instructorEmail") String instructorEmail);

    @Query("SELECT DISTINCT si.section FROM SectionInstructor si WHERE si.instructor.id = :instructorId")
    List<CourseSection> findByInstructorId(@Param("instructorId") Long instructorId);

    boolean existsByDepartmentIdAndSectionCodeAndSemesterAndAcademicYear(
            Long departmentId, String sectionCode, String semester, Integer academicYear);

    boolean existsByCourseIdAndSectionCodeAndSemesterAndAcademicYear(
            Long courseId, String sectionCode, String semester, Integer academicYear);

    List<CourseSection> findByStatus(CourseSection.SectionStatus status);
}