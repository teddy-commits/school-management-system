package com.admas.management.modules.finance.model.entity;

import com.admas.management.modules.shared.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Fee> fees = new ArrayList<>();

    private Double totalAmount;
    private Double paidAmount = 0.0;
    private Double dueAmount;

    private String semester;
    private Integer academicYear;

    private LocalDateTime issueDate;
    private LocalDateTime dueDate;

    private String status;

    @CreatedDate
    private LocalDateTime createdAt;

    public void calculateTotals() {
        this.totalAmount = fees.stream().mapToDouble(Fee::getAmount).sum();
        this.paidAmount = fees.stream().mapToDouble(Fee::getPaidAmount).sum();
        this.dueAmount = this.totalAmount - this.paidAmount;

        if (this.dueAmount <= 0) {
            this.status = "PAID";
        } else if (this.paidAmount > 0) {
            this.status = "PARTIAL";
        } else if (LocalDateTime.now().isAfter(dueDate)) {
            this.status = "OVERDUE";
        } else {
            this.status = "PENDING";
        }
    }
}
