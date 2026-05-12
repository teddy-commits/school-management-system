package com.admas.management.modules.grading.model.entity;

import com.admas.management.modules.department.model.Department;
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

    // Department relationship (NEW - replaces course requirement)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // Course relationship (optional - can be null initially)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(nullable = false)
    private String sectionCode; // 'A', 'B', '01', '02'

    @Column(nullable = false)
    private Integer academicYearLevel; // 1, 2, 3, 4, 5

    @Column(nullable = false)
    private String semester; // FALL, SPRING, SUMMER

    @Column(nullable = false)
    private Integer academicYear; // 2024, 2025, etc.

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
// Add these to CourseSection.java

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    private Set<SectionInstructor> sectionInstructors = new HashSet<>();

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    private Set<SectionCourse> sectionCourses = new HashSet<>();

    // Helper methods
    public void addInstructor(User instructor, Course course) {
        SectionInstructor si = new SectionInstructor();
        si.setSection(this);
        si.setInstructor(instructor);
        si.setCourse(course);
        sectionInstructors.add(si);
    }

    public void addCourse(Course course, String schedule, String room) {
        SectionCourse sc = new SectionCourse();
        sc.setSection(this);
        sc.setCourse(course);
        sc.setSchedule(schedule);
        sc.setRoom(room);
        sc.setCredits(course.getCredits());
        sectionCourses.add(sc);
    }

    public boolean canAddInstructor() {
        return sectionInstructors.size() < 7;  // Max 7 instructors
    }

    public boolean canAddCourse() {
        return sectionCourses.size() < 7;  // Max 7 courses
    }
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

    // Helper method to get formatted section name
    public String getFormattedSectionName() {
        return String.format("%s - Year %d Section %s",
                department != null ? department.getCode() : "N/A",
                academicYearLevel,
                sectionCode);
    }
}