package com.admas.management.modules.registration.model;

import com.admas.management.modules.shared.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "semester_registrations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "semester", "academic_year"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SemesterRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @Column(nullable = false)
    private String semester; // FALL, SPRING, SUMMER

    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status = RegistrationStatus.PENDING;

    private Double totalCredits = 0.0;
    private Double totalFees = 0.0;
    private Double feesPaid = 0.0;
    private Double feesDue = 0.0;

    private String paymentReference;
    private LocalDateTime paymentDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "semesterRegistration")
    private List<CourseEnrollment> courseEnrollments = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum RegistrationStatus {
        PENDING,    // Registration initiated but not completed
        COMPLETED,  // Courses selected, payment pending
        PAID,       // Payment completed, fully registered
        CANCELLED,  // Registration cancelled
        DROPPED     // Student dropped the semester
    }

    public void calculateTotals() {
        this.totalCredits = courseEnrollments.stream()
                .mapToDouble(CourseEnrollment::getCredits)
                .sum();

        this.totalFees = courseEnrollments.stream()
                .mapToDouble(ce -> ce.getCredits() * ce.getFeePerCredit())
                .sum();

        this.feesDue = this.totalFees - this.feesPaid;
    }
}
