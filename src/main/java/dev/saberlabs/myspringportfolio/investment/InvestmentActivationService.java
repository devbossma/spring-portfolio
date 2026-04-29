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

@Service
public class InvestmentActivationService {

    static final long ACTIVATION_DELAY_SECONDS = 120;

    private final TaskScheduler taskScheduler;
    private final InvestmentRepository investmentRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public InvestmentActivationService(TaskScheduler taskScheduler,
                                       InvestmentRepository investmentRepository,
                                       NotificationService notificationService,
                                       UserRepository userRepository) {
        this.taskScheduler = taskScheduler;
        this.investmentRepository = investmentRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    public void scheduleActivation(Long investmentId, Long userId, Instant activateAt) {
        taskScheduler.schedule(() -> activate(investmentId, userId), activateAt);
    }

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
//Your Investment Akamiz has been successfully created and activated.
    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void recoverPendingInvestments() {
        investmentRepository.findByStatus(InvestmentStatus.PENDING).forEach(inv -> {
            Long userId = inv.getPortfolio().getUser().getId();
            LocalDateTime activateAt = inv.getCreatedAt().plusSeconds(ACTIVATION_DELAY_SECONDS);
            if (!activateAt.isAfter(LocalDateTime.now())) {
                activate(inv.getId(), userId);
            } else {
                long remainingSeconds = Duration.between(LocalDateTime.now(), activateAt).toSeconds();
                scheduleActivation(inv.getId(), userId, Instant.now().plusSeconds(remainingSeconds));
            }
        });
    }
}
