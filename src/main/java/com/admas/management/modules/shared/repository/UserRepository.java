package com.admas.management.modules.shared.repository;

import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Basic queries
    Optional<User> findByEmail(String email);
    Optional<User> findByStudentId(String studentId);
    Optional<User> findByEmployeeId(String employeeId);

    // Role-based queries
    List<User> findByRole(Role role);
    List<User> findByRoleIn(List<Role> roles);

    // Department queries
    List<User> findByDepartment(String department);
    List<User> findByFaculty(String faculty);

    // Status queries
    List<User> findByIsActive(Boolean isActive);

    List<User> findByEnrollmentYear(Integer year);
    List<User> findByFirstNameContainingOrLastNameContainingOrEmailContaining(String firstName, String lastName, String email);
    // Search queries
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> searchByName(@Param("name") String name);

    // Complex queries
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.department = :department")
    List<User> findByRoleAndDepartment(@Param("role") Role role, @Param("department") String department);

    // Student-specific queries
    @Query("SELECT u FROM User u WHERE u.studentId IS NOT NULL AND u.enrollmentYear = :year")
    List<User> findStudentsByEnrollmentYear(@Param("year") Integer year);

    // Instructor queries
    @Query("SELECT u FROM User u WHERE u.role IN :roles AND u.department = :department")
    List<User> findFacultyByDepartment(@Param("roles") List<Role> roles, @Param("department") String department);

    // Count queries for dashboard
    long countByRole(Role role);
    long countByIsActiveTrue();

    // Check existence
    boolean existsByEmail(String email);
    boolean existsByStudentId(String studentId);
    boolean existsByEmployeeId(String employeeId);
}