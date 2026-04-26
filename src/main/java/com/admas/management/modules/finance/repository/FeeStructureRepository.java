package com.admas.management.modules.finance.repository;

import com.admas.management.modules.finance.model.entity.FeeStructure;
import com.admas.management.modules.finance.model.enums.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findByIsActiveTrue();
    List<FeeStructure> findByFeeType(FeeType feeType);
    List<FeeStructure> findByDepartmentAndFaculty(String department, String faculty);
    Optional<FeeStructure> findByFeeTypeAndDepartment(FeeType feeType, String department);
}