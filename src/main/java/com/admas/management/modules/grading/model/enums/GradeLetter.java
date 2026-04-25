package com.admas.management.modules.grading.model.enums;

import lombok.Getter;

@Getter
public enum GradeLetter {
    A_PLUS("A+", 4.0, 90, 100, "Excellent"),
    A("A", 4.0, 85, 89, "Excellent"),
    A_MINUS("A-", 3.7, 80, 84, "Very Good"),
    B_PLUS("B+", 3.3, 77, 79, "Good"),
    B("B", 3.0, 73, 76, "Good"),
    B_MINUS("B-", 2.7, 70, 72, "Satisfactory"),
    C_PLUS("C+", 2.3, 67, 69, "Average"),
    C("C", 2.0, 63, 66, "Below Average"),
    C_MINUS("C-", 1.7, 60, 62, "Poor"),
    D("D", 1.0, 50, 59, "Passing"),
    F("F", 0.0, 0, 49, "Fail"),
    W("W", 0.0, 0, 0, "Withdrawn"),
    I("I", 0.0, 0, 0, "Incomplete");

    private final String symbol;
    private final double gradePoint;
    private final int minScore;
    private final int maxScore;
    private final String description;

    GradeLetter(String symbol, double gradePoint, int minScore, int maxScore, String description) {
        this.symbol = symbol;
        this.gradePoint = gradePoint;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.description = description;
    }

    public static GradeLetter fromScore(double score) {
        for (GradeLetter grade : values()) {
            if (score >= grade.minScore && score <= grade.maxScore) {
                return grade;
            }
        }
        return F;
    }
}