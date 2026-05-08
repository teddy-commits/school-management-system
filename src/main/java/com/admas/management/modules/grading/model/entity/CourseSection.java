package com.admas.management.modules.grading.model.entity;

import com.admas.management.modules.shared.model.User;
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
@Table(name = "course_sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CourseSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String sectionCode; // 'A', 'B', '01', '02'

    @Column(nullable = false)
    private String semester; // FALL, SPRING, SUMMER

    @Column(nullable = false)
    private Integer academicYear;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;

    private Integer maxStudents = 40;
    private Integer enrolledStudents = 0;

    private String schedule;
    private String room;

    @Enumerated(EnumType.STRING)
    private SectionStatus status = SectionStatus.OPEN;

    @OneToMany(mappedBy = "section")
    private Set<StudentEnrollment> enrollments = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum SectionStatus {
        OPEN, CLOSED, FULL, CANCELLED
    }

    public boolean hasAvailableSeats() {
        return enrolledStudents < maxStudents;
    }

    public void incrementEnrolledStudents() {
        this.enrolledStudents++;
        if (enrolledStudents >= maxStudents) {
            this.status = SectionStatus.FULL;
        }
    }

    public void decrementEnrolledStudents() {
        if (this.enrolledStudents > 0) {
            this.enrolledStudents--;
        }
        if (this.enrolledStudents < maxStudents && this.status == SectionStatus.FULL) {
            this.status = SectionStatus.OPEN;
        }
    }
}