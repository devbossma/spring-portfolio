package dev.saberlabs.myspringportfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
/*
 * Entry point for the My Spring Portfolio application.
 * @EnableScheduling activates Spring's task scheduling support, required by InvestmentActivationService
 * to schedule investment activation tasks using the configured ThreadPoolTaskScheduler.
 * */
public class MySpringPortfolioApplication {

    public static void main(String[] args) {
        SpringApplication.run(MySpringPortfolioApplication.class, args);
    }
}
