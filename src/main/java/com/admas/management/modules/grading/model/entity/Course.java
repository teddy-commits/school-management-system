package com.admas.management.modules.grading.model.entity;


import com.admas.management.modules.grading.model.enums.CourseStatus;
import com.admas.management.modules.grading.model.enums.Semester;
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
@Table(name = "courses", uniqueConstraints = {
        @UniqueConstraint(columnNames = "courseCode")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String courseCode;

    @Column(nullable = false)
    private String courseName;

    @Column(length = 500)
    private String description;

    private Integer credits;

    private String department;
    private String faculty;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    private Integer academicYear;

    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.DRAFT;

    private String instructorName;
    private String instructorEmail;

    private Integer maxStudents;
    private Integer enrolledStudents = 0;

    private String prerequisites;
    private String syllabus;

    private String room;
    private String schedule;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "course")
    private Set<Enrollment> enrollments = new HashSet<>();

    @OneToMany(mappedBy = "course")
    private Set<Grade> grades = new HashSet<>();

    public boolean hasAvailableSeats() {
        return enrolledStudents < maxStudents;
    }

    public void incrementEnrolledStudents() {
        this.enrolledStudents++;
    }

    public void decrementEnrolledStudents() {
        if (this.enrolledStudents > 0) {
            this.enrolledStudents--;
        }
    }
}
