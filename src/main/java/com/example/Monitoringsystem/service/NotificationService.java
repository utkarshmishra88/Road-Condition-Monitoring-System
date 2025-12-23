package com.example.Monitoringsystem.service;

import com.example.Monitoringsystem.model.Notification;
import com.example.Monitoringsystem.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repo;

    public void send(String recipient, String message) {
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setMessage(message);
        repo.save(n);
    }

    public List<Notification> getMyNotifications(String recipient) {
        return repo.findByRecipientOrderByCreatedAtDesc(recipient);
    }
}