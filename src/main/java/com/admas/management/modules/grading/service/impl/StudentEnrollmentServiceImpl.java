package com.admas.management.modules.grading.service.impl;

import com.admas.management.modules.grading.dto.request.StudentEnrollmentRequestDTO;
import com.admas.management.modules.grading.dto.response.StudentEnrollmentResponseDTO;
import com.admas.management.modules.grading.model.entity.*;
import com.admas.management.modules.grading.repository.CourseSectionRepository;
import com.admas.management.modules.grading.repository.EnrollmentRepository;
import com.admas.management.modules.grading.repository.SectionCourseRepository;
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

    private final StudentEnrollmentRepository studentEnrollmentRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final CourseSectionRepository sectionRepository;
    private final SectionCourseRepository sectionCourseRepository;
    private final UserRepository userRepository;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public StudentEnrollmentResponseDTO enrollStudent(StudentEnrollmentRequestDTO request) {
        if (studentEnrollmentRepo.existsByStudentIdAndSectionId(request.getStudentId(), request.getSectionId())) {
            throw new RuntimeException("Student already enrolled in this section");
        }

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CourseSection section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found"));
        if (!section.hasAvailableSeats()) {
            throw new RuntimeException("No available seats in this section");
        }
        if (section.getStatus() != CourseSection.SectionStatus.OPEN) {
            throw new RuntimeException("Section is not open for registration");
        }
        StudentEnrollment enrollment = new StudentEnrollment();
        enrollment.setStudent(student);
        enrollment.setSection(section);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus(StudentEnrollment.EnrollmentStatus.ENROLLED);
        StudentEnrollment saved = studentEnrollmentRepo.save(enrollment);
        List<SectionCourse> sectionCourses = sectionCourseRepository.findBySectionId(section.getId());
        int enrolledCourseCount = 0;

        for (SectionCourse sc : sectionCourses) {
            Course course = sc.getCourse();

            boolean alreadyEnrolled = enrollmentRepo
                    .existsByStudentAndCourseAndSemesterAndAcademicYear(
                            student, course, section.getSemester(), section.getAcademicYear());

            if (!alreadyEnrolled) {
                Enrollment courseEnrollment = new Enrollment();
                courseEnrollment.setStudent(student);
                courseEnrollment.setCourse(course);
                courseEnrollment.setSemester(section.getSemester());
                courseEnrollment.setAcademicYear(section.getAcademicYear());
                courseEnrollment.setEnrollmentDate(LocalDateTime.now());
                courseEnrollment.setStatus(Enrollment.EnrollmentStatus.ENROLLED);
                enrollmentRepo.save(courseEnrollment);
                enrolledCourseCount++;
                log.info("Created Enrollment: Student {} in Course {}", student.getEmail(), course.getCourseCode());
            }
        }

        section.incrementEnrolledStudents();
        sectionRepository.save(section);
        String courseInfo = sectionCourses.stream()
                .map(sc -> sc.getCourse().getCourseCode())
                .collect(Collectors.joining(", "));

        return mapToResponseDTO(saved,
                String.format("Successfully enrolled in section %s. Courses: %s",
                        section.getSectionCode(), courseInfo));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public StudentEnrollmentResponseDTO dropCourse(Long enrollmentId) {
        log.info("Dropping enrollment: {}", enrollmentId);

        StudentEnrollment enrollment = studentEnrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setStatus(StudentEnrollment.EnrollmentStatus.DROPPED);
        StudentEnrollment updated = studentEnrollmentRepo.save(enrollment);
        CourseSection section = enrollment.getSection();
        section.decrementEnrolledStudents();
        sectionRepository.save(section);

        return mapToResponseDTO(updated, "Successfully dropped from section");
    }

    @Override
    @Transactional(readOnly = true)
    public StudentEnrollmentResponseDTO getEnrollmentById(Long id) {
        StudentEnrollment enrollment = studentEnrollmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        return mapToResponseDTO(enrollment, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentEnrollmentResponseDTO> getStudentEnrollments(Long studentId) {
        return studentEnrollmentRepo.findByStudentId(studentId)
                .stream()
                .map(e -> mapToResponseDTO(e, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentEnrollmentResponseDTO> getStudentEnrollmentsBySemester(Long studentId, String semester, Integer academicYear) {
        return studentEnrollmentRepo.findStudentEnrollmentsBySemester(studentId, semester, academicYear)
                .stream()
                .map(e -> mapToResponseDTO(e, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public List<StudentEnrollmentResponseDTO> getSectionEnrollments(Long sectionId) {
        return studentEnrollmentRepo.findBySectionId(sectionId)
                .stream()
                .map(e -> mapToResponseDTO(e, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public List<StudentEnrollmentResponseDTO> getInstructorStudents(Long instructorId, String semester, Integer academicYear) {
        return studentEnrollmentRepo.findStudentsByInstructorAndSemester(instructorId, semester, academicYear)
                .stream()
                .map(e -> mapToResponseDTO(e, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long studentId, Long sectionId) {
        return studentEnrollmentRepo.existsByStudentIdAndSectionId(studentId, sectionId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getEnrollmentCount(Long sectionId) {
        return studentEnrollmentRepo.countActiveEnrollmentsBySection(sectionId);
    }

    private StudentEnrollmentResponseDTO mapToResponseDTO(StudentEnrollment enrollment, String message) {
        CourseSection section = enrollment.getSection();
        List<SectionCourse> sectionCourses = sectionCourseRepository.findBySectionId(section.getId());
        String courseInfo = sectionCourses.stream()
                .map(sc -> sc.getCourse().getCourseCode())
                .collect(Collectors.joining(", "));

        return StudentEnrollmentResponseDTO.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getFullName())
                .studentIdNumber(enrollment.getStudent().getStudentId())
                .sectionId(section.getId())
                .courseCode(courseInfo)
                .courseName("Multiple Courses")
                .sectionCode(section.getSectionCode())
                .instructorName(section.getInstructor() != null ? section.getInstructor().getFullName() : "TBA")
                .schedule(section.getSchedule())
                .room(section.getRoom())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus().name())
                .semester(section.getSemester())
                .academicYear(section.getAcademicYear())
                .message(message != null ? message : "Enrollment processed successfully")
                .build();
    }
}