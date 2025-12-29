package com.example.Monitoringsystem.repository;

import com.example.Monitoringsystem.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<ContactMessage, Long> {
}