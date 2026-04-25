package com.admas.management.modules.grading.model.enums;

public enum Semester {
    FALL("Fall Semester", 1),
    SPRING("Spring Semester", 2),
    SUMMER("Summer Semester", 3);

    private final String name;
    private final int order;

    Semester(String name, int order) {
        this.name = name;
        this.order = order;
    }

    public String getName() { return name; }
    public int getOrder() { return order; }
}