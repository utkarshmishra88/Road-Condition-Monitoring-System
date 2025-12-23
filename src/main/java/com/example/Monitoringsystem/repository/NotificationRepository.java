package com.example.Monitoringsystem.repository;

import com.example.Monitoringsystem.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Find notifications for a specific person (Order by newest first)
    List<Notification> findByRecipientOrderByCreatedAtDesc(String recipient);
}