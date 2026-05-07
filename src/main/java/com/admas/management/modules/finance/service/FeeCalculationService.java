package com.admas.management.modules.finance.service;

import com.admas.management.modules.finance.model.entity.Fee;
import com.admas.management.modules.finance.model.entity.FeeStructure;
import com.admas.management.modules.finance.model.entity.Payment;
import com.admas.management.modules.finance.model.enums.FeeType;
import com.admas.management.modules.shared.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FeeCalculationService {

    public double calculateTotalFees(User student, List<FeeStructure> feeStructures, String semester, Integer academicYear) {
        double total = 0.0;

        for (FeeStructure structure : feeStructures) {
            if (isFeeApplicable(structure, student, semester, academicYear)) {
                total += structure.getAmount();
            }
        }

        return total;
    }
    public double calculateTotalPaid(List<Payment> payments) {
        return payments.stream()
                .filter(p -> p.getStatus() == com.admas.management.modules.finance.model.enums.PaymentStatus.PAID)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double calculateOutstandingBalance(double totalFees, double totalPaid) {
        return Math.max(0, totalFees - totalPaid);
    }
    public double calculateLateFee(Fee fee, LocalDateTime currentDate) {
        if (fee.getDueDate() == null || currentDate.isBefore(fee.getDueDate())) {
            return 0.0;
        }

        long daysOverdue = ChronoUnit.DAYS.between(fee.getDueDate(), currentDate);
        double lateFeePercentage = fee.getFeeStructure() != null ? fee.getFeeStructure().getLateFeePercentage() : 5.0;
        double lateFee = fee.getDueAmount() * (lateFeePercentage / 100) * (daysOverdue / 30.0);
        return Math.min(lateFee, fee.getAmount() * 0.5);
    }
    public double calculateScholarshipAmount(User student, double totalFees) {
        Double cgpa = student.getCgpa();

        if (cgpa == null) return 0.0;

        if (cgpa >= 3.8) {
            return totalFees * 0.30;
        } else if (cgpa >= 3.5) {
            return totalFees * 0.20;
        } else if (cgpa >= 3.0) {
            return totalFees * 0.10;
        }

        return 0.0;
    }
    public double calculateInstallmentAmount(double totalFees, int numberOfInstallments) {
        if (numberOfInstallments <= 0) return totalFees;
        return totalFees / numberOfInstallments;
    }
    public double calculateTotalCollected(List<Payment> payments, LocalDateTime startDate, LocalDateTime endDate) {
        return payments.stream()
                .filter(p -> p.getPaymentDate() != null)
                .filter(p -> !p.getPaymentDate().isBefore(startDate) && !p.getPaymentDate().isAfter(endDate))
                .filter(p -> p.getStatus() == com.admas.management.modules.finance.model.enums.PaymentStatus.PAID)
                .mapToDouble(Payment::getAmount)
                .sum();
    }
    public java.util.Map<FeeType, Double> calculateFeeBreakdown(List<Fee> fees) {
        java.util.Map<FeeType, Double> breakdown = new java.util.HashMap<>();

        for (Fee fee : fees) {
            breakdown.merge(fee.getFeeType(), fee.getAmount(), Double::sum);
        }

        return breakdown;
    }
    public double calculatePaymentProgress(double totalFees, double totalPaid) {
        if (totalFees <= 0) return 0.0;
        return (totalPaid / totalFees) * 100;
    }
    public double calculateAveragePaymentPerStudent(double totalCollected, long studentCount) {
        if (studentCount == 0) return 0.0;
        return totalCollected / studentCount;
    }
    public double calculateProjectedRevenue(List<User> students, List<FeeStructure> feeStructures, String semester, Integer academicYear) {
        double projectedRevenue = 0.0;

        for (User student : students) {
            if (student.isStudent()) {
                projectedRevenue += calculateTotalFees(student, feeStructures, semester, academicYear);
            }
        }

        return projectedRevenue;
    }
    public double calculateProratedRefund(double totalFee, LocalDateTime enrollmentDate, LocalDateTime withdrawalDate, int totalDaysInSemester) {
        if (enrollmentDate == null || withdrawalDate == null) return 0.0;

        long daysAttended = ChronoUnit.DAYS.between(enrollmentDate, withdrawalDate);
        double percentageUsed = (double) daysAttended / totalDaysInSemester;
        double refundPercentage = Math.min(1 - percentageUsed, 0.8);

        return totalFee * refundPercentage;
    }
    private boolean isFeeApplicable(FeeStructure structure, User student, String semester, Integer academicYear) {
        if (!structure.getIsActive()) return false;
        if (!"ALL".equals(structure.getSemester()) && !structure.getSemester().equals(semester)) return false;

        if (structure.getAcademicYear() != null && !structure.getAcademicYear().equals(academicYear)) return false;
        if (!"ALL".equals(structure.getDepartment()) &&
                !structure.getDepartment().equals(student.getDepartment())) return false;
        if (!"ALL".equals(structure.getFaculty()) &&
                !structure.getFaculty().equals(student.getFaculty())) return false;

        return true;
    }
}
