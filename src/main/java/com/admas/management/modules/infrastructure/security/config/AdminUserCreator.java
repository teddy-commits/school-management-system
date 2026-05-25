package com.admas.management.modules.infrastructure.security.config;

import com.admas.management.modules.shared.model.Role;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdminUserCreator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner createAdminUser() {
        return args -> {
            String adminEmail = "teklu@admas.com";
            String adminEmployeeId = "ADMIN001";

            // Check if admin already exists by email OR employee ID
            boolean adminExists = userRepository.findByEmail(adminEmail).isPresent() ||
                    userRepository.findByEmployeeId(adminEmployeeId).isPresent();

            if (!adminExists) {
                log.info("Creating admin user...");

                User admin = User.builder()
                        .firstName("System")
                        .lastName("Administrator")
                        .email(adminEmail)
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .isActive(true)
                        .isEmailVerified(true)
                        .employeeId(adminEmployeeId)
                        .phoneNumber("+251911111111")
                        .address("Addis Ababa, Ethiopia")
                        .build();

                userRepository.save(admin);
                log.info("✅ Admin user created successfully!");
                log.info("   Email: admin@admas.com");
                log.info("   Password: admin123");
                log.info("   Role: ADMIN");
            } else {
                log.info("Admin user already exists. Skipping creation.");

                // Optional: Update existing admin if needed
                userRepository.findByEmail(adminEmail).ifPresentOrElse(
                        existingAdmin -> {
                            log.info("Existing admin found with email: {}", existingAdmin.getEmail());
                            // Update password if needed
                            if (!passwordEncoder.matches("admin123", existingAdmin.getPassword())) {
                                existingAdmin.setPassword(passwordEncoder.encode("admin123"));
                                userRepository.save(existingAdmin);
                                log.info("✅ Admin password updated to 'admin123'");
                            }
                        },
                        () -> log.info("Admin with email {} not found, but employee ID may exist", adminEmail)
                );
            }
        };
    }
}