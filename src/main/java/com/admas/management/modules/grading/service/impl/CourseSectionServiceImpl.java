package com.admas.management.modules.grading.service.impl;

import com.admas.management.modules.grading.dto.request.CourseSectionRequestDTO;
import com.admas.management.modules.grading.model.dto.response.CourseSectionResponseDTO;
import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.model.entity.CourseSection;
import com.admas.management.modules.grading.repository.CourseRepository;
import com.admas.management.modules.grading.repository.CourseSectionRepository;
import com.admas.management.modules.grading.service.CourseSectionService;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseSectionServiceImpl implements CourseSectionService {

    private final CourseSectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public CourseSectionResponseDTO createSection(CourseSectionRequestDTO request) {
        log.info("Creating section for course: {}", request.getCourseId());

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if section already exists
        boolean exists = sectionRepository.existsByCourseIdAndSectionCodeAndSemesterAndAcademicYear(
                request.getCourseId(), request.getSectionCode(), request.getSemester(), request.getAcademicYear());

        if (exists) {
            throw new RuntimeException("Section already exists for this course, semester, and academic year");
        }

        CourseSection section = new CourseSection();
        section.setCourse(course);
        section.setSectionCode(request.getSectionCode().toUpperCase());
        section.setAcademicYearLevel(request.getAcademicYearLevel()); // IMPORTANT: Set this!
        section.setSemester(request.getSemester());
        section.setAcademicYear(request.getAcademicYear());
        section.setMaxStudents(request.getMaxStudents() != null ? request.getMaxStudents() : 40);
        section.setSchedule(request.getSchedule());
        section.setRoom(request.getRoom());
        section.setEnrolledStudents(0);
        section.setStatus(CourseSection.SectionStatus.OPEN);

        if (request.getInstructorId() != null) {
            User instructor = userRepository.findById(request.getInstructorId())
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));
            section.setInstructor(instructor);
        }

        CourseSection saved = sectionRepository.save(section);
        return mapToResponseDTO(saved, "Section created successfully");
    }
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public CourseSectionResponseDTO updateSection(Long id, CourseSectionRequestDTO request) {
        log.info("Updating section with id: {}", id);

        CourseSection section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        if (request.getMaxStudents() != null) section.setMaxStudents(request.getMaxStudents());
        if (request.getSchedule() != null) section.setSchedule(request.getSchedule());
        if (request.getRoom() != null) section.setRoom(request.getRoom());

        if (request.getInstructorId() != null) {
            User instructor = userRepository.findById(request.getInstructorId())
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));
            section.setInstructor(instructor);
        }

        CourseSection updated = sectionRepository.save(section);
        return mapToResponseDTO(updated, "Section updated successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public CourseSectionResponseDTO getSectionById(Long id) {
        CourseSection section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));
        return mapToResponseDTO(section, null);
    }
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public List<CourseSectionResponseDTO> getAllSections() {
        log.info("Fetching all sections");
        return sectionRepository.findAll()
                .stream()
                .map(s -> mapToResponseDTO(s, null))
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<CourseSectionResponseDTO> getSectionsByCourse(Long courseId) {
        return sectionRepository.findByCourseId(courseId)
                .stream()
                .map(s -> mapToResponseDTO(s, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSectionResponseDTO> getSectionsByInstructor(Long instructorId) {
        return sectionRepository.findByInstructorId(instructorId)
                .stream()
                .map(s -> mapToResponseDTO(s, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSectionResponseDTO> getSectionsByInstructorEmail(String instructorEmail, String semester, Integer academicYear) {
        return sectionRepository.findSectionsByInstructorEmail(instructorEmail, semester, academicYear)
                .stream()
                .map(s -> mapToResponseDTO(s, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSectionResponseDTO> getSectionsBySemester(String semester, Integer academicYear) {
        return sectionRepository.findBySemesterAndAcademicYear(semester, academicYear)
                .stream()
                .map(s -> mapToResponseDTO(s, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSectionResponseDTO> getOpenSectionsBySemester(String semester, Integer academicYear) {
        return sectionRepository.findOpenSectionsBySemester(semester, academicYear)
                .stream()
                .map(s -> mapToResponseDTO(s, null))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public void deleteSection(Long id) {
        sectionRepository.deleteById(id);
        log.info("Deleted section with id: {}", id);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public void updateSectionStatus(Long id, String status) {
        CourseSection section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));
        section.setStatus(CourseSection.SectionStatus.valueOf(status));
        sectionRepository.save(section);
        log.info("Section {} status updated to {}", id, status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAvailableSeats(Long sectionId) {
        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));
        return section.hasAvailableSeats();
    }

    private CourseSectionResponseDTO mapToResponseDTO(CourseSection section, String message) {
        return CourseSectionResponseDTO.builder()
                .id(section.getId())
                .courseId(section.getCourse().getId())
                .courseCode(section.getCourse().getCourseCode())
                .courseName(section.getCourse().getCourseName())
                .sectionCode(section.getSectionCode())
                .academicYearLevel(section.getAcademicYearLevel())
                .semester(section.getSemester())
                .academicYear(section.getAcademicYear())
                .instructorId(section.getInstructor() != null ? section.getInstructor().getId() : null)
                .instructorName(section.getInstructor() != null ? section.getInstructor().getFullName() : null)
                .instructorEmail(section.getInstructor() != null ? section.getInstructor().getEmail() : null)
                .maxStudents(section.getMaxStudents())
                .enrolledStudents(section.getEnrolledStudents())
                .schedule(section.getSchedule())
                .room(section.getRoom())
                .status(section.getStatus().name())
                .hasAvailableSeats(section.hasAvailableSeats())
                .formattedSectionName(section.getFormattedSectionName())
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .message(message != null ? message : "Section retrieved successfully")
                .build();
    }
}