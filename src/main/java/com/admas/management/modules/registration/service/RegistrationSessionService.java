package com.admas.management.modules.registration.service;

import com.admas.management.modules.registration.dto.request.RegistrationSessionRequestDTO;
import com.admas.management.modules.registration.dto.response.RegistrationSessionResponseDTO;

import java.util.List;

public interface RegistrationSessionService {

    RegistrationSessionResponseDTO createSession(RegistrationSessionRequestDTO request);

    RegistrationSessionResponseDTO updateSession(Long id, RegistrationSessionRequestDTO request);

    RegistrationSessionResponseDTO getCurrentOpenSession();

    RegistrationSessionResponseDTO getSessionById(Long id);

    List<RegistrationSessionResponseDTO> getAllSessions();

    List<RegistrationSessionResponseDTO> getUpcomingSessions();

    void closeSession(Long id);

    void activateSession(Long id);

    void deleteSession(Long id);

    boolean isRegistrationOpen();

    RegistrationSessionResponseDTO getSessionBySemesterAndYear(String semester, Integer academicYear);
}
