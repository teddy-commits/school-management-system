package com.admas.management.modules.registration.service;

import com.admas.management.modules.registration.dto.request.SemesterRegistrationRequestDTO;
import com.admas.management.modules.registration.dto.response.SemesterRegistrationResponseDTO;

import java.util.List;

public interface SemesterRegistrationService {

    SemesterRegistrationResponseDTO initiateRegistration(SemesterRegistrationRequestDTO request);

    SemesterRegistrationResponseDTO addCourses(Long registrationId, List<Long> courseIds);

    SemesterRegistrationResponseDTO removeCourse(Long registrationId, Long courseId);

    SemesterRegistrationResponseDTO completeRegistration(Long registrationId);

    SemesterRegistrationResponseDTO processPayment(Long registrationId, String paymentReference, Double amount);

    List<SemesterRegistrationResponseDTO> getStudentRegistrations(Long studentId);

    SemesterRegistrationResponseDTO getCurrentSemesterRegistration(Long studentId);

    SemesterRegistrationResponseDTO getRegistrationById(Long registrationId);

    boolean canRegisterForSemester(Long studentId, String semester, Integer academicYear);

    List<SemesterRegistrationResponseDTO> getRegistrationsByStatus(String status);
}
