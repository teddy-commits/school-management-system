package com.admas.management.modules.commen.model;

public enum Role {
    // Student Roles
    STUDENT("Student", "Can view courses, grades, pay fees"),
    UNDERGRADUATE_STUDENT("Undergraduate Student", "Bachelor degree student"),
    POSTGRADUATE_STUDENT("Postgraduate Student", "Master's or PhD student"),
    RESEARCH_STUDENT("Research Student", "Research scholar"),

    // Academic Roles
    INSTRUCTOR("Instructor", "Can create courses, assign grades"),
    SENIOR_INSTRUCTOR("Senior Instructor", "Senior teaching faculty"),
    PROFESSOR("Professor", "Can manage departments, approve course registrations"),
    ASSOCIATE_PROFESSOR("Associate Professor", "Senior faculty with research responsibilities"),
    ASSISTANT_PROFESSOR("Assistant Professor", "Junior faculty member"),

    // Administrative Roles
    ACADEMIC_ADMINISTRATOR("Academic Administrator", "Manages academic records, transcripts"),
    HOD("Head of Department", "Department head"),
    DEAN("Dean", "Faculty dean"),
    REGISTRAR("Registrar", "Manages student registrations and records"),

    // Management Roles
    MANAGEMENT("Management", "University management team"),
    FINANCE_MANAGER("Finance Manager", "Manages fees, scholarships, payments"),
    HR_MANAGER("HR Manager", "Manages staff and faculty"),

    // System Roles
    ADMIN("System Administrator", "Full system access"),
    SUPER_ADMIN("Super Administrator", "Complete system control");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
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