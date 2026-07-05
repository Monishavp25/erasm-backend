package com.erasm.service;

import com.erasm.dto.*;
import com.erasm.entity.*;
import com.erasm.exception.SkillNotFoundException;
import com.erasm.exception.UserNotFoundException;
import com.erasm.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeSkillRepository employeeSkillRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                            EmployeeSkillRepository employeeSkillRepository,
                            SkillRepository skillRepository,
                            UserRepository userRepository,
                            RoleRepository roleRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeSkillRepository = employeeSkillRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<EmployeeDTO> listEmployees() {
        return employeeRepository.findAll().stream().map(this::toDto).toList();
    }

    public EmployeeDTO getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Employee not found: " + id));
        return toDto(employee);
    }

    /**
     * Creates an Employee profile for an ALREADY-REGISTERED user account.
     * (Registering a brand new user + employee together is done via POST /auth/register;
     * this endpoint covers the admin use case of attaching a profile to an existing user.)
     */
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + dto.getUserId()));

        if (employeeRepository.findByUserId(user.getId()).isPresent()) {
            throw new IllegalArgumentException("Employee profile already exists for user: " + dto.getUserId());
        }

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new UserNotFoundException("Role not found: " + dto.getRoleId()));

        Employee employee = Employee.builder()
                .fullName(dto.getFullName())
                .designation(dto.getDesignation())
                .user(user)
                .role(role)
                .totalAllocationPercent(0)
                .build();

        return toDto(employeeRepository.save(employee));
    }

    /** Generic update of an employee's editable profile fields. */
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Employee not found: " + id));
        employee.setFullName(dto.getFullName());
        employee.setDesignation(dto.getDesignation());
        return toDto(employeeRepository.save(employee));
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new UserNotFoundException("Employee not found: " + id);
        }
        employeeRepository.deleteById(id);
    }

    @Transactional
    public EmployeeDTO addSkillToEmployee(Long employeeId, EmployeeSkillDTO dto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new UserNotFoundException("Employee not found: " + employeeId));
        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new SkillNotFoundException("Skill not found: " + dto.getSkillId()));

        EmployeeSkill employeeSkill = EmployeeSkill.builder()
                .employee(employee)
                .skill(skill)
                .level(dto.getLevel())
                .yearsOfExperience(dto.getYearsOfExperience())
                .build();
        employeeSkillRepository.save(employeeSkill);

        return getEmployee(employeeId);
    }

    public EmployeeDTO updateDesignation(Long employeeId, String designation) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new UserNotFoundException("Employee not found: " + employeeId));
        employee.setDesignation(designation);
        return toDto(employeeRepository.save(employee));
    }

    private EmployeeDTO toDto(Employee employee) {
        List<EmployeeSkillDTO> skills = employee.getEmployeeSkills().stream()
                .map(es -> EmployeeSkillDTO.builder()
                        .skillId(es.getSkill().getId())
                        .level(es.getLevel())
                        .yearsOfExperience(es.getYearsOfExperience())
                        .build())
                .toList();

        return EmployeeDTO.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .designation(employee.getDesignation())
                .email(employee.getUser() != null ? employee.getUser().getEmail() : null)
                .role(employee.getRole() != null ? employee.getRole().getName().name() : null)
                .totalAllocationPercent(employee.getTotalAllocationPercent())
                .skills(skills)
                .build();
    }
}
