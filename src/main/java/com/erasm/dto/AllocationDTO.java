package com.erasm.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AllocationDTO {
    private Long id;

    @NotNull
    private Long employeeId;

    @NotNull
    private Long projectId;

    @NotNull
    @Min(1) @Max(100)
    private Integer allocationPercent;

    private LocalDate startDate;
    private LocalDate endDate;
}
