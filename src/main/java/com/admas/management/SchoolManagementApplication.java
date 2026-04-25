package com.admas.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.admas.management")
@EnableJpaRepositories(basePackages = "com.admas.management")
public class SchoolManagementApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SchoolManagementApplication.class, args);

        // Check if controller is loaded
        try {
            Object controller = context.getBean(com.admas.management.modules.registration.controller.StudentRegistrationController.class);
            System.out.println("✅ Controller loaded: " + controller.getClass().getName());
        } catch (Exception e) {
            System.out.println("❌ Controller NOT loaded: " + e.getMessage());
        }

        // Print all registered endpoints
        System.out.println("\n=== Registered Endpoints ===");
        context.getBean(org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping.class)
                .getHandlerMethods()
                .keySet()
                .forEach(mappingInfo -> System.out.println(mappingInfo));
    }
}