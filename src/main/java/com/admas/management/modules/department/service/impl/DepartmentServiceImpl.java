package com.admas.management.modules.department.service.impl;

import com.admas.management.modules.department.dto.request.DepartmentRequestDTO;
import com.admas.management.modules.department.dto.response.DepartmentResponseDTO;
import com.admas.management.modules.department.model.Department;
import com.admas.management.modules.department.repository.DepartmentRepository;
import com.admas.management.modules.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO request) {
        log.info("Creating department: {}", request.getCode());

        if (departmentRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Department code already exists: " + request.getCode());
        }
        if (departmentRepository.existsByName(request.getName())) {
            throw new RuntimeException("Department name already exists: " + request.getName());
        }

        Department department = new Department();
        department.setCode(request.getCode().toUpperCase());
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setFaculty(request.getFaculty());
        department.setHeadOfDepartment(request.getHeadOfDepartment());
        department.setHeadEmail(request.getHeadEmail());
        department.setContactPhone(request.getContactPhone());
        department.setOfficeLocation(request.getOfficeLocation());
        department.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        Department saved = departmentRepository.save(department);
        return mapToResponseDTO(saved, "Department created successfully");
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO request) {
        log.info("Updating department with id: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        if (!department.getCode().equals(request.getCode()) &&
                departmentRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Department code already exists: " + request.getCode());
        }

        if (!department.getName().equals(request.getName()) &&
                departmentRepository.existsByName(request.getName())) {
            throw new RuntimeException("Department name already exists: " + request.getName());
        }

        department.setCode(request.getCode().toUpperCase());
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setFaculty(request.getFaculty());
        department.setHeadOfDepartment(request.getHeadOfDepartment());
        department.setHeadEmail(request.getHeadEmail());
        department.setContactPhone(request.getContactPhone());
        department.setOfficeLocation(request.getOfficeLocation());

        Department updated = departmentRepository.save(department);
        return mapToResponseDTO(updated, "Department updated successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return mapToResponseDTO(department, null);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentByCode(String code) {
        Department department = departmentRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Department not found with code: " + code));
        return mapToResponseDTO(department, null);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentByName(String name) {
        Department department = departmentRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Department not found with name: " + name));
        return mapToResponseDTO(department, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(d -> mapToResponseDTO(d, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> getActiveDepartments() {
        return departmentRepository.findByIsActiveTrue()
                .stream()
                .map(d -> mapToResponseDTO(d, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> getDepartmentsByFaculty(String faculty) {
        return departmentRepository.findByFaculty(faculty)
                .stream()
                .map(d -> mapToResponseDTO(d, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> searchDepartments(String keyword) {
        return departmentRepository.searchDepartments(keyword)
                .stream()
                .map(d -> mapToResponseDTO(d, null))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public void deleteDepartment(Long id) {
        log.info("Deleting department with id: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        departmentRepository.delete(department);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public void activateDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        department.setIsActive(true);
        departmentRepository.save(department);
        log.info("Department activated: {}", department.getName());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACADEMIC_ADMINISTRATOR')")
    public void deactivateDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        department.setIsActive(false);
        departmentRepository.save(department);
        log.info("Department deactivated: {}", department.getName());
    }

    private DepartmentResponseDTO mapToResponseDTO(Department department, String message) {
        return DepartmentResponseDTO.builder()
                .id(department.getId())
                .code(department.getCode())
                .name(department.getName())
                .description(department.getDescription())
                .faculty(department.getFaculty())
                .headOfDepartment(department.getHeadOfDepartment())
                .headEmail(department.getHeadEmail())
                .contactPhone(department.getContactPhone())
                .officeLocation(department.getOfficeLocation())
                .isActive(department.getIsActive())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .message(message != null ? message : "Department retrieved successfully")
                .build();
    }
}