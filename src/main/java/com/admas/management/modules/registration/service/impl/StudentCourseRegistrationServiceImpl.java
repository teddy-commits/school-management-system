package com.admas.management.modules.registration.service.impl;


import com.admas.management.modules.grading.dto.request.CourseRegistrationRequestDTO;
import com.admas.management.modules.grading.dto.response.StudentAvailableCourseDTO;
import com.admas.management.modules.grading.dto.response.RegisteredCourseDTO;
import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.model.enums.Semester;
import com.admas.management.modules.grading.repository.CourseRepository;
import com.admas.management.modules.registration.dto.response.RegistrationSummaryDTO;
import com.admas.management.modules.registration.model.CourseEnrollment;
import com.admas.management.modules.registration.model.SemesterRegistration;
import com.admas.management.modules.registration.repository.CourseEnrollmentRepository;
import com.admas.management.modules.registration.repository.SemesterRegistrationRepository;
import com.admas.management.modules.registration.service.StudentCourseRegistrationService;
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
public class StudentCourseRegistrationServiceImpl implements StudentCourseRegistrationService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SemesterRegistrationRepository semesterRegistrationRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    private static final int MAX_CREDITS_PER_SEMESTER = 18;
    private static final int MAX_COURSES_PER_SEMESTER = 6;
    private static final double FEE_PER_CREDIT = 1500.0; // ETB per credit

    @Override
    public List<StudentAvailableCourseDTO> getAvailableCoursesForStudent(Long studentId, String semester, Integer academicYear) {
        log.info("Getting available courses for student: {}, semester: {}, academicYear: {}", studentId, semester, academicYear);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        if (!student.isStudent()) {
            throw new RuntimeException("User with ID: " + studentId + " is not a student");
        }

        Long departmentId = student.getDepartment() != null ? student.getDepartment().getId() : null;
        Integer studentYearLevel = student.getAcademicYearLevel();
        String studentFaculty = student.getFaculty();

        List<Course> availableCourses = new ArrayList<>();

        // Get courses based on department
        if (departmentId != null) {
            List<Course> departmentCourses = courseRepository.findCoursesByDepartmentAndSemesterAndYear(
                    departmentId, Semester.valueOf(semester), academicYear);
            availableCourses.addAll(departmentCourses);
            log.info("Found {} courses in department: {}", departmentCourses.size(), student.getDepartment().getName());
        }

        // Also get courses from same faculty if needed
        if (studentFaculty != null && !studentFaculty.isEmpty()) {
            List<Course> facultyCourses = courseRepository.findCoursesByFacultyAndSemesterAndYear(
                    studentFaculty, Semester.valueOf(semester), academicYear);
            for (Course course : facultyCourses) {
                if (!availableCourses.contains(course)) {
                    availableCourses.add(course);
                }
            }
            log.info("Added {} courses from faculty: {}", facultyCourses.size(), studentFaculty);
        }

        // Get already registered course IDs for this semester
        List<Long> registeredCourseIds = courseEnrollmentRepository.findRegisteredCourseIdsByStudentAndSemester(
                studentId, semester, academicYear);

        log.info("Student already registered for {} courses this semester", registeredCourseIds.size());

        // Convert to DTO and check eligibility
        return availableCourses.stream()
                .map(course -> toAvailableCourseDTO(course, student, registeredCourseIds))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RegisteredCourseDTO registerCourse(CourseRegistrationRequestDTO request) {
        log.info("Registering student {} for course {} in semester {} {}",
                request.getStudentId(), request.getCourseId(), request.getSemester(), request.getAcademicYear());

        // Validate student
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + request.getStudentId()));

        if (!student.isStudent()) {
            throw new RuntimeException("User is not a student");
        }

        // Validate course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + request.getCourseId()));

        // Check if course is for student's department
        Long studentDeptId = student.getDepartment() != null ? student.getDepartment().getId() : null;
        Long courseDeptId = course.getDepartmentId();

        if (courseDeptId != null && studentDeptId != null && !courseDeptId.equals(studentDeptId)) {
            // Check if course is from same faculty
            if (course.getFaculty() != null && student.getFaculty() != null &&
                    !course.getFaculty().equals(student.getFaculty())) {
                throw new RuntimeException("You can only register for courses in your department or faculty");
            }
        }

        // Check if already registered for this course in same semester
        boolean alreadyRegistered = courseEnrollmentRepository.existsByStudentAndCourseAndSemesterAndAcademicYear(
                request.getStudentId(), request.getCourseId(), request.getSemester(), request.getAcademicYear());

        if (alreadyRegistered) {
            throw new RuntimeException("You are already registered for course: " + course.getCourseCode());
        }

        // Get or create semester registration
        SemesterRegistration semesterReg = semesterRegistrationRepository
                .findByStudentIdAndSemesterAndAcademicYear(request.getStudentId(), request.getSemester(), request.getAcademicYear())
                .orElseGet(() -> createSemesterRegistration(student, request.getSemester(), request.getAcademicYear()));

        // Check credit limit
        Double currentCredits = semesterRegistrationRepository.getTotalCreditsForSemester(
                request.getStudentId(), request.getSemester(), request.getAcademicYear());

        if (currentCredits == null) currentCredits = 0.0;

        if (currentCredits + course.getCredits() > MAX_CREDITS_PER_SEMESTER) {
            throw new RuntimeException(String.format(
                    "Maximum credits per semester is %d. You have %.1f credits, trying to add %d credits (Total would be %.1f)",
                    MAX_CREDITS_PER_SEMESTER, currentCredits, course.getCredits(), currentCredits + course.getCredits()));
        }

        // Check course capacity
        if (course.getEnrolledStudents() >= course.getMaxStudents()) {
            throw new RuntimeException("Course " + course.getCourseCode() + " is full. Maximum capacity: " + course.getMaxStudents());
        }

        // Create enrollment
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setSemesterRegistration(semesterReg);
        enrollment.setCourse(course);
        enrollment.setCredits(Double.valueOf(course.getCredits()));
        enrollment.setFeePerCredit(FEE_PER_CREDIT);
        enrollment.setTotalFee(course.getCredits() * FEE_PER_CREDIT);
        enrollment.setStatus(CourseEnrollment.EnrollmentStatus.ENROLLED);
        enrollment.setEnrollmentDate(LocalDateTime.now());

        courseEnrollmentRepository.save(enrollment);

        // Update course enrollment count
        course.setEnrolledStudents(course.getEnrolledStudents() + 1);
        courseRepository.save(course);

        // Update semester registration totals
        semesterReg.calculateTotals();

        // Update payment if reference provided
        if (request.getPaymentReference() != null && !request.getPaymentReference().isEmpty()) {
            semesterReg.setPaymentReference(request.getPaymentReference());
            semesterReg.setPaymentDate(LocalDateTime.now());
            semesterReg.setStatus(SemesterRegistration.RegistrationStatus.PAID);
        }

        semesterRegistrationRepository.save(semesterReg);

        log.info("Student {} successfully registered for course {}", student.getEmail(), course.getCourseCode());

        return toRegisteredCourseDTO(enrollment, course, student);
    }

    @Override
    public List<RegisteredCourseDTO> getStudentRegisteredCourses(Long studentId, String semester, Integer academicYear) {
        log.info("Getting registered courses for student: {}, semester: {}, academicYear: {}", studentId, semester, academicYear);

        List<CourseEnrollment> enrollments = courseEnrollmentRepository
                .findByStudentAndSemesterAndAcademicYear(studentId, semester, academicYear);

        return enrollments.stream()
                .map(enrollment -> toRegisteredCourseDTO(enrollment, enrollment.getCourse(), null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void dropCourse(Long studentId, Long courseId, String semester, Integer academicYear, String reason) {
        log.info("Dropping course {} for student {} in semester {} {}", courseId, studentId, semester, academicYear);

        CourseEnrollment enrollment = courseEnrollmentRepository
                .findByStudentAndCourseAndSemesterAndAcademicYear(studentId, courseId, semester, academicYear)
                .orElseThrow(() -> new RuntimeException("Enrollment not found for student and course"));

        enrollment.setStatus(CourseEnrollment.EnrollmentStatus.DROPPED);
        enrollment.setDropDate(LocalDateTime.now());
        enrollment.setDropReason(reason);
        courseEnrollmentRepository.save(enrollment);

        // Update course enrollment count
        Course course = enrollment.getCourse();
        course.setEnrolledStudents(course.getEnrolledStudents() - 1);
        courseRepository.save(course);

        // Update semester registration totals
        SemesterRegistration semesterReg = enrollment.getSemesterRegistration();
        semesterReg.calculateTotals();
        semesterRegistrationRepository.save(semesterReg);

        log.info("Student dropped course: {}", course.getCourseCode());
    }

    @Override
    public RegistrationSummaryDTO getRegistrationSummary(Long studentId, String semester, Integer academicYear) {
        log.info("Getting registration summary for student: {}, semester: {}, academicYear: {}", studentId, semester, academicYear);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        SemesterRegistration semesterReg = semesterRegistrationRepository
                .findByStudentIdAndSemesterAndAcademicYear(studentId, semester, academicYear)
                .orElse(null);

        if (semesterReg == null) {
            return RegistrationSummaryDTO.builder()
                    .studentId(studentId)
                    .studentName(student.getFullName())
                    .studentEmail(student.getEmail())
                    .department(student.getDepartmentName())
                    .academicYearLevel(student.getAcademicYearLevel())
                    .semester(semester)
                    .academicYear(academicYear)
                    .totalCredits(0.0)
                    .totalFees(0.0)
                    .feesPaid(0.0)
                    .feesDue(0.0)
                    .totalCourses(0)
                    .registrationStatus("NOT_STARTED")
                    .build();
        }

        int totalCourses = semesterReg.getCourseEnrollments() != null ?
                (int) semesterReg.getCourseEnrollments().stream()
                      .filter(ce -> ce.getStatus() == CourseEnrollment.EnrollmentStatus.ENROLLED)
                      .count() : 0;

        return RegistrationSummaryDTO.builder()
                .studentId(studentId)
                .studentName(student.getFullName())
                .studentEmail(student.getEmail())
                .department(student.getDepartmentName())
                .academicYearLevel(student.getAcademicYearLevel())
                .semester(semester)
                .academicYear(academicYear)
                .totalCredits(semesterReg.getTotalCredits())
                .totalFees(semesterReg.getTotalFees())
                .feesPaid(semesterReg.getFeesPaid())
                .feesDue(semesterReg.getFeesDue())
                .totalCourses(totalCourses)
                .registrationStatus(semesterReg.getStatus().toString())
                .registrationDate(semesterReg.getRegistrationDate())
                .build();
    }

    private SemesterRegistration createSemesterRegistration(User student, String semester, Integer academicYear) {
        log.info("Creating new semester registration for student: {}, semester: {}, academicYear: {}",
                student.getEmail(), semester, academicYear);

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

    private StudentAvailableCourseDTO toAvailableCourseDTO(Course course, User student, List<Long> registeredCourseIds) {
        boolean isRegistered = registeredCourseIds.contains(course.getId());
        boolean isFull = course.getEnrolledStudents() >= course.getMaxStudents();
        boolean isEligible = !isRegistered && !isFull;
        String eligibilityMessage = getEligibilityMessage(isRegistered, isFull, course);

        return StudentAvailableCourseDTO.builder()
                .courseId(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .description(course.getDescription())
                .credits(course.getCredits())
                .department(course.getDepartmentName())
                .faculty(course.getFaculty())
                .semester(course.getSemester() != null ? course.getSemester().toString() : null)
                .academicYear(course.getAcademicYear())
                .academicYearLevel(getCourseYearLevel(course))
                .instructorName(course.getInstructorName())
                .instructorEmail(course.getInstructorEmail())
                .maxStudents(course.getMaxStudents())
                .enrolledStudents(course.getEnrolledStudents())
                .availableSeats(course.getMaxStudents() - course.getEnrolledStudents())
                .prerequisites(course.getPrerequisites())
                .schedule(course.getSchedule())
                .room(course.getRoom())
                .isEligible(isEligible)
                .eligibilityMessage(eligibilityMessage)
                .build();
    }

    private String getEligibilityMessage(boolean isRegistered, boolean isFull, Course course) {
        if (isRegistered) {
            return "Already registered for this course";
        }
        if (isFull) {
            return "Course is full (Maximum " + course.getMaxStudents() + " students)";
        }
        return "Available for registration";
    }

    private RegisteredCourseDTO toRegisteredCourseDTO(CourseEnrollment enrollment, Course course, User student) {
        return RegisteredCourseDTO.builder()
                .registrationId(enrollment.getId())
                .courseId(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .credits(course.getCredits())
                .schedule(course.getSchedule())
                .room(course.getRoom())
                .instructorName(course.getInstructorName())
                .status(enrollment.getStatus().toString())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .fee(enrollment.getTotalFee())
                .build();
    }

    private Integer getCourseYearLevel(Course course) {
        // You can determine year level from course code pattern
        // Example: CS101 -> Year 1, CS301 -> Year 3
        String courseCode = course.getCourseCode();
        if (courseCode != null && courseCode.length() >= 3) {
            try {
                // Try to extract year level from the course code number
                String numberPart = courseCode.replaceAll("[^0-9]", "");
                if (!numberPart.isEmpty()) {
                    int firstDigit = Character.getNumericValue(numberPart.charAt(0));
                    if (firstDigit >= 1 && firstDigit <= 5) {
                        return firstDigit;
                    }
                }
            } catch (Exception e) {
                log.warn("Could not extract year level from course code: {}", courseCode);
            }
        }
        return null; // No year restriction
    }
}
