package com.admas.management.modules.grading.dto.request;
import com.admas.management.modules.grading.model.enums.CourseStatus;
import com.admas.management.modules.grading.model.enums.Semester;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDTO {

    @NotBlank(message = "Course code is required")
    @Pattern(regexp = "^[A-Z]{2,4}[0-9]{3,4}$", message = "Course code must be like CS101, MATH2001")
    private String courseCode;

    @NotBlank(message = "Course name is required")
    @Size(min = 3, max = 100, message = "Course name must be between 3 and 100 characters")
    private String courseName;

    private String description;

    @NotNull(message = "Credits are required")
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 6, message = "Credits cannot exceed 6")
    private Integer credits;

    private String department;
    private String faculty;

    @NotNull(message = "Semester is required")
    private Semester semester;

    @NotNull(message = "Academic year is required")
    private Integer academicYear;

    private CourseStatus status;

    @Email(message = "Invalid instructor email")
    private String instructorEmail;

    @Min(value = 5, message = "Minimum 5 students per course")
    @Max(value = 200, message = "Maximum 200 students per course")
    private Integer maxStudents;

    private String prerequisites;
    private String syllabus;
    private String room;
    private String schedule;
}