package com.erasm.service;

import com.erasm.dto.AllocationDTO;
import com.erasm.entity.*;
import com.erasm.exception.AllocationException;
import com.erasm.exception.ProjectNotFoundException;
import com.erasm.exception.UserNotFoundException;
import com.erasm.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implements Module 7 (Resource Allocation).
 * Core business rule: an employee's total allocation across active
 * allocations must never exceed 100%.
 */
@Service
public class AllocationService {

    private static final Logger log = LoggerFactory.getLogger(AllocationService.class);

    private final AllocationRepository allocationRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;

    public AllocationService(AllocationRepository allocationRepository,
                              EmployeeRepository employeeRepository,
                              ProjectRepository projectRepository) {
        this.allocationRepository = allocationRepository;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public AllocationDTO allocate(AllocationDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new UserNotFoundException("Employee not found: " + dto.getEmployeeId()));
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found: " + dto.getProjectId()));

        int currentTotal = allocationRepository.findByEmployeeIdAndActiveTrue(employee.getId())
                .stream().mapToInt(Allocation::getAllocationPercent).sum();

        int newTotal = currentTotal + dto.getAllocationPercent();
        if (newTotal > 100) {
            throw new AllocationException(
                    "Allocation exceeds 100%% for employee " + employee.getFullName() +
                    ": current=" + currentTotal + "%, requested=" + dto.getAllocationPercent() + "%");
        }

        Allocation allocation = Allocation.builder()
                .employee(employee)
                .project(project)
                .allocationPercent(dto.getAllocationPercent())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .active(true)
                .build();
        allocation = allocationRepository.save(allocation);

        employee.setTotalAllocationPercent(newTotal);
        employeeRepository.save(employee);

        log.info("Resource allocated: employee={}, project={}, percent={}",
                employee.getId(), project.getId(), dto.getAllocationPercent());

        return toDto(allocation);
    }

    @Transactional
    public void release(Long allocationId) {
        Allocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new AllocationException("Allocation not found: " + allocationId));

        allocation.setActive(false);
        allocationRepository.save(allocation);

        Employee employee = allocation.getEmployee();
        int newTotal = allocationRepository.findByEmployeeIdAndActiveTrue(employee.getId())
                .stream().mapToInt(Allocation::getAllocationPercent).sum();
        employee.setTotalAllocationPercent(newTotal);
        employeeRepository.save(employee);
    }

    /**
     * Updates an existing allocation's percentage/dates. Re-validates the 100%
     * cap by excluding this allocation's current percent before re-adding the new one.
     */
    @Transactional
    public AllocationDTO updateAllocation(Long id, AllocationDTO dto) {
        Allocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new AllocationException("Allocation not found: " + id));

        Employee employee = allocation.getEmployee();

        int totalExcludingThis = allocationRepository.findByEmployeeIdAndActiveTrue(employee.getId())
                .stream()
                .filter(a -> !a.getId().equals(id))
                .mapToInt(Allocation::getAllocationPercent)
                .sum();

        int newTotal = totalExcludingThis + dto.getAllocationPercent();
        if (allocation.isActive() && newTotal > 100) {
            throw new AllocationException(
                    "Allocation exceeds 100%% for employee " + employee.getFullName() +
                    ": other active allocations=" + totalExcludingThis + "%, requested=" + dto.getAllocationPercent() + "%");
        }

        allocation.setAllocationPercent(dto.getAllocationPercent());
        allocation.setStartDate(dto.getStartDate());
        allocation.setEndDate(dto.getEndDate());
        allocation = allocationRepository.save(allocation);

        if (allocation.isActive()) {
            employee.setTotalAllocationPercent(newTotal);
            employeeRepository.save(employee);
        }

        return toDto(allocation);
    }

    public List<AllocationDTO> listAll() {
        return allocationRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<AllocationDTO> listByProject(Long projectId) {
        return allocationRepository.findByProjectId(projectId).stream().map(this::toDto).toList();
    }

    private AllocationDTO toDto(Allocation a) {
        return AllocationDTO.builder()
                .id(a.getId())
                .employeeId(a.getEmployee().getId())
                .projectId(a.getProject().getId())
                .allocationPercent(a.getAllocationPercent())
                .startDate(a.getStartDate())
                .endDate(a.getEndDate())
                .build();
    }
}
