package com.erasm.controller;

import com.erasm.dto.*;
import com.erasm.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> list() {
        return ResponseEntity.ok(employeeService.listEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDTO> create(@Valid @RequestBody EmployeeDTO dto) {
        return ResponseEntity.ok(employeeService.createEmployee(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> update(@PathVariable Long id, @Valid @RequestBody EmployeeDTO dto) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/skills")
    public ResponseEntity<EmployeeDTO> addSkill(@PathVariable Long id, @Valid @RequestBody EmployeeSkillDTO dto) {
        return ResponseEntity.ok(employeeService.addSkillToEmployee(id, dto));
    }

    @PutMapping("/{id}/designation")
    public ResponseEntity<EmployeeDTO> updateDesignation(@PathVariable Long id, @RequestBody String designation) {
        return ResponseEntity.ok(employeeService.updateDesignation(id, designation));
    }
}
