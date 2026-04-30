package dev.saberlabs.myspringportfolio.config;

import dev.saberlabs.myspringportfolio.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
/*
 * Configures Spring Security for the application.
 * Defines which routes are public, sets up form-based login and logout behavior,
 * and wires the custom UserDetailsService.
 * CSRF protection is enabled (Spring Security default). All POST forms use th:action,
 * so Thymeleaf injects the CSRF token automatically — no manual token handling needed.
 * */
public class SecurityConfig {

    private final UserService userDetailService;

    public SecurityConfig(UserService userDetailService) {
        this.userDetailService = userDetailService;
    }


    @Bean
    /*
     * Defines the security filter chain that controls access to all HTTP endpoints.
     * - Public routes: /, /auth/**, /css/**, /js/**
     * - All other routes require authentication.
     * - Login page: /auth/login with a success redirect to /portfolio.
     * - Logout: /auth/logout, invalidates session and deletes JSESSIONID cookie.
     * Params:
     * - http: The HttpSecurity builder provided by Spring Security.
     * Returns: A configured SecurityFilterChain.
     * */
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/", "/auth/**", "/css/**", "/js/**").permitAll()
                                .anyRequest().authenticated()

                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .formLogin(
                        form -> form
                                .loginPage("/auth/login")
                                .defaultSuccessUrl("/portfolio", true)
                                .permitAll()
                )
                .userDetailsService(this.userDetailService);
        return http.build();
    }

}
