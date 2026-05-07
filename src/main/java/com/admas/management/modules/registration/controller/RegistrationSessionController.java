package com.admas.management.modules.registration.controller;

import com.admas.management.modules.registration.dto.request.RegistrationSessionRequestDTO;
import com.admas.management.modules.registration.dto.response.RegistrationSessionResponseDTO;
import com.admas.management.modules.registration.service.RegistrationSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/registration/sessions")
@RequiredArgsConstructor
public class RegistrationSessionController {

    private final RegistrationSessionService sessionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<RegistrationSessionResponseDTO> createSession(@Valid @RequestBody RegistrationSessionRequestDTO request) {
        RegistrationSessionResponseDTO response = sessionService.createSession(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<RegistrationSessionResponseDTO> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody RegistrationSessionRequestDTO request) {
        RegistrationSessionResponseDTO response = sessionService.updateSession(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current")
    public ResponseEntity<RegistrationSessionResponseDTO> getCurrentOpenSession() {
        RegistrationSessionResponseDTO session = sessionService.getCurrentOpenSession();
        if (session == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(session);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkRegistrationStatus() {
        boolean isOpen = sessionService.isRegistrationOpen();
        Map<String, Object> response = new HashMap<>();
        response.put("isOpen", isOpen);
        if (isOpen) {
            RegistrationSessionResponseDTO session = sessionService.getCurrentOpenSession();
            response.put("session", session);
            response.put("message", "Registration is currently OPEN");
            response.put("endDate", session.getEndDate());
        } else {
            response.put("message", "Registration is currently CLOSED");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<RegistrationSessionResponseDTO> getSessionById(@PathVariable Long id) {
        RegistrationSessionResponseDTO response = sessionService.getSessionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<List<RegistrationSessionResponseDTO>> getAllSessions() {
        List<RegistrationSessionResponseDTO> sessions = sessionService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<RegistrationSessionResponseDTO>> getUpcomingSessions() {
        List<RegistrationSessionResponseDTO> sessions = sessionService.getUpcomingSessions();
        return ResponseEntity.ok(sessions);
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<Map<String, String>> closeSession(@PathVariable Long id) {
        sessionService.closeSession(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration session closed successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public ResponseEntity<Map<String, String>> activateSession(@PathVariable Long id) {
        sessionService.activateSession(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration session activated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}