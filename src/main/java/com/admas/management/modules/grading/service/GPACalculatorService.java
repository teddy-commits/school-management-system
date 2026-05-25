package com.admas.management.modules.grading.service;

import com.admas.management.modules.grading.dto.response.TranscriptResponseDTO;
import com.admas.management.modules.grading.model.entity.Grade;
import com.admas.management.modules.grading.repository.GradeRepository;
import com.admas.management.modules.shared.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GPACalculatorService {

    private final GradeRepository gradeRepository;

    public Double calculateStudentCGPA(Long studentId) {
        List<Grade> grades = gradeRepository.findByStudent(
                com.admas.management.modules.shared.model.User.builder().id(studentId).build()
        );

        if (grades.isEmpty()) return 0.0;

        double totalGradePoints = 0.0;
        int totalCredits = 0;

        for (Grade grade : grades) {
            if (grade.getGradeLetter() != null && grade.getCourse() != null) {
                totalGradePoints += grade.getGradePoint() * grade.getCourse().getCredits();
                totalCredits += grade.getCourse().getCredits();
            }
        }

        return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
    }

    public TranscriptResponseDTO generateTranscript(User student) {
        List<Grade> grades = gradeRepository.findByStudent(student);
        Map<String, List<Grade>> gradesBySemester = grades.stream()
                .collect(Collectors.groupingBy(g -> g.getAcademicYear() + "-" + g.getSemester()));

        List<TranscriptResponseDTO.SemesterGradeDTO> semesterGrades = new ArrayList<>();
        int totalCreditsEarned = 0;

        for (Map.Entry<String, List<Grade>> entry : gradesBySemester.entrySet()) {
            List<Grade> semesterGradeList = entry.getValue();

            double semesterTotalPoints = 0;
            int semesterCredits = 0;
            List<TranscriptResponseDTO.CourseGradeDTO> courseGrades = new ArrayList<>();

            for (Grade grade : semesterGradeList) {
                if (grade.getGradeLetter() != null && grade.getGradeLetter().getGradePoint() > 0) {
                    semesterTotalPoints += grade.getGradePoint() * grade.getCourse().getCredits();
                    semesterCredits += grade.getCourse().getCredits();
                    totalCreditsEarned += grade.getCourse().getCredits();
                }

                courseGrades.add(TranscriptResponseDTO.CourseGradeDTO.builder()
                        .courseCode(grade.getCourse().getCourseCode())
                        .courseName(grade.getCourse().getCourseName())
                        .credits(grade.getCourse().getCredits())
                        .score(grade.getScore())
                        .gradeLetter(grade.getGradeLetter().getSymbol())
                        .gradePoint(grade.getGradePoint())
                        .build());
            }

            double semesterGPA = semesterCredits > 0 ? semesterTotalPoints / semesterCredits : 0.0;

            semesterGrades.add(TranscriptResponseDTO.SemesterGradeDTO.builder()
                    .semester(entry.getKey())
                    .semesterGPA(semesterGPA)
                    .courses(courseGrades)
                    .build());
        }

        double overallCGPA = calculateStudentCGPA(student.getId());

        String departmentName = null;
        if (student.getDepartment() != null) {
            departmentName = student.getDepartment().getName();
        } else if (student.getDepartmentName() != null) {
            departmentName = student.getDepartmentName();
        }

        return TranscriptResponseDTO.builder()
                .studentId(student.getStudentId())
                .studentName(student.getFullName())
                .department(departmentName)  // Now passing String
                .faculty(student.getFaculty())
                .overallCGPA(overallCGPA)
                .totalCreditsEarned(totalCreditsEarned)
                .semesterGrades(semesterGrades)
                .build();
    }
}
