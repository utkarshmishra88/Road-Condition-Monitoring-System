package com.example.Monitoringsystem.repository;

import com.example.Monitoringsystem.model.RoadIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface IssueRepository extends JpaRepository<RoadIssue, Long> {
    // 1. Find history for a normal user
    List<RoadIssue> findByReporterPhone(String reporterPhone);

    // 2. Find tasks assigned to a specific Field Engineer
    List<RoadIssue> findByAssignedTo(String assignedTo);

    // 3. Delete all reports by a specific user (For Cascade Delete)
    @Transactional
    void deleteByReporterPhone(String reporterPhone);
}