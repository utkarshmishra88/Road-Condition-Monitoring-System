package com.example.Monitoringsystem.controller;

import com.example.Monitoringsystem.model.User;
import com.example.Monitoringsystem.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping("/api/whoami")
    @ResponseBody
    public String whoAmI(Principal principal) { return principal.getName(); }

    // --- SIMPLE REGISTER (NO OTP) ---
    @PostMapping("/api/register")
    @ResponseBody
    public String registerUser(@RequestBody User user) {
        if (userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            return "Error: User already registered!";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        return "Success: User Registered!";
    }

    // --- GOOGLE LOGIN ---
    @PostMapping("/api/auth/google")
    @ResponseBody
    public String googleLogin(@RequestBody String idToken) {
        try {
            String cleanToken = idToken.replace("\"", "");
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(cleanToken);
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();

            if (userRepository.findByPhoneNumber(email).isEmpty()) {
                User newUser = new User();
                newUser.setPhoneNumber(email);
                newUser.setFullName(name);
                newUser.setPassword(passwordEncoder.encode("GOOGLE_USER"));
                newUser.setRole("ROLE_USER");
                newUser.setDob("2000-01-01");
                newUser.setNativePlace("Google");
                newUser.setAadharNumber("000000000000");
                userRepository.save(newUser);
            }
            return "Success";
        } catch (Exception e) {
            return "Error: Invalid Token";
        }
    }

    // --- ADMIN METHODS (Keep these) ---
    @PostMapping("/api/admin/create")
    @ResponseBody
    public String createAdmin(@RequestBody User user, Principal principal) {
        if(!"admin".equals(principal.getName())) return "Error: Unauthorized";
        if (userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) return "Error: Phone exists!";
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_ADMIN");
        user.setFullName(user.getFullName() + " (Field Engineer)");
        userRepository.save(user);
        return "Success: Field Engineer Created!";
    }

    @GetMapping("/api/admin/users")
    @ResponseBody
    public List<User> getAllUsers(Principal principal) {
        if (!"admin".equals(principal.getName())) return List.of();
        return userRepository.findAll().stream().filter(u -> "ROLE_USER".equals(u.getRole())).collect(Collectors.toList());
    }

    @GetMapping("/api/admin/list-admins")
    @ResponseBody
    public List<User> getAllAdmins(Principal principal) {
        if (!"admin".equals(principal.getName())) return List.of();
        return userRepository.findAll().stream().filter(u -> "ROLE_ADMIN".equals(u.getRole()) && !"admin".equals(u.getPhoneNumber())).collect(Collectors.toList());
    }

    @DeleteMapping("/api/admin/users/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable Long id, Principal principal) {
        if (!"admin".equals(principal.getName())) return "Error: Unauthorized";
        if (userRepository.existsById(id)) { userRepository.deleteById(id); return "Deleted Successfully"; }
        return "Error: Not Found";
    }

    @GetMapping("/redirectByRole")
    public String redirectByRole(Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin_dashboard.html";
        } else {
            return "redirect:/user_dashboard.html";
        }
    }
}