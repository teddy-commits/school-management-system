package com.admas.management.modules.grading.service;


import com.admas.management.modules.grading.dto.request.CourseSectionRequestDTO;
import com.admas.management.modules.grading.model.dto.response.CourseSectionResponseDTO;

import java.util.List;

public interface CourseSectionService {

    CourseSectionResponseDTO createSection(CourseSectionRequestDTO request);

    CourseSectionResponseDTO updateSection(Long id, CourseSectionRequestDTO request);

    CourseSectionResponseDTO getSectionById(Long id);

    List<CourseSectionResponseDTO> getSectionsByCourse(Long courseId);

    List<CourseSectionResponseDTO> getSectionsByInstructor(Long instructorId);

    List<CourseSectionResponseDTO> getSectionsByInstructorEmail(String instructorEmail, String semester, Integer academicYear);

    List<CourseSectionResponseDTO> getSectionsBySemester(String semester, Integer academicYear);
    List<CourseSectionResponseDTO> getAllSections();
    List<CourseSectionResponseDTO> getOpenSectionsBySemester(String semester, Integer academicYear);

    void deleteSection(Long id);

    void updateSectionStatus(Long id, String status);

    boolean hasAvailableSeats(Long sectionId);
}