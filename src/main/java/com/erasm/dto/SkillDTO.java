package com.erasm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SkillDTO {
    private Long id;

    @NotBlank
    private String name;
}
