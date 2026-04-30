package dev.saberlabs.myspringportfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
/*
 * Configures the TaskScheduler bean used by InvestmentActivationService to schedule
 * time-based investment activation tasks.
 * A thread pool of size 5 is used, with threads named "inv-activation-*" for traceability.
 * */
public class SchedulerConfig {

    @Bean
    /*
     * Creates and configures a ThreadPoolTaskScheduler with a pool size of 5.
     * Returns: A configured TaskScheduler bean.
     * */
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("inv-activation-");
        return scheduler;
    }
}
