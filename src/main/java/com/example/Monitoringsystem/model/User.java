package com.example.Monitoringsystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String dob;
    private String nativePlace;

    @Column(unique = true)
    private String phoneNumber;

    private String aadharNumber;
    private String password;
    private String role;

    // --- MANUAL GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    public String getNativePlace() { return nativePlace; }
    public void setNativePlace(String nativePlace) { this.nativePlace = nativePlace; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}