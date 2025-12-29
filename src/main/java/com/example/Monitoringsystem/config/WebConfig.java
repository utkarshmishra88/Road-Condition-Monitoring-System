package com.example.Monitoringsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ye line browser ko permission deti hai images access karne ki
        // URL: http://localhost:8080/uploads/image.jpg
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}