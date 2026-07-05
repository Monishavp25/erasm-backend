package com.erasm.controller;

import com.erasm.dto.ProjectDTO;
import com.erasm.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> list() {
        return ResponseEntity.ok(projectService.listProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DELIVERY_MANAGER')")
    public ResponseEntity<ProjectDTO> create(@Valid @RequestBody ProjectDTO dto) {
        return ResponseEntity.ok(projectService.createProject(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DELIVERY_MANAGER')")
    public ResponseEntity<ProjectDTO> update(@PathVariable Long id, @Valid @RequestBody ProjectDTO dto) {
        return ResponseEntity.ok(projectService.updateProject(id, dto));
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN','DELIVERY_MANAGER')")
    public ResponseEntity<ProjectDTO> close(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.closeProject(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
