package com.erasm.repository;

import com.erasm.entity.EmployeeSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeSkillRepository extends JpaRepository<EmployeeSkill, Long> {
    List<EmployeeSkill> findByEmployeeId(Long employeeId);
    List<EmployeeSkill> findBySkillId(Long skillId);
}
