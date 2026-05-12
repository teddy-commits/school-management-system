package com.admas.management.modules.grading.service;

import com.admas.management.modules.grading.dto.request.CourseSectionRequestDTO;
import com.admas.management.modules.grading.dto.response.SectionCourseDetailDTO;
import com.admas.management.modules.grading.dto.response.*;
import com.admas.management.modules.grading.model.entity.SectionCourse;

import java.util.List;

public interface CourseSectionService {

    // Basic CRUD
    CourseSectionResponseDTO createSection(CourseSectionRequestDTO request);
    CourseSectionResponseDTO updateSection(Long id, CourseSectionRequestDTO request);
    CourseSectionResponseDTO getSectionById(Long id);
    List<CourseSectionResponseDTO> getAllSections();
    void deleteSection(Long id);
    void updateSectionStatus(Long id, String status);

    // Query methods
    List<CourseSectionResponseDTO> getSectionsByCourse(Long courseId);
    List<CourseSectionResponseDTO> getSectionsByInstructor(Long instructorId);
    List<CourseSectionResponseDTO> getSectionsByInstructorEmail(String instructorEmail, String semester, Integer academicYear);
    List<CourseSectionResponseDTO> getSectionsBySemester(String semester, Integer academicYear);
    List<CourseSectionResponseDTO> getOpenSectionsBySemester(String semester, Integer academicYear);
    boolean hasAvailableSeats(Long sectionId);

    // Section Instructor Management
    // In CourseSectionService.java
    SectionInstructorResponseDTO addInstructorToSection(Long sectionId, Long instructorId, Long courseId);
    void removeInstructorFromSection(Long sectionInstructorId);
    boolean canAddMoreInstructors(Long sectionId);  // Add this

    // Section Course Management
    SectionCourseResponseDTO addCourseToSection(Long sectionId, Long courseId, String schedule, String room);
    void removeCourseFromSection(Long sectionCourseId);
    boolean canAddMoreCourses(Long sectionId);  // Add this
    // Replace the existing methods with DTO versions
    List<SectionInstructorDTO> getSectionInstructors(Long sectionId);
    List<SectionCourseDTO> getSectionCourses(Long sectionId);
    List<SectionCourseDetailDTO> getCoursesByInstructorEmail(String instructorEmail, String semester, Integer academicYear);
    List<SectionCourse> getSectionCoursesEntities(Long sectionId);
}