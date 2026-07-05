package com.erasm.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeDTO {
    private Long id;
    private String fullName;
    private String designation;
    private String email;
    private String role;
    private Integer totalAllocationPercent;
    private List<EmployeeSkillDTO> skills;

    // Only used as INPUT on POST /employees (creating an employee profile for
    // an existing user account). Not populated on responses.
    private Long userId;
    private Long roleId;
}
