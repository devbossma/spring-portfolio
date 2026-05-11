package dev.saberlabs.myspringportfolio.notification;

import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    //  sendNotification 

    @Test
    void sendNotification_persistsNotificationWithCorrectFields() {
        UserEntity user = new UserEntity();
        user.setId(99L); // must be non-null so ConcurrentHashMap.get() doesn't throw

        notificationService.sendNotification(user, "Investment Activated", "Your investment is now active.");

        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository).save(captor.capture());
        NotificationEntity saved = captor.getValue();

        assertThat(saved.getHeader()).isEqualTo("Investment Activated");
        assertThat(saved.getContent()).isEqualTo("Your investment is now active.");
        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.isRead()).isFalse();
    }

    //  subscribe 

    @Test
    void subscribe_returnsNonNullEmitter() {
        SseEmitter emitter = notificationService.subscribe(1L);
        assertThat(emitter).isNotNull();
    }
}
