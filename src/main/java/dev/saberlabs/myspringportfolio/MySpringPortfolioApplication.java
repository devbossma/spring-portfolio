package dev.saberlabs.myspringportfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MySpringPortfolioApplication {

    public static void main(String[] args) {
        SpringApplication.run(MySpringPortfolioApplication.class, args);
    }
}
