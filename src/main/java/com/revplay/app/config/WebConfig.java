package com.revplay.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded audio files from the uploads/audio directory
        registry.addResourceHandler("/audio/**")
                .addResourceLocations("file:uploads/audio/");

        // Serve uploaded images from the uploads/images directory
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/images/");
    }
}
