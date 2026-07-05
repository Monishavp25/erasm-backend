package com.erasm.controller;

import com.erasm.dto.ResourceRequestDTO;
import com.erasm.service.ResourceRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resource-requests")
public class ResourceRequestController {

    private final ResourceRequestService resourceRequestService;

    public ResourceRequestController(ResourceRequestService resourceRequestService) {
        this.resourceRequestService = resourceRequestService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DELIVERY_MANAGER')")
    public ResponseEntity<ResourceRequestDTO> create(@Valid @RequestBody ResourceRequestDTO dto) {
        return ResponseEntity.ok(resourceRequestService.createRequest(dto));
    }

    @PutMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN','DELIVERY_MANAGER')")
    public ResponseEntity<ResourceRequestDTO> submit(@PathVariable Long id) {
        return ResponseEntity.ok(resourceRequestService.submit(id));
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN','RESOURCE_MANAGER')")
    public ResponseEntity<ResourceRequestDTO> review(@PathVariable Long id) {
        return ResponseEntity.ok(resourceRequestService.review(id));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','RESOURCE_MANAGER')")
    public ResponseEntity<ResourceRequestDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(resourceRequestService.approve(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','RESOURCE_MANAGER')")
    public ResponseEntity<ResourceRequestDTO> reject(@PathVariable Long id) {
        return ResponseEntity.ok(resourceRequestService.reject(id));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ResourceRequestDTO>> listByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(resourceRequestService.listByProject(projectId));
    }
}
