package com.erasm.dto;

import com.erasm.entity.SkillLevel;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeSkillDTO {
    @NotNull
    private Long skillId;

    @NotNull
    private SkillLevel level;

    private Integer yearsOfExperience;
}
