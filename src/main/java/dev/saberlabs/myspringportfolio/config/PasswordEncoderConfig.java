package dev.saberlabs.myspringportfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
/*
 * Spring configuration class that registers a BCryptPasswordEncoder bean.
 * BCrypt is used throughout the app to securely hash and verify user passwords.
 * */
public class PasswordEncoderConfig {
    @Bean
    /*
     * Provides a BCryptPasswordEncoder as the application-wide PasswordEncoder.
     * Returns: A BCryptPasswordEncoder instance.
     * */
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
