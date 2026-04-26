package com.admas.management.modules.finance.model.enums;

public enum PaymentStatus {
    PENDING("Pending", "Payment not yet made"),
    PAID("Paid", "Payment completed successfully"),
    PARTIAL("Partial", "Partial payment made"),
    OVERDUE("Overdue", "Payment past due date"),
    REFUNDED("Refunded", "Payment refunded"),
    CANCELLED("Cancelled", "Payment cancelled"),
    FAILED("Failed", "Payment failed");

    private final String status;
    private final String description;

    PaymentStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public String getStatus() { return status; }
    public String getDescription() { return description; }
}
