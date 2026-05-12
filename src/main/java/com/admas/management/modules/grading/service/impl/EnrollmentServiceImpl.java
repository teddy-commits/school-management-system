package com.admas.management.modules.grading.service.impl;

import com.admas.management.modules.grading.dto.request.EnrollmentRequestDTO;
import com.admas.management.modules.grading.dto.response.EnrollmentResponseDTO;
import com.admas.management.modules.grading.mapper.EnrollmentMapper;
import com.admas.management.modules.grading.model.entity.*;
import com.admas.management.modules.grading.model.enums.CourseStatus;
import com.admas.management.modules.grading.repository.*;
import com.admas.management.modules.grading.service.EnrollmentService;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
    private final CourseSectionRepository sectionRepository;
    private final SectionCourseRepository sectionCourseRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public void enrollStudentInSection(Long studentId, Long sectionId, String semester, Integer academicYear) {
        log.info("=== Enrolling student {} in section {} for {} {} ===", studentId, sectionId, semester, academicYear);

        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // ✅ CHECK: Is student already enrolled in ANY section for this semester/year?
        List<StudentEnrollment> existingEnrollments = studentEnrollmentRepository
                .findByStudentIdAndSection_SemesterAndSection_AcademicYear(studentId, semester, academicYear);

        if (!existingEnrollments.isEmpty()) {
            CourseSection existingSection = existingEnrollments.get(0).getSection();
            throw new RuntimeException(
                    String.format("Student is already enrolled in Section %s for %s %d. A student can only be in one section per semester.",
                            existingSection.getSectionCode(), semester, academicYear)
            );
        }

        // ✅ CHECK: Is student already enrolled in any course for this semester?
        long courseCount = enrollmentRepository
                .countByStudentIdAndSemesterAndAcademicYear(studentId, semester, academicYear);

        if (courseCount > 0) {
            throw new RuntimeException(
                    String.format("Student is already enrolled in %d course(s) for %s %d. Cannot enroll in another section.",
                            courseCount, semester, academicYear)
            );
        }

        // Check if section has available seats
        if (!section.hasAvailableSeats()) {
            throw new RuntimeException("No available seats in this section");
        }

        // Check if section is open
        if (section.getStatus() != CourseSection.SectionStatus.OPEN) {
            throw new RuntimeException("Section is not open for registration");
        }

        // ✅ STEP 1: Create StudentEnrollment record (section-level)
        StudentEnrollment studentEnrollment = new StudentEnrollment();
        studentEnrollment.setStudent(student);
        studentEnrollment.setSection(section);
        studentEnrollment.setEnrollmentDate(LocalDateTime.now());
        studentEnrollment.setStatus(StudentEnrollment.EnrollmentStatus.ENROLLED);
        studentEnrollmentRepository.save(studentEnrollment);
        log.info("Created StudentEnrollment for section {}", section.getSectionCode());

        // ✅ STEP 2: Get all courses in this section
        List<SectionCourse> sectionCourses = sectionCourseRepository.findBySectionId(sectionId);

        int enrolledCount = 0;

        // ✅ STEP 3: Create Enrollment records for each course (course-level)
        for (SectionCourse sc : sectionCourses) {
            Course course = sc.getCourse();

            boolean alreadyEnrolled = enrollmentRepository
                    .existsByStudentAndCourseAndSemesterAndAcademicYear(
                            student, course, semester, academicYear);

            if (!alreadyEnrolled) {
                Enrollment enrollment = new Enrollment();
                enrollment.setStudent(student);
                enrollment.setCourse(course);
                enrollment.setSemester(semester);
                enrollment.setAcademicYear(academicYear);
                enrollment.setEnrollmentDate(LocalDateTime.now());
                enrollment.setStatus(Enrollment.EnrollmentStatus.ENROLLED);

                enrollmentRepository.save(enrollment);
                enrolledCount++;
            }
        }

        // Update section count
        section.setEnrolledStudents(section.getEnrolledStudents() + 1);
        sectionRepository.save(section);

        log.info("Enrolled student {} in section {} with {} course enrollments",
                student.getEmail(), section.getSectionCode(), enrolledCount);
    }
    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getEnrollmentsByCourseAndSection(
            Long courseId, Long sectionId, String semester, Integer academicYear) {

        log.info("Getting enrollments for course {} in section {} for {} {}",
                courseId, sectionId, semester, academicYear);

        // Get all students enrolled in the course for this semester
        List<Enrollment> courseEnrollments = enrollmentRepository
                .findByCourseIdAndSemesterAndAcademicYear(courseId, semester, academicYear);

        // Get all students enrolled in the specific section
        List<StudentEnrollment> sectionEnrollments = studentEnrollmentRepository
                .findBySectionId(sectionId);

        // Get the set of student IDs that are in this section
        Set<Long> sectionStudentIds = sectionEnrollments.stream()
                .map(se -> se.getStudent().getId())
                .collect(Collectors.toSet());

        // Filter course enrollments to only include students in this section
        return courseEnrollments.stream()
                .filter(e -> sectionStudentIds.contains(e.getStudent().getId()))
                .map(enrollmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getEnrollmentsByCourseAndSemester(Long courseId, String semester, Integer academicYear) {
        log.info("Getting enrollments for course {} in {} {}", courseId, semester, academicYear);

        return enrollmentRepository.findByCourseIdAndSemesterAndAcademicYear(courseId, semester, academicYear)
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
