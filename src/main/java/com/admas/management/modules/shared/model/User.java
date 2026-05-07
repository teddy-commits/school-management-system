package com.admas.management.modules.shared.model;

import com.admas.management.modules.registration.model.StudentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(unique = true)
    private String studentId;

    @Column(unique = true)
    private String employeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> additionalRoles = new HashSet<>();

    private String department;
    private String faculty;
    private Integer enrollmentYear;
    private Integer graduationYear;
    private String currentSemester;
    private Double cgpa;

    @Enumerated(EnumType.STRING)
    private StudentType studentType;

    private String designation;
    private String qualification;
    private LocalDateTime joiningDate;
    private Double salary;

    private Boolean isActive = true;
    private Boolean isEmailVerified = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;
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