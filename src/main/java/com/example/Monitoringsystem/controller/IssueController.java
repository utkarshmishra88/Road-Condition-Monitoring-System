package com.example.Monitoringsystem.controller;

import com.example.Monitoringsystem.model.ContactMessage;
import com.example.Monitoringsystem.model.Notification;
import com.example.Monitoringsystem.model.RoadIssue;
import com.example.Monitoringsystem.repository.ContactRepository;
import com.example.Monitoringsystem.repository.IssueRepository; // Direct Repo access for delete
import com.example.Monitoringsystem.service.IssueService;
import com.example.Monitoringsystem.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "*")
public class IssueController {

    @Autowired private IssueService service;
    @Autowired private IssueRepository issueRepository; // Added for Delete
    @Autowired private ContactRepository contactRepo;
    @Autowired private NotificationService notificationService;

    // --- 1. GET REPORTS (Fixed Visibility) ---
    @GetMapping
    public List<RoadIssue> getAllIssues(Principal principal) {
        String currentUser = principal.getName();
        if ("admin".equals(currentUser)) {
            // Main Admin sees ALL
            return service.getAllIssues();
        } else {
            // Field Engineer sees ONLY tasks assigned to their Phone Number
            return service.getAssignedIssues(currentUser);
        }
    }

    // --- 2. REPORT CREATION ---
    @PostMapping
    public ResponseEntity<RoadIssue> reportIssue(@RequestParam("description") String description, @RequestParam("latitude") double latitude, @RequestParam("longitude") double longitude, @RequestParam("image") MultipartFile image, Principal principal) {
        try {
            RoadIssue saved = service.saveIssue(description, latitude, longitude, image, principal.getName());
            notificationService.send("ADMIN", "🚨 New Hazard Reported: " + description);
            return ResponseEntity.ok(saved);
        } catch (IOException e) { return ResponseEntity.internalServerError().build(); }
    }

    // --- 3. DELETE REPORT (Main Admin Only) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReport(@PathVariable Long id, Principal principal) {
        if(!"admin".equals(principal.getName())) return ResponseEntity.status(403).body("Unauthorized");

        if(issueRepository.existsById(id)) {
            issueRepository.deleteById(id);
            return ResponseEntity.ok("Report Deleted Permanently");
        }
        return ResponseEntity.notFound().build();
    }

    // --- 4. ASSIGN (Main Admin Only) ---
    @PutMapping("/{id}/assign")
    public ResponseEntity<RoadIssue> assignIssue(@PathVariable Long id, @RequestParam String adminPhone, Principal principal) {
        if(!"admin".equals(principal.getName())) return ResponseEntity.status(403).build();

        RoadIssue issue = service.findById(id);
        if(issue != null) {
            issue.setAssignedTo(adminPhone);
            issue.setStatus("ASSIGNED");
            service.saveRaw(issue);
            notificationService.send(adminPhone, "🛠 You have been assigned Report #" + id);
            return ResponseEntity.ok(issue);
        }
        return ResponseEntity.notFound().build();
    }

    // --- 5. VERIFY (Field Engineer Only) ---
    @PutMapping("/{id}/verify")
    public ResponseEntity<RoadIssue> verifyIssue(@PathVariable Long id, Principal principal) {
        RoadIssue issue = service.findById(id);
        // Strict check: Is this assigned to the person logged in?
        if(issue != null && (principal.getName().equals(issue.getAssignedTo()) || "admin".equals(principal.getName()))) {
            issue.setStatus("VERIFIED");
            service.saveRaw(issue);
            notificationService.send("ADMIN", "✅ Report #" + id + " Verified by Field Team.");
            return ResponseEntity.ok(issue);
        }
        return ResponseEntity.notFound().build();
    }

    // --- 6. RESOLVE (Main Admin Only) ---
    @PutMapping("/{id}/status")
    public ResponseEntity<RoadIssue> updateStatus(@PathVariable Long id, @RequestParam String status, Principal principal) {
        if(!"admin".equals(principal.getName())) return ResponseEntity.status(403).build();

        RoadIssue updated = service.updateStatus(id, status);
        if ("RESOLVED".equals(status)) {
            String userPhone = updated.getReporterPhone();
            if (userPhone != null) notificationService.send(userPhone, "🎉 Your Report #" + id + " has been RESOLVED!");
        }
        return ResponseEntity.ok(updated);
    }

    // --- OTHERS ---
    @GetMapping("/my-history")
    public List<RoadIssue> getMyIssues(Principal principal) { return service.getIssuesByUser(principal.getName()); }

    @PostMapping("/messages")
    public ResponseEntity<String> saveMessage(@RequestBody ContactMessage message) {
        contactRepo.save(message);
        notificationService.send("ADMIN", "📩 New Message");
        return ResponseEntity.ok("Received");
    }
    @GetMapping("/messages")
    public List<ContactMessage> getAllMessages(Principal principal) {
        if(!"admin".equals(principal.getName())) return List.of();
        return contactRepo.findAll();
    }
    @DeleteMapping("/messages/{id}")
    public ResponseEntity<String> deleteMessage(@PathVariable Long id, Principal principal) {
        if(!"admin".equals(principal.getName())) return ResponseEntity.status(403).build();
        contactRepo.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
    @GetMapping("/notifications")
    public List<Notification> getNotifications(Principal principal) {
        return notificationService.getMyNotifications(principal.getName());
    }
}