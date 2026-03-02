package dev.saberlabs.myspringportfolio.config;

import dev.saberlabs.myspringportfolio.auth.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userDetailService;

    public SecurityConfig(UserService userDetailService) {
        this.userDetailService = userDetailService;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/auth/**").permitAll()
                                .anyRequest().authenticated()

                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
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
