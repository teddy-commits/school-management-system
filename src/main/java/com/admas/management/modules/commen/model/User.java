package com.admas.management.modules.commen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "studentId"),
                @UniqueConstraint(columnNames = "employeeId")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Personal Information
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phoneNumber;

    private String address;

    // University Identification
    @Column(unique = true)
    private String studentId;      // For students only

    @Column(unique = true)
    private String employeeId;     // For staff, faculty, admin

    // Role Information
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> additionalRoles = new HashSet<>();  // For multi-role users

    // Academic Information (for students)
    private String department;
    private String faculty;
    private Integer enrollmentYear;
    private Integer graduationYear;
    private String currentSemester;
    private Double cgpa;  // Cumulative GPA

    // Professional Information (for staff & faculty)
    private String designation;      // Assistant Professor, Senior Lecturer, etc.
    private String qualification;    // PhD, Masters, etc.
    private LocalDateTime joiningDate;
    private Double salary;

    // Status and Tracking
    private Boolean isActive = true;
    private Boolean isEmailVerified = false;

    // Audit Fields
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    // Constructors for different user types
    public User(String firstName, String lastName, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isStudent() {
        return role == Role.STUDENT || additionalRoles.contains(Role.STUDENT);
    }

    public boolean isInstructor() {
        return role == Role.INSTRUCTOR || additionalRoles.contains(Role.INSTRUCTOR);
    }

    public boolean isAcademicAdministrator() {
        return role == Role.ACADEMIC_ADMINISTRATOR || additionalRoles.contains(Role.ACADEMIC_ADMINISTRATOR);
    }

    public boolean isManagement() {
        return role == Role.MANAGEMENT || additionalRoles.contains(Role.MANAGEMENT);
    }

    public boolean isAdmin() {
        return role == Role.ADMIN || additionalRoles.contains(Role.ADMIN);
    }

    public String getPrimaryIdentifier() {
        if (studentId != null) return studentId;
        if (employeeId != null) return employeeId;
        return email;
    }
}