package com.admas.management.modules.finance.model.entity;

import com.admas.management.modules.finance.model.enums.FeeType;
import com.admas.management.modules.finance.model.enums.PaymentStatus;
import com.admas.management.modules.shared.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "fees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "fee_structure_id")
    private FeeStructure feeStructure;

    @Enumerated(EnumType.STRING)
    private FeeType feeType;

    private Double amount;
    private Double paidAmount = 0.0;
    private Double dueAmount;

    private String description;
    private String semester;
    private Integer academicYear;

    private LocalDateTime dueDate;
    private Boolean isLate = false;
    private Double lateFee = 0.0;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    private String invoiceNumber;

    @Column(length = 500)
    private String remarks;

    @Column(length = 500)
    private String waiverReason;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void calculateDueAmount() {
        this.dueAmount = this.amount - this.paidAmount;
        if (this.dueAmount <= 0) {
            this.status = PaymentStatus.PAID;
            this.dueAmount = 0.0;
        } else if (this.paidAmount > 0) {
            this.status = PaymentStatus.PARTIAL;
        }
    }

    public void applyLateFee() {
        if (LocalDateTime.now().isAfter(dueDate) && this.dueAmount > 0) {
            this.isLate = true;
            this.lateFee = this.dueAmount * (feeStructure != null ? feeStructure.getLateFeePercentage() / 100 : 0.05);
            this.amount += this.lateFee;
            this.calculateDueAmount();
        }
    }
}