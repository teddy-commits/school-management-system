package com.admas.management.modules.grading.service.impl;

import com.admas.management.modules.grading.dto.request.StudentEnrollmentRequestDTO;
import com.admas.management.modules.grading.dto.response.StudentEnrollmentResponseDTO;
import com.admas.management.modules.grading.model.entity.CourseSection;
import com.admas.management.modules.grading.model.entity.StudentEnrollment;
import com.admas.management.modules.grading.repository.CourseSectionRepository;
import com.admas.management.modules.grading.repository.StudentEnrollmentRepository;
import com.admas.management.modules.grading.service.StudentEnrollmentService;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentEnrollmentServiceImpl implements StudentEnrollmentService {

    private final StudentEnrollmentRepository enrollmentRepository;
    private final CourseSectionRepository sectionRepository;
    private final UserRepository userRepository;

    @Override
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public StudentEnrollmentResponseDTO enrollStudent(StudentEnrollmentRequestDTO request) {
        log.info("Enrolling student {} in section {}", request.getStudentId(), request.getSectionId());

        // Check if already enrolled
        if (enrollmentRepository.existsByStudentIdAndSectionId(request.getStudentId(), request.getSectionId())) {
            throw new RuntimeException("Student already enrolled in this section");
        }

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CourseSection section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found"));

        // Check if section has available seats
        if (!section.hasAvailableSeats()) {
            throw new RuntimeException("No available seats in this section");
        }

        // Check if section is open for registration
        if (section.getStatus() != CourseSection.SectionStatus.OPEN) {
            throw new RuntimeException("Section is not open for registration");
        }

        StudentEnrollment enrollment = new StudentEnrollment();
        enrollment.setStudent(student);
        enrollment.setSection(section);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus(StudentEnrollment.EnrollmentStatus.ENROLLED);

        StudentEnrollment saved = enrollmentRepository.save(enrollment);

        // Update section enrollment count
        section.incrementEnrolledStudents();
        sectionRepository.save(section);

        return mapToResponseDTO(saved, "Successfully enrolled in " + section.getCourse().getCourseCode() + " Section " + section.getSectionCode());
    }

    @Override
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public StudentEnrollmentResponseDTO dropCourse(Long enrollmentId) {
        log.info("Dropping enrollment: {}", enrollmentId);

        StudentEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setStatus(StudentEnrollment.EnrollmentStatus.DROPPED);
        StudentEnrollment updated = enrollmentRepository.save(enrollment);

        // Update section enrollment count
        CourseSection section = enrollment.getSection();
        section.decrementEnrolledStudents();
        sectionRepository.save(section);

        return mapToResponseDTO(updated, "Successfully dropped the course");
    }

    @Override
    @Transactional(readOnly = true)
    public StudentEnrollmentResponseDTO getEnrollmentById(Long id) {
        StudentEnrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        return mapToResponseDTO(enrollment, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentEnrollmentResponseDTO> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(e -> mapToResponseDTO(e, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentEnrollmentResponseDTO> getStudentEnrollmentsBySemester(Long studentId, String semester, Integer academicYear) {
        return enrollmentRepository.findStudentEnrollmentsBySemester(studentId, semester, academicYear)
                .stream()
                .map(e -> mapToResponseDTO(e, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public List<StudentEnrollmentResponseDTO> getSectionEnrollments(Long sectionId) {
        return enrollmentRepository.findBySectionId(sectionId)
                .stream()
                .map(e -> mapToResponseDTO(e, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public List<StudentEnrollmentResponseDTO> getInstructorStudents(Long instructorId, String semester, Integer academicYear) {
        return enrollmentRepository.findStudentsByInstructorAndSemester(instructorId, semester, academicYear)
                .stream()
                .map(e -> mapToResponseDTO(e, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long studentId, Long sectionId) {
        return enrollmentRepository.existsByStudentIdAndSectionId(studentId, sectionId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getEnrollmentCount(Long sectionId) {
        return enrollmentRepository.countActiveEnrollmentsBySection(sectionId);
    }

    private StudentEnrollmentResponseDTO mapToResponseDTO(StudentEnrollment enrollment, String message) {
        CourseSection section = enrollment.getSection();
        return StudentEnrollmentResponseDTO.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getFullName())
                .studentIdNumber(enrollment.getStudent().getStudentId())
                .sectionId(section.getId())
                .courseCode(section.getCourse().getCourseCode())
                .courseName(section.getCourse().getCourseName())
                .sectionCode(section.getSectionCode())
                .instructorName(section.getInstructor() != null ? section.getInstructor().getFullName() : null)
                .schedule(section.getSchedule())
                .room(section.getRoom())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus().name())
                .semester(section.getSemester())
                .academicYear(section.getAcademicYear())
                .message(message != null ? message : "Enrollment retrieved successfully")
                .build();
    }
}