package com.admas.management.modules.grading.mapper;

import com.admas.management.modules.department.model.Department;
import com.admas.management.modules.department.repository.DepartmentRepository;
import com.admas.management.modules.grading.dto.request.CourseRequestDTO;
import com.admas.management.modules.grading.dto.response.CourseResponseDTO;
import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.model.enums.CourseStatus;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseMapper {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public Course toEntity(CourseRequestDTO dto) {
        Course course = new Course();
        course.setCourseCode(dto.getCourseCode());
        course.setCourseName(dto.getCourseName());
        course.setDescription(dto.getDescription());
        course.setCredits(dto.getCredits());
        if (dto.getDepartment() != null && !dto.getDepartment().isEmpty()) {
            Department department = departmentRepository.findByName(dto.getDepartment())
                    .orElseThrow(() -> new RuntimeException("Department not found: " + dto.getDepartment()));
            course.setDepartment(department);
        }

        course.setFaculty(dto.getFaculty());
        course.setSemester(dto.getSemester());
        course.setAcademicYear(dto.getAcademicYear());
        course.setStatus(dto.getStatus() != null ? dto.getStatus() : CourseStatus.DRAFT);
        course.setInstructorEmail(dto.getInstructorEmail());
        course.setMaxStudents(dto.getMaxStudents() != null ? dto.getMaxStudents() : 50);
        course.setEnrolledStudents(0);
        course.setPrerequisites(dto.getPrerequisites());
        course.setSyllabus(dto.getSyllabus());
        course.setRoom(dto.getRoom());
        course.setSchedule(dto.getSchedule());
        if (dto.getInstructorEmail() != null) {
            userRepository.findByEmail(dto.getInstructorEmail()).ifPresent(instructor ->
                    course.setInstructorName(instructor.getFullName())
            );
        }

        return course;
    }

    public CourseResponseDTO toResponseDTO(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .description(course.getDescription())
                .credits(course.getCredits())
                .department(course.getDepartment() != null ? course.getDepartment().getName() : null)
                .faculty(course.getFaculty())
                .semester(course.getSemester())
                .academicYear(course.getAcademicYear())
                .status(course.getStatus())
                .instructorName(course.getInstructorName())
                .instructorEmail(course.getInstructorEmail())
                .maxStudents(course.getMaxStudents())
                .enrolledStudents(course.getEnrolledStudents())
                .prerequisites(course.getPrerequisites())
                .syllabus(course.getSyllabus())
                .room(course.getRoom())
                .schedule(course.getSchedule())
                .hasAvailableSeats(course.hasAvailableSeats())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(Course course, CourseRequestDTO dto) {
        if (dto.getCourseName() != null) course.setCourseName(dto.getCourseName());
        if (dto.getDescription() != null) course.setDescription(dto.getDescription());
        if (dto.getCredits() != null) course.setCredits(dto.getCredits());
        if (dto.getDepartment() != null && !dto.getDepartment().isEmpty()) {
            Department department = departmentRepository.findByName(dto.getDepartment())
                    .orElseThrow(() -> new RuntimeException("Department not found: " + dto.getDepartment()));
            course.setDepartment(department);
        }

        if (dto.getFaculty() != null) course.setFaculty(dto.getFaculty());
        if (dto.getSemester() != null) course.setSemester(dto.getSemester());
        if (dto.getAcademicYear() != null) course.setAcademicYear(dto.getAcademicYear());
        if (dto.getStatus() != null) course.setStatus(dto.getStatus());
        if (dto.getInstructorEmail() != null) {
            course.setInstructorEmail(dto.getInstructorEmail());
            userRepository.findByEmail(dto.getInstructorEmail()).ifPresent(instructor ->
                    course.setInstructorName(instructor.getFullName())
            );
        }
        if (dto.getMaxStudents() != null) course.setMaxStudents(dto.getMaxStudents());
        if (dto.getPrerequisites() != null) course.setPrerequisites(dto.getPrerequisites());
        if (dto.getSyllabus() != null) course.setSyllabus(dto.getSyllabus());
        if (dto.getRoom() != null) course.setRoom(dto.getRoom());
        if (dto.getSchedule() != null) course.setSchedule(dto.getSchedule());
    }
}