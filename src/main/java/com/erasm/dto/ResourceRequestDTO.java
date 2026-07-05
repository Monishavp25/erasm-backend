package com.erasm.dto;

import com.erasm.entity.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResourceRequestDTO {
    private Long id;

    @NotNull
    private Long projectId;

    @NotNull
    private Long skillId;

    @NotNull
    private Integer requiredCount;

    private RequestStatus status;
}
