package com.admas.management.modules.grading.repository;

import com.admas.management.modules.grading.model.entity.Course;
import com.admas.management.modules.grading.model.enums.CourseStatus;
import com.admas.management.modules.grading.model.enums.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByDepartment(String department);
    List<Course> findByFaculty(String faculty);
    List<Course> findBySemester(Semester semester);
    List<Course> findByAcademicYear(Integer year);
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByInstructorEmail(String instructorEmail);
    List<Course> findBySemesterAndAcademicYear(Semester semester, Integer academicYear);
    @Query("SELECT c FROM Course c WHERE c.courseCode LIKE %:keyword% OR c.courseName LIKE %:keyword%")
    List<Course> searchCourses(@Param("keyword") String keyword);

    @Query("SELECT c FROM Course c WHERE c.department = :department AND c.semester = :semester AND c.academicYear = :year")
    List<Course> findCoursesByDepartmentAndSemester(@Param("department") String department,
                                                    @Param("semester") Semester semester,
                                                    @Param("year") Integer year);

    boolean existsByCourseCode(String courseCode);
}
