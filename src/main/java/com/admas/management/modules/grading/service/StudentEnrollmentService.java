package com.admas.management.modules.grading.service;

import com.admas.management.modules.grading.dto.request.StudentEnrollmentRequestDTO;
import com.admas.management.modules.grading.dto.response.StudentEnrollmentResponseDTO;

import java.util.List;

public interface StudentEnrollmentService {

    StudentEnrollmentResponseDTO enrollStudent(StudentEnrollmentRequestDTO request);

    StudentEnrollmentResponseDTO dropCourse(Long enrollmentId);

    StudentEnrollmentResponseDTO getEnrollmentById(Long id);

    List<StudentEnrollmentResponseDTO> getStudentEnrollments(Long studentId);

    List<StudentEnrollmentResponseDTO> getStudentEnrollmentsBySemester(Long studentId, String semester, Integer academicYear);

    List<StudentEnrollmentResponseDTO> getSectionEnrollments(Long sectionId);

    List<StudentEnrollmentResponseDTO> getInstructorStudents(Long instructorId, String semester, Integer academicYear);

    boolean isStudentEnrolled(Long studentId, Long sectionId);

    long getEnrollmentCount(Long sectionId);
}