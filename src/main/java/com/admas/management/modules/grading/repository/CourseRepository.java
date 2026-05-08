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

    // Fix: Find by department name (since department is now an object relationship)
    @Query("SELECT c FROM Course c WHERE c.department.name = :departmentName")
    List<Course> findByDepartment(@Param("departmentName") String departmentName);

    List<Course> findByFaculty(String faculty);
    List<Course> findBySemester(Semester semester);
    List<Course> findByAcademicYear(Integer year);
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByInstructorEmail(String instructorEmail);

    @Query("SELECT c FROM Course c WHERE c.semester = :semester AND c.academicYear = :year")
    List<Course> findBySemesterAndAcademicYear(@Param("semester") Semester semester, @Param("year") Integer year);

    // Get courses by instructor's department (using instructor email to get department name)
    @Query("SELECT c FROM Course c WHERE c.department.name = (SELECT u.department.name FROM User u WHERE u.email = :instructorEmail)")
    List<Course> findCoursesByInstructorDepartment(@Param("instructorEmail") String instructorEmail);

    // Get available courses for instructor to teach (based on their department)
    @Query("SELECT c FROM Course c WHERE c.department.name = (SELECT u.department.name FROM User u WHERE u.email = :instructorEmail) AND c.status = 'OPEN'")
    List<Course> findAvailableCoursesByInstructorDepartment(@Param("instructorEmail") String instructorEmail);

    // Fix: Find by department name and status
    @Query("SELECT c FROM Course c WHERE c.department.name = :departmentName AND c.status = :status")
    List<Course> findByDepartmentAndStatus(@Param("departmentName") String departmentName, @Param("status") CourseStatus status);

    @Query("SELECT c FROM Course c WHERE c.courseCode LIKE %:keyword% OR c.courseName LIKE %:keyword%")
    List<Course> searchCourses(@Param("keyword") String keyword);

    // Fix: Find by department name, semester, and academic year
    @Query("SELECT c FROM Course c WHERE c.department.name = :departmentName AND c.semester = :semester AND c.academicYear = :year")
    List<Course> findCoursesByDepartmentAndSemester(@Param("departmentName") String departmentName,
                                                    @Param("semester") Semester semester,
                                                    @Param("year") Integer year);

    boolean existsByCourseCode(String courseCode);

    // Fix: Count courses by department name
    @Query("SELECT COUNT(c) FROM Course c WHERE c.department.name = :departmentName")
    long countByDepartment(@Param("departmentName") String departmentName);
}