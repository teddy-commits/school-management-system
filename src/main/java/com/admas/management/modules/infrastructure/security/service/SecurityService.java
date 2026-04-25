package com.admas.management.modules.infrastructure.security.service;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;

    public boolean isStudentOwner(Long studentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName(); // This is now studentId or employeeId

        User currentUser = findUserById(currentUserId);
        if (currentUser == null) return false;

        // Check if the user is the student themselves or admin
        return currentUser.getId().equals(studentId) || currentUser.isAdmin();
    }

    public boolean isOwnerOrAdmin(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName();

        User currentUser = findUserById(currentUserId);
        if (currentUser == null) return false;

        return currentUser.getId().equals(userId) || currentUser.isAdmin();
    }

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public User getCurrentUser() {
        String currentUserId = getCurrentUserId();
        return findUserById(currentUserId);
    }

    private User findUserById(String id) {
        return userRepository.findByStudentId(id)
                .orElseGet(() -> userRepository.findByEmployeeId(id)
                        .orElse(null));
    }
}