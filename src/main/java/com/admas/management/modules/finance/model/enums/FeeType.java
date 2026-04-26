package com.admas.management.modules.finance.model.enums;

import lombok.Getter;

@Getter
public enum FeeType {
    TUITION("Tuition Fee", "Per semester course fee"),
    REGISTRATION("Registration Fee", "One-time registration fee"),
    LIBRARY("Library Fee", "Library services fee"),
    LABORATORY("Laboratory Fee", "Lab equipment and materials"),
    SPORTS("Sports Fee", "Sports facilities fee"),
    HOSTEL("Hostel Fee", "Accommodation fee"),
    TRANSPORT("Transport Fee", "Bus service fee"),
    EXAMINATION("Examination Fee", "Exam processing fee"),
    ID_CARD("ID Card Fee", "Student ID card fee"),
    LATE_PAYMENT("Late Payment Fee", "Penalty for late payment"),
    SCHOLARSHIP("Scholarship", "Scholarship deduction"),
    DISCOUNT("Discount", "Special discount");

    private final String displayName;
    private final String description;

    FeeType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
