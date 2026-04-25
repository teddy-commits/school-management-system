package com.admas.management.modules.grading.model.enums;

public enum CourseStatus {
    DRAFT("Draft"),
    OPEN("Open for Registration"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String description;

    CourseStatus(String description) {
        this.description = description;
    }

    public String getDescription() { return description; }
}