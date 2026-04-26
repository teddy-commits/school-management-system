package com.admas.management.modules.infrastructure.security;

import com.admas.management.modules.shared.model.Role;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createAdminIfNotExists();
    }

    private void createAdminIfNotExists() {
        // Check if any ADMIN exists
        boolean adminExists = userRepository.findByRole(Role.ADMIN).stream().findAny().isPresent();

        if (!adminExists) {
            log.info("========================================");
            log.info("No admin found. Creating default admin user...");
            log.info("========================================");

            User admin = new User();
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("admin@university.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhoneNumber("+1234567890");
            admin.setRole(Role.ADMIN);
            admin.setEmployeeId(generateEmployeeId());
            admin.setDesignation("System Administrator");
            admin.setIsActive(true);
            admin.setIsEmailVerified(true);

            userRepository.save(admin);

            log.info("✅ Default admin created successfully!");
            log.info("📧 Email: admin@university.com");
            log.info("🔑 Password: admin123");
            log.info("🆔 Employee ID: {}", admin.getEmployeeId());
            log.info("========================================");
        } else {
            log.info("Admin user already exists. Skipping admin creation.");
        }
    }

    private String generateEmployeeId() {
        return "ADMIN" + System.currentTimeMillis();
    }
}
