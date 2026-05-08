package com.admas.management.modules.registration.service.impl;

import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.repository.CourseRepository;
import com.admas.management.modules.registration.dto.request.SemesterRegistrationRequestDTO;
import com.admas.management.modules.registration.dto.response.SemesterRegistrationResponseDTO;
import com.admas.management.modules.registration.model.CourseEnrollment;
import com.admas.management.modules.registration.model.SemesterRegistration;
import com.admas.management.modules.registration.repository.CourseEnrollmentRepository;
import com.admas.management.modules.registration.repository.SemesterRegistrationRepository;
import com.admas.management.modules.registration.service.SemesterRegistrationService;
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
public class SemesterRegistrationServiceImpl implements SemesterRegistrationService {

    private final SemesterRegistrationRepository registrationRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    private static final double FEE_PER_CREDIT = 1500.0; // $1500 per credit hour

    @Override
    public SemesterRegistrationResponseDTO initiateRegistration(SemesterRegistrationRequestDTO request) {
        log.info("Initiating semester registration for student: {}", request.getStudentId());

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Check if already registered for this semester
        if (registrationRepository.existsByStudentAndSemesterAndAcademicYear(
                student, request.getSemester(), request.getAcademicYear())) {
            throw new RuntimeException("Student already registered for this semester");
        }

        SemesterRegistration registration = new SemesterRegistration();
        registration.setStudent(student);
        registration.setSemester(request.getSemester());
        registration.setAcademicYear(request.getAcademicYear());
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setStatus(SemesterRegistration.RegistrationStatus.PENDING);

        // Add selected courses
        for (Long courseId : request.getCourseIds()) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));

            CourseEnrollment enrollment = new CourseEnrollment();
            enrollment.setSemesterRegistration(registration);
            enrollment.setCourse(course);
            enrollment.setCredits(Double.valueOf(course.getCredits()));
            enrollment.setFeePerCredit(FEE_PER_CREDIT);
            enrollment.calculateFee();
            enrollment.setEnrollmentDate(LocalDateTime.now());
            enrollment.setStatus(CourseEnrollment.EnrollmentStatus.ENROLLED);

            registration.getCourseEnrollments().add(enrollment);
        }

        registration.calculateTotals();
        SemesterRegistration saved = registrationRepository.save(registration);

        return mapToResponseDTO(saved);
    }

    @Override
    public SemesterRegistrationResponseDTO addCourses(Long registrationId, List<Long> courseIds) {
        SemesterRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (registration.getStatus() != SemesterRegistration.RegistrationStatus.PENDING) {
            throw new RuntimeException("Cannot add courses. Registration is already " + registration.getStatus());
        }

        for (Long courseId : courseIds) {
            // Check if already enrolled
            boolean alreadyEnrolled = registration.getCourseEnrollments().stream()
                    .anyMatch(ce -> ce.getCourse().getId().equals(courseId));

            if (!alreadyEnrolled) {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));

                CourseEnrollment enrollment = new CourseEnrollment();
                enrollment.setSemesterRegistration(registration);
                enrollment.setCourse(course);
                enrollment.setCredits(Double.valueOf(course.getCredits()));
                enrollment.setFeePerCredit(FEE_PER_CREDIT);
                enrollment.calculateFee();
                enrollment.setEnrollmentDate(LocalDateTime.now());
                enrollment.setStatus(CourseEnrollment.EnrollmentStatus.ENROLLED);

                registration.getCourseEnrollments().add(enrollment);
            }
        }

        registration.calculateTotals();
        SemesterRegistration updated = registrationRepository.save(registration);

        return mapToResponseDTO(updated);
    }

    @Override
    public SemesterRegistrationResponseDTO removeCourse(Long registrationId, Long courseId) {
        SemesterRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (registration.getStatus() != SemesterRegistration.RegistrationStatus.PENDING) {
            throw new RuntimeException("Cannot remove courses. Registration is already " + registration.getStatus());
        }

        registration.getCourseEnrollments().removeIf(ce -> ce.getCourse().getId().equals(courseId));
        registration.calculateTotals();

        SemesterRegistration updated = registrationRepository.save(registration);
        return mapToResponseDTO(updated);
    }

    @Override
    public SemesterRegistrationResponseDTO completeRegistration(Long registrationId) {
        SemesterRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (registration.getCourseEnrollments().isEmpty()) {
            throw new RuntimeException("Cannot complete registration. No courses selected.");
        }

        registration.setStatus(SemesterRegistration.RegistrationStatus.COMPLETED);
        SemesterRegistration updated = registrationRepository.save(registration);

        return mapToResponseDTO(updated);
    }

    @Override
    public SemesterRegistrationResponseDTO processPayment(Long registrationId, String paymentReference, Double amount) {
        SemesterRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        registration.setFeesPaid(amount);
        registration.setFeesDue(registration.getTotalFees() - amount);
        registration.setPaymentReference(paymentReference);
        registration.setPaymentDate(LocalDateTime.now());

        if (registration.getFeesDue() <= 0) {
            registration.setStatus(SemesterRegistration.RegistrationStatus.PAID);
        }

        SemesterRegistration updated = registrationRepository.save(registration);
        return mapToResponseDTO(updated);
    }

    @Override
    public List<SemesterRegistrationResponseDTO> getStudentRegistrations(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return registrationRepository.findByStudent(student)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SemesterRegistrationResponseDTO getCurrentSemesterRegistration(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Get current semester (you can determine based on current date)
        String currentSemester = getCurrentSemester();
        Integer currentYear = getCurrentAcademicYear();

        return registrationRepository.findByStudentAndSemesterAndAcademicYear(
                        student, currentSemester, currentYear)
                .map(this::mapToResponseDTO)
                .orElse(null);
    }

    @Override
    public SemesterRegistrationResponseDTO getRegistrationById(Long registrationId) {
        SemesterRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        return mapToResponseDTO(registration);
    }

    @Override
    public boolean canRegisterForSemester(Long studentId, String semester, Integer academicYear) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return !registrationRepository.existsByStudentAndSemesterAndAcademicYear(student, semester, academicYear);
    }

    @Override
    public List<SemesterRegistrationResponseDTO> getRegistrationsByStatus(String status) {
        SemesterRegistration.RegistrationStatus regStatus =
                SemesterRegistration.RegistrationStatus.valueOf(status);

        return registrationRepository.findAll()
                .stream()
                .filter(r -> r.getStatus() == regStatus)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private String getCurrentSemester() {
        int month = LocalDateTime.now().getMonthValue();
        if (month >= 1 && month <= 4) return "SPRING";
        if (month >= 5 && month <= 8) return "SUMMER";
        return "FALL";
    }

    private Integer getCurrentAcademicYear() {
        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();
        // If month is fall or winter, it's current year, else next year
        return year;
    }

    private SemesterRegistrationResponseDTO mapToResponseDTO(SemesterRegistration registration) {
        List<SemesterRegistrationResponseDTO.CourseEnrollmentDTO> courseDTOs =
                registration.getCourseEnrollments().stream()
                        .map(ce -> SemesterRegistrationResponseDTO.CourseEnrollmentDTO.builder()
                                .courseId(ce.getCourse().getId())
                                .courseCode(ce.getCourse().getCourseCode())
                                .courseName(ce.getCourse().getCourseName())
                                .credits(ce.getCredits())
                                .fee(ce.getTotalFee())
                                .status(ce.getStatus().name())
                                .build())
                        .collect(Collectors.toList());

        return SemesterRegistrationResponseDTO.builder()
                .id(registration.getId())
                .studentId(registration.getStudent().getId())
                .studentName(registration.getStudent().getFullName())
                .studentIdNumber(registration.getStudent().getStudentId())
                .semester(registration.getSemester())
                .academicYear(registration.getAcademicYear())
                .registrationDate(registration.getRegistrationDate())
                .status(registration.getStatus().name())
                .totalCredits(registration.getTotalCredits())
                .totalFees(registration.getTotalFees())
                .feesPaid(registration.getFeesPaid())
                .feesDue(registration.getFeesDue())
                .paymentReference(registration.getPaymentReference())
                .courses(courseDTOs)
                .message("Semester registration processed successfully")
                .build();
    }
}
