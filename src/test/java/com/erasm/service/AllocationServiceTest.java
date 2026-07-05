package com.erasm.service;

import com.erasm.dto.AllocationDTO;
import com.erasm.entity.Employee;
import com.erasm.entity.Project;
import com.erasm.exception.AllocationException;
import com.erasm.repository.AllocationRepository;
import com.erasm.repository.EmployeeRepository;
import com.erasm.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllocationServiceTest {

    @Mock private AllocationRepository allocationRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private ProjectRepository projectRepository;

    @InjectMocks
    private AllocationService allocationService;

    private Employee employee;
    private Project project;

    @BeforeEach
    void setUp() {
        employee = Employee.builder().id(1L).fullName("Jane Doe").totalAllocationPercent(0).build();
        project = Project.builder().id(1L).name("Healthcare Portal").build();
    }

    @Test
    void allocate_succeeds_whenTotalIsWithin100Percent() {
        AllocationDTO dto = AllocationDTO.builder().employeeId(1L).projectId(1L).allocationPercent(60).build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(allocationRepository.findByEmployeeIdAndActiveTrue(1L)).thenReturn(Collections.emptyList());
        when(allocationRepository.save(any())).thenAnswer(inv -> {
            var a = inv.getArgument(0, com.erasm.entity.Allocation.class);
            a.setId(100L);
            return a;
        });

        AllocationDTO result = allocationService.allocate(dto);

        assertEquals(60, result.getAllocationPercent());
        verify(employeeRepository).save(employee);
        assertEquals(60, employee.getTotalAllocationPercent());
    }

    @Test
    void allocate_throws_whenTotalExceeds100Percent() {
        AllocationDTO dto = AllocationDTO.builder().employeeId(1L).projectId(1L).allocationPercent(50).build();

        com.erasm.entity.Allocation existing = com.erasm.entity.Allocation.builder()
                .id(1L).employee(employee).project(project).allocationPercent(70).active(true).build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(allocationRepository.findByEmployeeIdAndActiveTrue(1L)).thenReturn(List.of(existing));

        assertThrows(AllocationException.class, () -> allocationService.allocate(dto));
        verify(allocationRepository, never()).save(any());
    }
}
