package com.admas.management.modules.registration.model;

import com.admas.management.modules.grading.model.entity.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"semester_registration_id", "course_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CourseEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "semester_registration_id")
    private SemesterRegistration semesterRegistration;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Double credits;
    private Double feePerCredit;
    private Double totalFee;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    private LocalDateTime enrollmentDate;

    @CreatedDate
    private LocalDateTime createdAt;

    public enum EnrollmentStatus {
        ENROLLED, DROPPED, COMPLETED, WITHDRAWN
    }

    public void calculateFee() {
        if (credits != null && feePerCredit != null) {
            this.totalFee = credits * feePerCredit;
        }
    }
}
