package com.erasm.dto;

import com.erasm.entity.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectDTO {
    private Long id;

    @NotBlank
    private String name;

    private String clientName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String technologyStack;
    private Double budget;
    private ProjectStatus status;
}
