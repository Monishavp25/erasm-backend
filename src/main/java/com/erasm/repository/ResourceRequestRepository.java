package com.erasm.repository;

import com.erasm.entity.ResourceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResourceRequestRepository extends JpaRepository<ResourceRequest, Long> {
    List<ResourceRequest> findByProjectId(Long projectId);
}
