package com.ddobang.backend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Getter
    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        AppConfig.objectMapper = objectMapper;
    }

    private static String activeProfile;

    @Value("${spring.profiles.active}")
    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }

    public static boolean isProd() {
        return activeProfile.equals("prod");
    }

    public static boolean isDev() {
        return activeProfile.equals("dev");
    }

    public static boolean isTest() {
        return activeProfile.equals("Test");
    }

    public static boolean isNotProd() {
        return !isProd();
    }
}
