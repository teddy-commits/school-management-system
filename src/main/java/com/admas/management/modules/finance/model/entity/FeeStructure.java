package com.admas.management.modules.finance.model.entity;

import com.admas.management.modules.finance.model.enums.FeeCategory;
import com.admas.management.modules.finance.model.enums.FeeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "fee_structures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeType feeType;

    @Enumerated(EnumType.STRING)
    private FeeCategory category;

    @Column(nullable = false)
    private String description;

    private Double amount;

    private String department;
    private String faculty;
    private String program;

    private Boolean isActive = true;
    private Boolean isMandatory = true;

    private Integer academicYear;
    private String semester;

    private LocalDateTime dueDate;
    private Integer gracePeriodDays = 15;
    private Double lateFeePercentage = 5.0;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}