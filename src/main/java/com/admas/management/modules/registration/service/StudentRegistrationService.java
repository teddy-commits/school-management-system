package com.admas.management.modules.registration.service;

import com.admas.management.modules.registration.dto.request.StudentRegistrationRequest;
import com.admas.management.modules.registration.dto.request.StudentUpdateRequest;
import com.admas.management.modules.registration.dto.response.StudentProfileResponse;
import com.admas.management.modules.registration.dto.response.StudentRegistrationResponse;

import java.util.List;

public interface StudentRegistrationService {

    StudentRegistrationResponse registerStudent(StudentRegistrationRequest request);

    StudentProfileResponse getStudentById(Long id);

    StudentProfileResponse getStudentByStudentId(String studentId);

    StudentProfileResponse getStudentByEmail(String email);

    List<StudentProfileResponse> getAllStudents();

    List<StudentProfileResponse> getStudentsByDepartment(String department);

    List<StudentProfileResponse> getStudentsByFaculty(String faculty);

    List<StudentProfileResponse> getStudentsByEnrollmentYear(Integer year);

    StudentProfileResponse updateStudent(Long id, StudentUpdateRequest request);

    void deactivateStudent(Long id);

    void activateStudent(Long id);

    long getTotalStudentCount();

    List<StudentProfileResponse> searchStudents(String keyword);
}