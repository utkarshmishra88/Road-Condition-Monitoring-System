package com.example.Monitoringsystem.service;

import com.example.Monitoringsystem.model.RoadIssue;
import com.example.Monitoringsystem.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class IssueService {

    @Autowired private IssueRepository repository;
    private final String UPLOAD_DIR = "uploads/";

    // Save New Report (Handle Image)
    public RoadIssue saveIssue(String description, double lat, double lng, MultipartFile image, String reporterPhone) throws IOException {
        RoadIssue issue = new RoadIssue();
        issue.setDescription(description);
        issue.setLatitude(lat);
        issue.setLongitude(lng);
        issue.setReporterPhone(reporterPhone);

        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, image.getBytes());
            issue.setImageUrl(fileName);
        }
        return repository.save(issue);
    }

    // Helper: Save updates without file handling logic (for Assignment/Verification)
    public RoadIssue saveRaw(RoadIssue issue) {
        return repository.save(issue);
    }

    public List<RoadIssue> getAllIssues() { return repository.findAll(); }

    // For Field Engineers
    public List<RoadIssue> getAssignedIssues(String phone) {
        return repository.findByAssignedTo(phone);
    }

    // For Users
    public List<RoadIssue> getIssuesByUser(String phone) {
        return repository.findByReporterPhone(phone);
    }

    public RoadIssue findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public RoadIssue updateStatus(Long id, String status) {
        RoadIssue issue = findById(id);
        if(issue != null) {
            issue.setStatus(status);
            return repository.save(issue);
        }
        throw new RuntimeException("Issue not found");
    }
}