package com.erasm.service;

import com.erasm.dto.ProjectDTO;
import com.erasm.entity.Project;
import com.erasm.entity.ProjectStatus;
import com.erasm.exception.ProjectNotFoundException;
import com.erasm.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectDTO createProject(ProjectDTO dto) {
        Project project = Project.builder()
                .name(dto.getName())
                .clientName(dto.getClientName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .technologyStack(dto.getTechnologyStack())
                .budget(dto.getBudget())
                .status(ProjectStatus.ACTIVE)
                .build();
        project = projectRepository.save(project);
        log.info("Project created: {}", project.getName());
        return toDto(project);
    }

    public ProjectDTO updateProject(Long id, ProjectDTO dto) {
        Project project = findOrThrow(id);
        project.setName(dto.getName());
        project.setClientName(dto.getClientName());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setTechnologyStack(dto.getTechnologyStack());
        project.setBudget(dto.getBudget());
        return toDto(projectRepository.save(project));
    }

    public ProjectDTO closeProject(Long id) {
        Project project = findOrThrow(id);
        project.setStatus(ProjectStatus.CLOSED);
        return toDto(projectRepository.save(project));
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException("Project not found: " + id);
        }
        projectRepository.deleteById(id);
    }

    public ProjectDTO getProject(Long id) {
        return toDto(findOrThrow(id));
    }

    public List<ProjectDTO> listProjects() {
        return projectRepository.findAll().stream().map(this::toDto).toList();
    }

    private Project findOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found: " + id));
    }

    private ProjectDTO toDto(Project p) {
        return ProjectDTO.builder()
                .id(p.getId()).name(p.getName()).clientName(p.getClientName())
                .startDate(p.getStartDate()).endDate(p.getEndDate())
                .technologyStack(p.getTechnologyStack()).budget(p.getBudget())
                .status(p.getStatus())
                .build();
    }
}
