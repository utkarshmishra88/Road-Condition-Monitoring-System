package com.example.Monitoringsystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class RoadIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private String status;
    private LocalDateTime reportedAt;
    private String reporterPhone;

    private String severity;
    private String assignedTo;

    // --- MANUAL GETTERS AND SETTERS (REQUIRED) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }

    public String getReporterPhone() { return reporterPhone; }
    public void setReporterPhone(String reporterPhone) { this.reporterPhone = reporterPhone; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    @PrePersist
    protected void onCreate() {
        this.reportedAt = LocalDateTime.now();
        if (this.status == null) this.status = "PENDING";

        // Mock AI Logic
        double random = Math.random();
        if (random < 0.33) this.severity = "LOW";
        else if (random < 0.66) this.severity = "MEDIUM";
        else this.severity = "HIGH";
    }
}