package com.admas.management.modules.grading.service.impl;

import com.admas.management.modules.grading.dto.request.GradeSubmissionDTO;
import com.admas.management.modules.grading.dto.response.GradeResponseDTO;
import com.admas.management.modules.grading.dto.response.TranscriptResponseDTO;
import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.model.entity.Grade;
import com.admas.management.modules.grading.model.enums.CourseStatus;
import com.admas.management.modules.grading.repository.CourseRepository;
import com.admas.management.modules.grading.repository.GradeRepository;
import com.admas.management.modules.grading.service.GPACalculatorService;
import com.admas.management.modules.grading.service.GradeService;
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
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final GPACalculatorService gpaCalculatorService;

    @Override
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'PROFESSOR', 'ADMIN')")
    public GradeResponseDTO submitGrade(GradeSubmissionDTO dto, String instructorEmail) {
        log.info("Submitting grade for student: {} in course: {}", dto.getStudentId(), dto.getCourseCode());

        User student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findByCourseCode(dto.getCourseCode())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Verify instructor teaches this course
        if (!course.getInstructorEmail().equals(instructorEmail) &&
                !instructorEmail.equals("admin@university.com")) {
            throw new RuntimeException("You are not authorized to grade this course");
        }

        // Check if grade already exists
        Grade grade = gradeRepository.findByStudentAndCourse(student, course)
                .orElse(new Grade());

        grade.setStudent(student);
        grade.setCourse(course);
        grade.setScore(dto.getScore());
        grade.setSemester(dto.getSemester());
        grade.setAcademicYear(dto.getAcademicYear());
        grade.setRemarks(dto.getRemarks());
        grade.setGradedBy(instructorEmail);
        grade.setGradedDate(LocalDateTime.now());
        grade.calculateGrade();

        Grade savedGrade = gradeRepository.save(grade);

        // Update student's CGPA
        Double newCGPA = gpaCalculatorService.calculateStudentCGPA(student.getId());
        student.setCgpa(newCGPA);
        userRepository.save(student);

        return mapToDTO(savedGrade);
    }

    @Override
    public GradeResponseDTO updateGrade(Long gradeId, GradeSubmissionDTO dto) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new RuntimeException("Grade not found"));

        grade.setScore(dto.getScore());
        grade.setRemarks(dto.getRemarks());
        grade.calculateGrade();
        grade.setGradedDate(LocalDateTime.now());

        Grade updatedGrade = gradeRepository.save(grade);
        return mapToDTO(updatedGrade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponseDTO> getStudentGrades(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return gradeRepository.findByStudent(student)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'PROFESSOR', 'ADMIN')")
    public List<GradeResponseDTO> getCourseGrades(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return gradeRepository.findByCourse(course)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TranscriptResponseDTO generateTranscript(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return gpaCalculatorService.generateTranscript(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateStudentCGPA(Long studentId) {
        return gpaCalculatorService.calculateStudentCGPA(studentId);
    }

    @Override
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public void publishGrades(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setStatus(CourseStatus.COMPLETED);
        courseRepository.save(course);

        log.info("Grades published for course: {}", courseCode);
    }

    private GradeResponseDTO mapToDTO(Grade grade) {
        return GradeResponseDTO.builder()
                .id(grade.getId())
                .studentId(grade.getStudent().getId())
                .studentName(grade.getStudent().getFullName())
                .studentIdNumber(grade.getStudent().getStudentId())
                .courseCode(grade.getCourse().getCourseCode())
                .courseName(grade.getCourse().getCourseName())
                .score(grade.getScore())
                .gradeLetter(grade.getGradeLetter().getSymbol())
                .gradePoint(grade.getGradePoint())
                .semester(grade.getSemester())
                .academicYear(grade.getAcademicYear())
                .remarks(grade.getRemarks())
                .gradedBy(grade.getGradedBy())
                .gradedDate(grade.getGradedDate())
                .build();
    }
}