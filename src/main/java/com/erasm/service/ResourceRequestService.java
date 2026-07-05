package com.erasm.service;

import com.erasm.dto.ResourceRequestDTO;
import com.erasm.entity.*;
import com.erasm.exception.ProjectNotFoundException;
import com.erasm.exception.SkillNotFoundException;
import com.erasm.repository.ProjectRepository;
import com.erasm.repository.ResourceRequestRepository;
import com.erasm.repository.SkillRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implements Module 6 (Approval Workflow): Draft -> Submitted -> Under Review -> Approved -> Allocated -> Completed
 */
@Service
public class ResourceRequestService {

    private final ResourceRequestRepository resourceRequestRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;

    public ResourceRequestService(ResourceRequestRepository resourceRequestRepository,
                                   ProjectRepository projectRepository,
                                   SkillRepository skillRepository) {
        this.resourceRequestRepository = resourceRequestRepository;
        this.projectRepository = projectRepository;
        this.skillRepository = skillRepository;
    }

    public ResourceRequestDTO createRequest(ResourceRequestDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found: " + dto.getProjectId()));
        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new SkillNotFoundException("Skill not found: " + dto.getSkillId()));

        ResourceRequest request = ResourceRequest.builder()
                .project(project)
                .requiredSkill(skill)
                .requiredCount(dto.getRequiredCount())
                .status(RequestStatus.DRAFT)
                .createdBy(currentUser())
                .build();

        return toDto(resourceRequestRepository.save(request));
    }

    public ResourceRequestDTO submit(Long id) {
        return transition(id, RequestStatus.SUBMITTED);
    }

    public ResourceRequestDTO review(Long id) {
        return transition(id, RequestStatus.UNDER_REVIEW);
    }

    public ResourceRequestDTO approve(Long id) {
        return transition(id, RequestStatus.APPROVED);
    }

    public ResourceRequestDTO reject(Long id) {
        return transition(id, RequestStatus.REJECTED);
    }

    public ResourceRequestDTO markAllocated(Long id) {
        return transition(id, RequestStatus.ALLOCATED);
    }

    public ResourceRequestDTO complete(Long id) {
        return transition(id, RequestStatus.COMPLETED);
    }

    public List<ResourceRequestDTO> listByProject(Long projectId) {
        return resourceRequestRepository.findByProjectId(projectId).stream().map(this::toDto).toList();
    }

    private ResourceRequestDTO transition(Long id, RequestStatus newStatus) {
        ResourceRequest request = resourceRequestRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Resource request not found: " + id));
        request.setStatus(newStatus);
        request.setModifiedBy(currentUser());
        return toDto(resourceRequestRepository.save(request));
    }

    private String currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }

    private ResourceRequestDTO toDto(ResourceRequest r) {
        return ResourceRequestDTO.builder()
                .id(r.getId())
                .projectId(r.getProject().getId())
                .skillId(r.getRequiredSkill().getId())
                .requiredCount(r.getRequiredCount())
                .status(r.getStatus())
                .build();
    }
}
