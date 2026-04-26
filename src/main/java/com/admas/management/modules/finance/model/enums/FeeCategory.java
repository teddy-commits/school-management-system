package com.admas.management.modules.finance.model.enums;

public enum FeeCategory {
    MANDATORY("Mandatory Fees", "Required for all students"),
    OPTIONAL("Optional Fees", "Based on student choice"),
    ONE_TIME("One-time Fees", "Paid only once"),
    SEMESTER("Semester Fees", "Paid every semester"),
    ANNUAL("Annual Fees", "Paid yearly"),
    PENALTY("Penalty", "Late payment penalties");

    private final String category;
    private final String description;

    FeeCategory(String category, String description) {
        this.category = category;
        this.description = description;
    }

    public String getCategory() { return category; }
    public String getDescription() { return description; }
}
