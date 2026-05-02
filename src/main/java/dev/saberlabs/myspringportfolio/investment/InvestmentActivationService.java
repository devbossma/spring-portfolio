package dev.saberlabs.myspringportfolio.investment;

import dev.saberlabs.myspringportfolio.notification.NotificationService;
import dev.saberlabs.myspringportfolio.user.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
/*
 * This service is responsible for activating investments after a 2-min delay and sending notifications to users.
 * It also handles the recovery of pending investments on application startup, ensuring that any investments that were pending before a server restart are properly activated or rescheduled.
 * Key functionalities include:
 * - Scheduling the activation of investments after a specified delay (2 minutes).
 * - Activating investments by changing their status to ACTIVE and sending notifications to users.
 * - On application startup, checking for any investments that are still in PENDING status
 *   and either activating them immediately if their activation time has passed or rescheduling their activation if the time has not yet come.
 * */
public class InvestmentActivationService {

    // Delay in seconds before an investment is activated after creation, set to 120 seconds (2 minutes).
    static final long ACTIVATION_DELAY_SECONDS = 120;

    private final ConcurrentHashMap<Long, ScheduledFuture<?>> pendingActivations = new ConcurrentHashMap<>();

    private final TaskScheduler taskScheduler;
    private final InvestmentRepository investmentRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    /*
    * Constructor for InvestmentActivationService, which initializes all required dependencies.
    * Params:
    * - taskScheduler: Spring's TaskScheduler used to schedule tasks for activating investments at a specific time in the future.
    * - investmentRepository: Repository for accessing and managing investment records in the database, used to update investment statuses and retrieve investment information.
    * - notificationService: Service responsible for sending notifications to users, used to notify users when their investments are activated.
    * - userRepository: Repository for accessing user information, used to retrieve user details for sending notifications.
    * Returns: An instance of InvestmentActivationService with all dependencies injected, ready to handle the scheduling and activation of investments as well as sending notifications to users.
    * */
    public InvestmentActivationService(TaskScheduler taskScheduler,
                                       InvestmentRepository investmentRepository,
                                       NotificationService notificationService,
                                       UserRepository userRepository) {
        this.taskScheduler = taskScheduler;
        this.investmentRepository = investmentRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    /*
    * Schedules the activation of an investment after a specified delay.
    * Params:
    * - investmentId: The ID of the investment to be activated.
    * - userId: The ID of the user who made the investment (used for sending notifications).
    * - activateAt: The exact time when the investment should be activated.
    * Returns: None (void). This method schedules a task to run at the specified time and does not return a value.
    * */
    public void scheduleActivation(Long investmentId, Long userId, Instant activateAt) {
        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            pendingActivations.remove(investmentId);
            activate(investmentId, userId);
        }, activateAt);
        if (future != null) {
            pendingActivations.put(investmentId, future);
        }
    }

    public void cancelActivation(Long investmentId) {
        ScheduledFuture<?> future = pendingActivations.remove(investmentId);
        if (future != null) {
            future.cancel(false);
        }
    }

    /*
    * Activates an investment by changing its status to ACTIVE and sending a notification to the user.
    * Params:
    * - investmentId: The ID of the investment to be activated.
    * - userId: The ID of the user who made the investment (used for sending notifications).
    * Returns: None (void). This method performs the activation and notification process without returning a value.
    * */
    void activate(Long investmentId, Long userId) {
        InvestmentEntity investment = investmentRepository.findById(investmentId).orElse(null);
        if (investment == null || !investment.isPending()) return;

        investment.setStatus(InvestmentStatus.ACTIVE);
        investmentRepository.save(investment);

        userRepository.findById(userId).ifPresent(user ->
                notificationService.sendNotification(user,
                        "Investment Activated",
                        "Your investment \"" + investment.getName() + "\" has been successfully created and activated."));
    }

    /*
    * On application startup, this method checks for any investments that are still in PENDING status and either activates them immediately
    * if their activation time has passed or reschedules their activation if the time has not yet come.
    * This ensures that any investments that were pending before a server restart are properly handled and activated as needed.
    * Params: None. This method does not take any parameters and is triggered automatically when the application is ready.
    * Returns: None (void). This method performs its operations without returning a value.
    * */
    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void recoverPendingInvestments() {
        investmentRepository.findByStatus(InvestmentStatus.PENDING).forEach(inv -> {
            Long userId = inv.getPortfolio().getUser().getId();
            LocalDateTime activateAt = inv.getCreatedAt().plusSeconds(ACTIVATION_DELAY_SECONDS);
            if (!activateAt.isAfter(LocalDateTime.now())) {
                activate(inv.getId(), userId);
            } else {
                // If the activation time is still in the future, reschedule the activation task to ensure it runs at the correct time.
                long remainingSeconds = Duration.between(LocalDateTime.now(), activateAt).toSeconds();
                scheduleActivation(inv.getId(), userId, Instant.now().plusSeconds(remainingSeconds));
            }
        });
    }
}
