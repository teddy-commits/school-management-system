package com.admas.management.modules.grading.model.entity;

import com.admas.management.modules.shared.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StudentEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private CourseSection section;

    private LocalDateTime enrollmentDate;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @ManyToOne
    @JoinColumn(name = "grade_id")
    private Grade grade;

    @CreatedDate
    private LocalDateTime createdAt;

    public enum EnrollmentStatus {
        ENROLLED, DROPPED, COMPLETED, WITHDRAWN
    }
}