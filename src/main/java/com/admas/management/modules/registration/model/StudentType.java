package com.admas.management.modules.registration.model;

public enum StudentType {
    REGULAR("Regular", "Full-time day student"),
    EXTENSION("Extension", "Evening/weekday extension student"),
    WEEKEND("Weekend", "Weekend program student"),
    DISTANCE("Distance", "Distance learning student");

    private final String displayName;
    private final String description;

    StudentType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
