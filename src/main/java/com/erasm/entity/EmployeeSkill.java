package com.erasm.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents the Many-To-Many relationship between Employee and Skill,
 * with an extra attribute (level) — hence modeled as its own entity
 * instead of a plain @ManyToMany join table.
 */
@Entity
@Table(name = "employee_skills")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    private SkillLevel level;

    private Integer yearsOfExperience;
}
