package com.admas.management.modules.grading.repository;

import com.admas.management.modules.grading.model.entity.StudentEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Long> {

    List<StudentEnrollment> findByStudentId(Long studentId);

    List<StudentEnrollment> findBySectionId(Long sectionId);

    Optional<StudentEnrollment> findByStudentIdAndSectionId(Long studentId, Long sectionId);

    boolean existsByStudentIdAndSectionId(Long studentId, Long sectionId);

    long countBySectionId(Long sectionId);
    List<StudentEnrollment> findByStudentIdAndSection_SemesterAndSection_AcademicYear(
            Long studentId, String semester, Integer academicYear);
    @Query("SELECT se FROM StudentEnrollment se WHERE se.section.instructor.id = :instructorId AND se.section.semester = :semester AND se.section.academicYear = :year")
    List<StudentEnrollment> findStudentsByInstructorAndSemester(@Param("instructorId") Long instructorId,
                                                                @Param("semester") String semester,
                                                                @Param("year") Integer year);

    @Query("SELECT se FROM StudentEnrollment se WHERE se.student.id = :studentId AND se.section.semester = :semester AND se.section.academicYear = :year")
    List<StudentEnrollment> findStudentEnrollmentsBySemester(@Param("studentId") Long studentId,
                                                             @Param("semester") String semester,
                                                             @Param("year") Integer year);

    @Query("SELECT COUNT(se) FROM StudentEnrollment se WHERE se.section.id = :sectionId AND se.status = 'ENROLLED'")
    long countActiveEnrollmentsBySection(@Param("sectionId") Long sectionId);
}