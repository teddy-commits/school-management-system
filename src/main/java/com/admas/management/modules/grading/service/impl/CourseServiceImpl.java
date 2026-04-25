package com.admas.management.modules.grading.service.impl;

import com.admas.management.modules.grading.dto.request.CourseRequestDTO;
import com.admas.management.modules.grading.dto.response.CourseResponseDTO;
import com.admas.management.modules.grading.mapper.CourseMapper;
import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.model.enums.CourseStatus;
import com.admas.management.modules.grading.model.enums.Semester;
import com.admas.management.modules.grading.repository.CourseRepository;
import com.admas.management.modules.grading.service.CourseService;
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
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public CourseResponseDTO createCourse(CourseRequestDTO requestDTO) {
        log.info("Creating course: {}", requestDTO.getCourseCode());

        if (courseRepository.existsByCourseCode(requestDTO.getCourseCode())) {
            throw new RuntimeException("Course with code " + requestDTO.getCourseCode() + " already exists");
        }

        Course course = courseMapper.toEntity(requestDTO);
        Course savedCourse = courseRepository.save(course);

        return courseMapper.toResponseDTO(savedCourse);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO requestDTO) {
        log.info("Updating course with id: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        courseMapper.updateEntityFromDTO(course, requestDTO);
        Course updatedCourse = courseRepository.save(course);

        return courseMapper.toResponseDTO(updatedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        return courseMapper.toResponseDTO(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseByCode(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found with code: " + courseCode));
        return courseMapper.toResponseDTO(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getCoursesByDepartment(String department) {
        return courseRepository.findByDepartment(department)
                .stream()
                .map(courseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getCoursesByFaculty(String faculty) {
        return courseRepository.findByFaculty(faculty)
                .stream()
                .map(courseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getCoursesBySemester(Semester semester, Integer academicYear) {
        return courseRepository.findBySemesterAndAcademicYear(semester, academicYear)
                .stream()
                .map(courseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getCoursesByInstructor(String instructorEmail) {
        return courseRepository.findByInstructorEmail(instructorEmail)
                .stream()
                .map(courseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> searchCourses(String keyword) {
        return courseRepository.searchCourses(keyword)
                .stream()
                .map(courseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public void deleteCourse(Long id) {
        log.info("Deleting course with id: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        courseRepository.delete(course);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public void updateCourseStatus(Long id, String status) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setStatus(CourseStatus.valueOf(status));
        courseRepository.save(course);
        log.info("Course {} status updated to {}", course.getCourseCode(), status);
    }
}
