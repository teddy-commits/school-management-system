package com.admas.management.modules.grading.service.impl;

import com.admas.management.modules.grading.dto.request.EnrollmentRequestDTO;
import com.admas.management.modules.grading.dto.response.EnrollmentResponseDTO;
import com.admas.management.modules.grading.mapper.EnrollmentMapper;
import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.model.entity.Enrollment;
import com.admas.management.modules.grading.model.enums.CourseStatus;
import com.admas.management.modules.grading.repository.CourseRepository;
import com.admas.management.modules.grading.repository.EnrollmentRepository;
import com.admas.management.modules.grading.service.EnrollmentService;
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
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public EnrollmentResponseDTO enrollStudent(EnrollmentRequestDTO requestDTO) {
        log.info("Enrolling student: {} in course: {}", requestDTO.getStudentId(), requestDTO.getCourseCode());

        User student = userRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findByCourseCode(requestDTO.getCourseCode())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (course.getStatus() != CourseStatus.OPEN) {
            throw new RuntimeException("Course is not open for registration");
        }
        if (enrollmentRepository.existsByStudentAndCourseAndStatus(student, course, Enrollment.EnrollmentStatus.ENROLLED)) {
            throw new RuntimeException("Student already enrolled in this course");
        }
        if (!course.hasAvailableSeats()) {
            throw new RuntimeException("No available seats in this course");
        }
        if (course.getPrerequisites() != null && !course.getPrerequisites().isEmpty()) {
            // TODO: Implement prerequisite checking logic
            log.info("Course has prerequisites: {}", course.getPrerequisites());
        }
        Enrollment enrollment = enrollmentMapper.toEntity(
                student, course, requestDTO.getSemester(), requestDTO.getAcademicYear()
        );

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        course.incrementEnrolledStudents();
        courseRepository.save(course);

        return enrollmentMapper.toResponseDTO(savedEnrollment, "Successfully enrolled in " + course.getCourseName());
    }

    @Override
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public EnrollmentResponseDTO withdrawFromCourse(Long enrollmentId) {
        log.info("Withdrawing from enrollment: {}", enrollmentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setStatus(Enrollment.EnrollmentStatus.WITHDRAWN);
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        Course course = enrollment.getCourse();
        course.decrementEnrolledStudents();
        courseRepository.save(course);

        return enrollmentMapper.toResponseDTO(updatedEnrollment, "Successfully withdrawn from course");
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getStudentEnrollments(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return enrollmentRepository.findByStudent(student)
                .stream()
                .map(enrollmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public List<EnrollmentResponseDTO> getCourseEnrollments(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return enrollmentRepository.findByCourse(course)
                .stream()
                .map(enrollmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getActiveEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findActiveEnrollmentsByStudent(studentId)
                .stream()
                .map(enrollmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getEnrollmentCountByCourse(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return enrollmentRepository.countActiveEnrollmentsByCourse(course.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long studentId, String courseCode) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return enrollmentRepository.existsByStudentAndCourseAndStatus(student, course, Enrollment.EnrollmentStatus.ENROLLED);
    }
}
