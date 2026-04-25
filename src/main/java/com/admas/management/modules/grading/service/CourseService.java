package com.admas.management.modules.grading.service;



import com.admas.management.modules.grading.dto.request.CourseRequestDTO;
import com.admas.management.modules.grading.dto.response.CourseResponseDTO;
import com.admas.management.modules.grading.model.enums.Semester;

import java.util.List;

public interface CourseService {
    CourseResponseDTO createCourse(CourseRequestDTO requestDTO);
    CourseResponseDTO updateCourse(Long id, CourseRequestDTO requestDTO);
    CourseResponseDTO getCourseById(Long id);
    CourseResponseDTO getCourseByCode(String courseCode);
    List<CourseResponseDTO> getAllCourses();
    List<CourseResponseDTO> getCoursesByDepartment(String department);
    List<CourseResponseDTO> getCoursesByFaculty(String faculty);
    List<CourseResponseDTO> getCoursesBySemester(Semester semester, Integer academicYear);
    List<CourseResponseDTO> getCoursesByInstructor(String instructorEmail);
    List<CourseResponseDTO> searchCourses(String keyword);
    void deleteCourse(Long id);
    void updateCourseStatus(Long id, String status);
}
