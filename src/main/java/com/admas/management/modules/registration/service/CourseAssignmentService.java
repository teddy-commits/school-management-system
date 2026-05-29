package com.admas.management.modules.registration.service;

import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.repository.CourseRepository;
import com.admas.management.modules.registration.dto.response.AssignmentResultDTO;
import com.admas.management.modules.registration.dto.response.StudentPreviewDTO;
import com.admas.management.modules.registration.model.CourseEnrollment;
import com.admas.management.modules.registration.model.SemesterRegistration;
import com.admas.management.modules.registration.repository.CourseEnrollmentRepository;
import com.admas.management.modules.registration.repository.SemesterRegistrationRepository;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseAssignmentService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SemesterRegistrationRepository semesterRegistrationRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    private static final double FEE_PER_CREDIT = 1500.0;

    /**
     * Assign courses to all students in a specific department and year level
     */
    @Transactional
    public AssignmentResultDTO assignCoursesToDepartmentYear(
            Long departmentId,
            Integer academicYearLevel,
            String semester,
            Integer academicYear,
            List<Long> courseIds) {

        log.info("Assigning courses {} to department: {}, year level: {}, semester: {}",
                courseIds, departmentId, academicYearLevel, semester);

        // Get all students in the department with the specified year level
        List<User> students = userRepository.findByDepartmentIdAndAcademicYearLevel(departmentId, academicYearLevel);

        if (students.isEmpty()) {
            throw new RuntimeException("No students found for department ID: " + departmentId + " and year level: " + academicYearLevel);
        }

        // Get courses
        List<Course> courses = courseRepository.findAllById(courseIds);
        if (courses.isEmpty()) {
            throw new RuntimeException("No courses found with the provided IDs");
        }

        int totalAssignments = 0;
        List<String> errors = new ArrayList<>();

        for (User student : students) {
            try {
                // Get or create semester registration
                SemesterRegistration registration = semesterRegistrationRepository
                        .findByStudentAndSemesterAndAcademicYear(student, semester, academicYear)
                        .orElseGet(() -> createSemesterRegistration(student, semester, academicYear));

                // Assign each course to the student
                for (Course course : courses) {
                    // Check if already enrolled
                    boolean alreadyEnrolled = courseEnrollmentRepository.existsByStudentAndCourseAndSemesterAndAcademicYear(
                            student.getId(), course.getId(), semester, academicYear);

                    if (!alreadyEnrolled) {
                        CourseEnrollment enrollment = new CourseEnrollment();
                        enrollment.setSemesterRegistration(registration);
                        enrollment.setCourse(course);
                        enrollment.setCredits(Double.valueOf(course.getCredits()));
                        enrollment.setFeePerCredit(FEE_PER_CREDIT);
                        enrollment.setTotalFee(course.getCredits() * FEE_PER_CREDIT);
                        enrollment.setStatus(CourseEnrollment.EnrollmentStatus.ENROLLED);
                        enrollment.setEnrollmentDate(LocalDateTime.now());

                        courseEnrollmentRepository.save(enrollment);
                        totalAssignments++;

                        // Update course enrollment count
                        course.setEnrolledStudents(course.getEnrolledStudents() + 1);
                        courseRepository.save(course);
                    }
                }

                // Update registration totals
                registration.calculateTotals();
                semesterRegistrationRepository.save(registration);

            } catch (Exception e) {
                log.error("Error assigning courses to student: {}", student.getEmail(), e);
                errors.add("Failed for student: " + student.getEmail() + " - " + e.getMessage());
            }
        }

        return AssignmentResultDTO.builder()
                .totalStudents(students.size())
                .totalAssignments(totalAssignments)
                .errors(errors)
                .build();
    }

    /**
     * Get students by department and year level (for preview)
     */
    public List<StudentPreviewDTO> getStudentsByDepartmentAndYear(Long departmentId, Integer academicYearLevel) {
        List<User> students = userRepository.findByDepartmentIdAndAcademicYearLevel(departmentId, academicYearLevel);

        return students.stream()
                .map(student -> StudentPreviewDTO.builder()
                        .id(student.getId())
                        .studentId(student.getStudentId())
                        .fullName(student.getFullName())
                        .email(student.getEmail())
                        .department(student.getDepartmentName())
                        .academicYearLevel(student.getAcademicYearLevel())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get courses already assigned to students in a department/year/semester
     */
    public List<Course> getAssignedCoursesForDepartmentYear(
            Long departmentId,
            Integer academicYearLevel,
            String semester,
            Integer academicYear) {

        List<User> students = userRepository.findByDepartmentIdAndAcademicYearLevel(departmentId, academicYearLevel);

        if (students.isEmpty()) {
            return new ArrayList<>();
        }

        // Get enrollments for the first student (assuming all students in same department/year have same assigned courses)
        User sampleStudent = students.get(0);
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByStudentAndSemesterAndAcademicYear(
                sampleStudent.getId(), semester, academicYear);

        return enrollments.stream()
                .map(CourseEnrollment::getCourse)
                .collect(Collectors.toList());
    }

    private SemesterRegistration createSemesterRegistration(User student, String semester, Integer academicYear) {
        SemesterRegistration registration = new SemesterRegistration();
        registration.setStudent(student);
        registration.setSemester(semester);
        registration.setAcademicYear(academicYear);
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setStatus(SemesterRegistration.RegistrationStatus.PENDING);
        registration.setFeesPaid(0.0);
        registration.setFeesDue(0.0);
        registration.setTotalCredits(0.0);
        registration.setTotalFees(0.0);
        return semesterRegistrationRepository.save(registration);
    }
}
