package com.admas.management.modules.grading.mapper;

import com.admas.management.modules.grading.dto.request.GradeSubmissionDTO;
import com.admas.management.modules.grading.dto.response.GradeResponseDTO;
import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.model.entity.Grade;
import com.admas.management.modules.shared.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GradeMapper {

    public Grade toEntity(GradeSubmissionDTO dto, User student, Course course) {
        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setCourse(course);
        grade.setScore(dto.getScore());
        grade.setSemester(dto.getSemester());
        grade.setAcademicYear(dto.getAcademicYear());
        grade.setRemarks(dto.getRemarks());
        grade.setGradedDate(LocalDateTime.now());
        grade.calculateGrade();
        return grade;
    }

    public GradeResponseDTO toResponseDTO(Grade grade) {
        if (grade == null) return null;

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

    public void updateEntityFromDTO(Grade grade, GradeSubmissionDTO dto) {
        if (dto.getScore() != null) {
            grade.setScore(dto.getScore());
            grade.calculateGrade();
        }
        if (dto.getRemarks() != null) grade.setRemarks(dto.getRemarks());
        grade.setGradedDate(LocalDateTime.now());
    }
}
