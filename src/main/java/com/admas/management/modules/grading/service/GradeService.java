package com.admas.management.modules.grading.service;



import com.admas.management.modules.grading.dto.request.GradeSubmissionDTO;
import com.admas.management.modules.grading.dto.response.GradeResponseDTO;
import com.admas.management.modules.grading.dto.response.TranscriptResponseDTO;

import java.util.List;

public interface GradeService {
    GradeResponseDTO submitGrade(GradeSubmissionDTO gradeSubmissionDTO, String instructorEmail);
    GradeResponseDTO updateGrade(Long gradeId, GradeSubmissionDTO gradeSubmissionDTO);
    List<GradeResponseDTO> getStudentGrades(Long studentId);
    List<GradeResponseDTO> getCourseGrades(String courseCode);
    TranscriptResponseDTO generateTranscript(Long studentId);
    Double calculateStudentCGPA(Long studentId);
    void publishGrades(String courseCode);
}