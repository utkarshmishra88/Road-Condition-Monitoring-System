package com.example.Monitoringsystem.config;

import com.example.Monitoringsystem.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
                .authorizeHttpRequests(auth -> auth
                        // 1. PUBLIC ACCESS (Everyone can see these)
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/login.html",
                                "/register.html",
                                "/api/register",
                                "/api/auth/send-otp",   // <--- CRITICAL: Allows sending OTP without login
                                "/api/issues/messages", // Contact form messages
                                "/api/auth/google",     // Google Login
                                "/css/**", "/js/**", "/images/**", "/uploads/**" // Static assets & images
                        ).permitAll()

                        // 2. MAIN ADMIN ONLY (Creating Field Engineers, Deleting Users)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/admin_dashboard.html").hasRole("ADMIN")

                        // 3. LOGGED IN USERS (Citizens & Admins)
                        .requestMatchers("/user_dashboard.html", "/api/issues/**").hasAnyRole("USER", "ADMIN")

                        // 4. EVERYTHING ELSE REQUIRES LOGIN
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")       // Custom Login UI
                        .loginProcessingUrl("/login")   // Backend Login Handler
                        .defaultSuccessUrl("/redirectByRole", true) // Smart Redirect
                        .failureUrl("/login.html?error=true") // Error Handling
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login.html")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo) {
        return username -> {
            // 1. Check for Root Admin (Hardcoded Backup)
            if ("admin".equals(username)) {
                return User.builder()
                        .username("admin")
                        .password(passwordEncoder().encode("admin123"))
                        .roles("ADMIN")
                        .build();
            }

            // 2. Check for Database User
            com.example.Monitoringsystem.model.User dbUser = userRepo.findByPhoneNumber(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Fix Role Format (Remove "ROLE_" prefix if present)
            String role = dbUser.getRole();
            if (role != null && role.startsWith("ROLE_")) {
                role = role.substring(5); // "ROLE_ADMIN" -> "ADMIN"
            }

            return User.builder()
                    .username(dbUser.getPhoneNumber())
                    .password(dbUser.getPassword())
                    .roles(role)
                    .build();
        };
    }
}