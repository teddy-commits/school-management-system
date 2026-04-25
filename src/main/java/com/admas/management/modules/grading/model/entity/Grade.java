package com.admas.management.modules.grading.model.entity;

import com.admas.management.modules.grading.model.enums.GradeLetter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private com.admas.management.modules.shared.model.User student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Double score;

    @Enumerated(EnumType.STRING)
    private GradeLetter gradeLetter;

    private Double gradePoint;

    private String semester;
    private Integer academicYear;

    private String remarks;
    private String gradedBy;

    private LocalDateTime gradedDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void calculateGrade() {
        if (score != null) {
            this.gradeLetter = GradeLetter.fromScore(score);
            this.gradePoint = this.gradeLetter.getGradePoint();
        }
    }
}
