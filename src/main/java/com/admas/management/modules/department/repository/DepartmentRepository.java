package com.admas.management.modules.department.repository;

import com.admas.management.modules.department.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByCode(String code);

    Optional<Department> findByName(String name);

    List<Department> findByIsActiveTrue();

    List<Department> findByFaculty(String faculty);

    @Query("SELECT d FROM Department d WHERE d.name LIKE %:keyword% OR d.code LIKE %:keyword%")
    List<Department> searchDepartments(@Param("keyword") String keyword);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    long countByIsActiveTrue();
}