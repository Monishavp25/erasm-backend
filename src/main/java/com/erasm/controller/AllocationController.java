package com.erasm.controller;

import com.erasm.dto.AllocationDTO;
import com.erasm.service.AllocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/allocations")
public class AllocationController {

    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESOURCE_MANAGER')")
    public ResponseEntity<AllocationDTO> allocate(@Valid @RequestBody AllocationDTO dto) {
        return ResponseEntity.ok(allocationService.allocate(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESOURCE_MANAGER')")
    public ResponseEntity<AllocationDTO> update(@PathVariable Long id, @Valid @RequestBody AllocationDTO dto) {
        return ResponseEntity.ok(allocationService.updateAllocation(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<AllocationDTO>> listAll() {
        return ResponseEntity.ok(allocationService.listAll());
    }

    @PutMapping("/{id}/release")
    @PreAuthorize("hasAnyRole('ADMIN','RESOURCE_MANAGER')")
    public ResponseEntity<Void> release(@PathVariable Long id) {
        allocationService.release(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<AllocationDTO>> listByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(allocationService.listByProject(projectId));
    }
}
