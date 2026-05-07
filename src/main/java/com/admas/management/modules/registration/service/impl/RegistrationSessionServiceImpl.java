package com.admas.management.modules.registration.service.impl;

import com.admas.management.modules.registration.dto.request.RegistrationSessionRequestDTO;
import com.admas.management.modules.registration.dto.response.RegistrationSessionResponseDTO;
import com.admas.management.modules.registration.model.RegistrationSession;
import com.admas.management.modules.registration.repository.RegistrationSessionRepository;
import com.admas.management.modules.registration.service.RegistrationSessionService;
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
public class RegistrationSessionServiceImpl implements RegistrationSessionService {

    private final RegistrationSessionRepository sessionRepository;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public RegistrationSessionResponseDTO createSession(RegistrationSessionRequestDTO request) {
        log.info("Creating registration session for {} {}", request.getSemester(), request.getAcademicYear());
        if (sessionRepository.existsBySemesterAndAcademicYear(request.getSemester(), request.getAcademicYear())) {
            throw new RuntimeException("Registration session already exists for this semester and year");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }

        RegistrationSession session = new RegistrationSession();
        session.setSemester(request.getSemester());
        session.setAcademicYear(request.getAcademicYear());
        session.setStartDate(request.getStartDate());
        session.setEndDate(request.getEndDate());
        session.setDescription(request.getDescription());
        session.setIsActive(true);

        RegistrationSession saved = sessionRepository.save(session);
        return mapToResponseDTO(saved);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public RegistrationSessionResponseDTO updateSession(Long id, RegistrationSessionRequestDTO request) {
        log.info("Updating registration session with id: {}", id);

        RegistrationSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }

        session.setStartDate(request.getStartDate());
        session.setEndDate(request.getEndDate());
        session.setDescription(request.getDescription());

        RegistrationSession updated = sessionRepository.save(session);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationSessionResponseDTO getCurrentOpenSession() {
        LocalDateTime now = LocalDateTime.now();
        RegistrationSession session = sessionRepository.findCurrentOpenSession(now)
                .orElse(null);
        return session != null ? mapToResponseDTO(session) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationSessionResponseDTO getSessionById(Long id) {
        RegistrationSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        return mapToResponseDTO(session);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public List<RegistrationSessionResponseDTO> getAllSessions() {
        return sessionRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationSessionResponseDTO> getUpcomingSessions() {
        return sessionRepository.findUpcomingSessions(LocalDateTime.now())
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public void closeSession(Long id) {
        RegistrationSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        session.setIsActive(false);
        sessionRepository.save(session);
        log.info("Closed registration session for {} {}", session.getSemester(), session.getAcademicYear());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public void activateSession(Long id) {
        RegistrationSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        session.setIsActive(true);
        sessionRepository.save(session);
        log.info("Activated registration session for {} {}", session.getSemester(), session.getAcademicYear());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
        log.info("Deleted registration session with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRegistrationOpen() {
        return getCurrentOpenSession() != null;
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationSessionResponseDTO getSessionBySemesterAndYear(String semester, Integer academicYear) {
        RegistrationSession session = sessionRepository.findBySemesterAndAcademicYear(semester, academicYear)
                .orElse(null);
        return session != null ? mapToResponseDTO(session) : null;
    }

    private RegistrationSessionResponseDTO mapToResponseDTO(RegistrationSession session) {
        return RegistrationSessionResponseDTO.builder()
                .id(session.getId())
                .semester(session.getSemester())
                .academicYear(session.getAcademicYear())
                .startDate(session.getStartDate())
                .endDate(session.getEndDate())
                .isActive(session.getIsActive())
                .isCurrentlyOpen(session.isCurrentlyOpen())
                .description(session.getDescription())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }
}