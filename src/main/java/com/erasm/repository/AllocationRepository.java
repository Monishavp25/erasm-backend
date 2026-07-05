package com.erasm.repository;

import com.erasm.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {
    List<Allocation> findByEmployeeIdAndActiveTrue(Long employeeId);
    List<Allocation> findByProjectId(Long projectId);
}
