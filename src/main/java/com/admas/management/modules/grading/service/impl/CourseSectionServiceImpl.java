package com.admas.management.modules.grading.service.impl;

import com.admas.management.modules.department.model.Department;
import com.admas.management.modules.department.repository.DepartmentRepository;
import com.admas.management.modules.grading.dto.request.CourseSectionRequestDTO;
import com.admas.management.modules.grading.dto.response.SectionCourseDetailDTO;
import com.admas.management.modules.grading.dto.response.*;
import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.model.entity.CourseSection;
import com.admas.management.modules.grading.model.entity.SectionCourse;
import com.admas.management.modules.grading.model.entity.SectionInstructor;
import com.admas.management.modules.grading.repository.CourseRepository;
import com.admas.management.modules.grading.repository.CourseSectionRepository;
import com.admas.management.modules.grading.service.CourseSectionService;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import com.admas.management.modules.grading.repository.SectionInstructorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.admas.management.modules.grading.repository.SectionCourseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseSectionServiceImpl implements CourseSectionService {

    private final CourseSectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository; // Add this
    private final UserRepository userRepository;
    private final SectionCourseRepository sectionCourseRepository;
    private final SectionInstructorRepository sectionInstructorRepository;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public CourseSectionResponseDTO createSection(CourseSectionRequestDTO request) {
        log.info("Creating section for department: {}", request.getDepartmentId());

        // Get department instead of course
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.getDepartmentId()));

        // Check if section already exists for this department, section code, semester, and academic year
        boolean exists = sectionRepository.existsByDepartmentIdAndSectionCodeAndSemesterAndAcademicYear(
                request.getDepartmentId(), request.getSectionCode(), request.getSemester(), request.getAcademicYear());

        if (exists) {
            throw new RuntimeException("Section already exists for this department, semester, and academic year");
        }

        CourseSection section = new CourseSection();
        section.setDepartment(department);  // Set department instead of course
        section.setSectionCode(request.getSectionCode().toUpperCase());
        section.setAcademicYearLevel(request.getAcademicYearLevel()); // Year of study (1,2,3,4,5)
        section.setSemester(request.getSemester());
        section.setAcademicYear(request.getAcademicYear());
        section.setMaxStudents(request.getMaxStudents() != null ? request.getMaxStudents() : 40);
        section.setEnrolledStudents(0);
        section.setStatus(CourseSection.SectionStatus.OPEN);

        // No instructor assignment at creation - will be added later
        // No schedule or room - will be added later per course

        CourseSection saved = sectionRepository.save(section);
        return mapToResponseDTO(saved, "Section created successfully");
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public CourseSectionResponseDTO updateSection(Long id, CourseSectionRequestDTO request) {
        log.info("Updating section with id: {}", id);

        CourseSection section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        // Update only the fields that can be changed
        if (request.getMaxStudents() != null) section.setMaxStudents(request.getMaxStudents());
        if (request.getSectionCode() != null) section.setSectionCode(request.getSectionCode().toUpperCase());
        if (request.getAcademicYearLevel() != null) section.setAcademicYearLevel(request.getAcademicYearLevel());
        if (request.getStatus() != null) section.setStatus(CourseSection.SectionStatus.valueOf(request.getStatus()));

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
    public List<CourseSectionResponseDTO> getAllSections() {
        return sectionRepository.findAll()
                .stream()
                .map(section -> mapToResponseDTO(section, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSectionResponseDTO> getSectionsByCourse(Long courseId) {
        return sectionRepository.findByCourseId(courseId)
                .stream()
                .map(section -> mapToResponseDTO(section, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSectionResponseDTO> getSectionsByInstructor(Long instructorId) {
        return sectionRepository.findByInstructorId(instructorId)
                .stream()
                .map(section -> mapToResponseDTO(section, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSectionResponseDTO> getSectionsByInstructorEmail(String instructorEmail, String semester, Integer academicYear) {
        log.info("Getting sections for instructor: {}, semester: {}, year: {}", instructorEmail, semester, academicYear);

        // Find all SectionInstructor records for this instructor with the given semester and year
        List<SectionInstructor> instructorAssignments = sectionInstructorRepository
                .findByInstructorEmailAndSection_SemesterAndSection_AcademicYear(
                        instructorEmail, semester, academicYear);

        // Extract and map sections from the assignments
        return instructorAssignments.stream()
                .map(SectionInstructor::getSection)
                .distinct() // In case instructor is assigned to same section multiple times (different courses)
                .map(section -> mapToResponseDTO(section, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSectionResponseDTO> getSectionsBySemester(String semester, Integer academicYear) {
        return sectionRepository.findBySemesterAndAcademicYear(semester, academicYear)
                .stream()
                .map(section -> mapToResponseDTO(section, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSectionResponseDTO> getOpenSectionsBySemester(String semester, Integer academicYear) {
        return sectionRepository.findOpenSectionsBySemester(semester, academicYear)
                .stream()
                .map(section -> mapToResponseDTO(section, null))
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
                .departmentId(section.getDepartment() != null ? section.getDepartment().getId() : null)
                .departmentCode(section.getDepartment() != null ? section.getDepartment().getCode() : null)
                .departmentName(section.getDepartment() != null ? section.getDepartment().getName() : null)
                .sectionCode(section.getSectionCode())
                .academicYearLevel(section.getAcademicYearLevel())
                .semester(section.getSemester())
                .academicYear(section.getAcademicYear())
                .maxStudents(section.getMaxStudents())
                .enrolledStudents(section.getEnrolledStudents())
                .status(section.getStatus().name())
                .hasAvailableSeats(section.hasAvailableSeats())
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .message(message != null ? message : "Section retrieved successfully")
                .build();
    }
    @Override
    public SectionInstructorResponseDTO addInstructorToSection(Long sectionId, Long instructorId, Long courseId) {
        log.info("Adding instructor {} to section {}", instructorId, sectionId);

        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        if (!section.canAddInstructor()) {
            throw new RuntimeException("Section already has maximum 7 instructors");
        }

        if (sectionInstructorRepository.existsBySectionIdAndInstructorId(sectionId, instructorId)) {
            throw new RuntimeException("Instructor already assigned to this section");
        }

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Course course = null;
        if (courseId != null) {
            course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
        }

        SectionInstructor sectionInstructor = new SectionInstructor();
        sectionInstructor.setSection(section);
        sectionInstructor.setInstructor(instructor);
        sectionInstructor.setCourse(course);

        SectionInstructor saved = sectionInstructorRepository.save(sectionInstructor);

        // Return DTO instead of entity
        return SectionInstructorResponseDTO.builder()
                .id(saved.getId())
                .instructorId(saved.getInstructor().getId())
                .instructorName(saved.getInstructor().getFullName())
                .instructorEmail(saved.getInstructor().getEmail())
                .courseId(saved.getCourse() != null ? saved.getCourse().getId() : null)
                .courseCode(saved.getCourse() != null ? saved.getCourse().getCourseCode() : null)
                .courseName(saved.getCourse() != null ? saved.getCourse().getCourseName() : null)
                .createdAt(saved.getCreatedAt())
                .message("Instructor added successfully")
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<SectionCourseDetailDTO> getCoursesByInstructorEmail(String instructorEmail, String semester, Integer academicYear) {
        log.info("Getting courses for instructor: {}, semester: {}, year: {}", instructorEmail, semester, academicYear);

        List<SectionCourse> sectionCourses = sectionCourseRepository
                .findByInstructorEmailAndSemesterAndAcademicYear(instructorEmail, semester, academicYear);

        return sectionCourses.stream()
                .map(sc -> {
                    CourseSection section = sc.getSection();
                    Course course = sc.getCourse();

                    return SectionCourseDetailDTO.builder()
                            .id(course.getId())
                            .sectionId(section.getId())  // ✅ ADD THIS LINE
                            .courseCode(course.getCourseCode())
                            .courseName(course.getCourseName())
                            .credits(course.getCredits())
                            .status(course.getStatus() != null ? course.getStatus().name() : "OPEN")
                            .schedule(sc.getSchedule())
                            .room(sc.getRoom())
                            .sectionCode(section.getSectionCode())
                            .semester(section.getSemester())
                            .academicYear(section.getAcademicYear())
                            .enrolledStudents(section.getEnrolledStudents())
                            .maxStudents(section.getMaxStudents())
                            .department(section.getDepartment() != null ? section.getDepartment().getName() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }
    @Override
    public SectionCourseResponseDTO addCourseToSection(Long sectionId, Long courseId, String schedule, String room) {
        log.info("Adding course {} to section {}", courseId, sectionId);

        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        if (!section.canAddCourse()) {
            throw new RuntimeException("Section already has maximum 7 courses");
        }

        if (sectionCourseRepository.existsBySectionIdAndCourseId(sectionId, courseId)) {
            throw new RuntimeException("Course already added to this section");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        SectionCourse sectionCourse = new SectionCourse();
        sectionCourse.setSection(section);
        sectionCourse.setCourse(course);
        sectionCourse.setSchedule(schedule);
        sectionCourse.setRoom(room);
        sectionCourse.setCredits(course.getCredits());

        SectionCourse saved = sectionCourseRepository.save(sectionCourse);

        return SectionCourseResponseDTO.builder()
                .id(saved.getId())
                .courseId(saved.getCourse().getId())
                .courseCode(saved.getCourse().getCourseCode())
                .courseName(saved.getCourse().getCourseName())
                .credits(saved.getCourse().getCredits())
                .schedule(saved.getSchedule())
                .room(saved.getRoom())
                .addedAt(saved.getCreatedAt())
                .message("Course added successfully")
                .build();
    }

    @Override
    public List<SectionCourseDTO> getSectionCourses(Long sectionId) {
        log.info("Getting courses for section: {}", sectionId);

        List<SectionCourse> courses = sectionCourseRepository.findBySectionId(sectionId);

        return courses.stream()
                .map(sc -> SectionCourseDTO.builder()
                        .id(sc.getId())
                        .courseId(sc.getCourse().getId())
                        .courseCode(sc.getCourse().getCourseCode())
                        .courseName(sc.getCourse().getCourseName())
                        .credits(sc.getCourse().getCredits())
                        .schedule(sc.getSchedule())
                        .room(sc.getRoom())
                        .addedAt(sc.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void removeInstructorFromSection(Long sectionInstructorId) {
        log.info("Removing instructor from section with id: {}", sectionInstructorId);
        SectionInstructor sectionInstructor = sectionInstructorRepository.findById(sectionInstructorId)
                .orElseThrow(() -> new RuntimeException("Section instructor assignment not found"));
        sectionInstructorRepository.delete(sectionInstructor);
    }

    @Override
    public List<SectionInstructorDTO> getSectionInstructors(Long sectionId) {
        log.info("Getting instructors for section: {}", sectionId);

        List<SectionInstructor> instructors = sectionInstructorRepository.findBySectionId(sectionId);

        return instructors.stream()
                .map(si -> SectionInstructorDTO.builder()
                        .id(si.getId())
                        .instructorId(si.getInstructor().getId())
                        .instructorName(si.getInstructor().getFullName())
                        .instructorEmail(si.getInstructor().getEmail())
                        .department(si.getInstructor().getDepartmentName())
                        .designation(si.getInstructor().getDesignation())
                        .courseId(si.getCourse() != null ? si.getCourse().getId() : null)
                        .courseCode(si.getCourse() != null ? si.getCourse().getCourseCode() : null)
                        .courseName(si.getCourse() != null ? si.getCourse().getCourseName() : null)
                        .assignedAt(si.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    @Override
    public boolean canAddMoreInstructors(Long sectionId) {
        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));
        return section.canAddInstructor();
    }

    @Override
    public void removeCourseFromSection(Long sectionCourseId) {
        log.info("Removing course from section with id: {}", sectionCourseId);
        SectionCourse sectionCourse = sectionCourseRepository.findById(sectionCourseId)
                .orElseThrow(() -> new RuntimeException("Section course assignment not found"));
        sectionCourseRepository.delete(sectionCourse);
    }

    @Override
    public boolean canAddMoreCourses(Long sectionId) {
        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));
        return section.canAddCourse();
    }
    @Override
    public List<SectionCourse> getSectionCoursesEntities(Long sectionId) {
        return sectionCourseRepository.findBySectionId(sectionId);
    }
}