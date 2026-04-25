package com.admas.management.modules.grading.service;

import com.admas.management.modules.grading.dto.request.EnrollmentRequestDTO;
import com.admas.management.modules.grading.dto.response.EnrollmentResponseDTO;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponseDTO enrollStudent(EnrollmentRequestDTO requestDTO);
    EnrollmentResponseDTO withdrawFromCourse(Long enrollmentId);
    List<EnrollmentResponseDTO> getStudentEnrollments(Long studentId);
    List<EnrollmentResponseDTO> getCourseEnrollments(String courseCode);
    List<EnrollmentResponseDTO> getActiveEnrollmentsByStudent(Long studentId);
    Long getEnrollmentCountByCourse(String courseCode);
    boolean isStudentEnrolled(Long studentId, String courseCode);
}
