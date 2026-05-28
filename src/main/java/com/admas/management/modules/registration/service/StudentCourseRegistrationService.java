package com.admas.management.modules.registration.service;

import com.admas.management.modules.grading.dto.request.CourseRegistrationRequestDTO;
import com.admas.management.modules.grading.dto.response.StudentAvailableCourseDTO;
import com.admas.management.modules.grading.dto.response.RegisteredCourseDTO;
import com.admas.management.modules.registration.dto.response.RegistrationSummaryDTO;

import java.util.List;

public interface StudentCourseRegistrationService {

    /**
     * Get available courses for a student based on their department and academic year level
     */
    List<StudentAvailableCourseDTO> getAvailableCoursesForStudent(Long studentId, String semester, Integer academicYear);

    /**
     * Register a student for a course
     */
    RegisteredCourseDTO registerCourse(CourseRegistrationRequestDTO request);

    /**
     * Get student's registered courses for a semester
     */
    List<RegisteredCourseDTO> getStudentRegisteredCourses(Long studentId, String semester, Integer academicYear);

    /**
     * Drop a course
     */
    void dropCourse(Long studentId, Long courseId, String semester, Integer academicYear, String reason);

    /**
     * Get registration summary for a student
     */
    RegistrationSummaryDTO getRegistrationSummary(Long studentId, String semester, Integer academicYear);
}
