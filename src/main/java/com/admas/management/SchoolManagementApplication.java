package com.admas.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@RestController
public class SchoolManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolManagementApplication.class, args);
    }
    @GetMapping("/health")
    public String healthCheck() {
        return "School Management System is running successfully!";
    }
}