package com.admas.management.modules.department.service;

import com.admas.management.modules.department.dto.request.DepartmentRequestDTO;
import com.admas.management.modules.department.dto.response.DepartmentResponseDTO;

import java.util.List;

public interface DepartmentService {

    DepartmentResponseDTO createDepartment(DepartmentRequestDTO request);

    DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO request);

    DepartmentResponseDTO getDepartmentById(Long id);

    DepartmentResponseDTO getDepartmentByCode(String code);

    DepartmentResponseDTO getDepartmentByName(String name);

    List<DepartmentResponseDTO> getAllDepartments();

    List<DepartmentResponseDTO> getActiveDepartments();

    List<DepartmentResponseDTO> getDepartmentsByFaculty(String faculty);

    List<DepartmentResponseDTO> searchDepartments(String keyword);

    void deleteDepartment(Long id);

    void activateDepartment(Long id);

    void deactivateDepartment(Long id);
}
