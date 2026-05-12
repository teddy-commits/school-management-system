package com.admas.management.modules.grading.repository;

import com.admas.management.modules.grading.model.entity.SectionInstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionInstructorRepository extends JpaRepository<SectionInstructor, Long> {
    List<SectionInstructor> findBySectionId(Long sectionId);
    List<SectionInstructor> findByInstructorId(Long instructorId);
    boolean existsBySectionIdAndInstructorId(Long sectionId, Long instructorId);
    long countBySectionId(Long sectionId);
    List<SectionInstructor> findByInstructorEmailAndSection_SemesterAndSection_AcademicYear(
            String instructorEmail, String semester, Integer academicYear);
}