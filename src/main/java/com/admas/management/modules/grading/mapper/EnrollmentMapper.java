package com.admas.management.modules.grading.mapper;

import com.admas.management.modules.grading.dto.response.EnrollmentResponseDTO;
import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.model.entity.Enrollment;
import com.admas.management.modules.shared.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EnrollmentMapper {

    public Enrollment toEntity(User student, Course course, String semester, Integer academicYear) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus(Enrollment.EnrollmentStatus.ENROLLED);
        enrollment.setSemester(semester != null ? semester : course.getSemester().name());
        enrollment.setAcademicYear(academicYear != null ? academicYear : course.getAcademicYear());
        enrollment.setCreatedAt(LocalDateTime.now());
        return enrollment;
    }

    public EnrollmentResponseDTO toResponseDTO(Enrollment enrollment) {
        return EnrollmentResponseDTO.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getFullName())
                .studentIdNumber(enrollment.getStudent().getStudentId())
                .courseId(enrollment.getCourse().getId())
                .courseCode(enrollment.getCourse().getCourseCode())
                .courseName(enrollment.getCourse().getCourseName())
                .credits(enrollment.getCourse().getCredits())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .semester(enrollment.getSemester())
                .academicYear(enrollment.getAcademicYear())
                .createdAt(enrollment.getCreatedAt())
                .message("Enrollment successful")
                .build();
    }

    public EnrollmentResponseDTO toResponseDTO(Enrollment enrollment, String message) {
        EnrollmentResponseDTO dto = toResponseDTO(enrollment);
        dto.setMessage(message);
        return dto;
    }
}
