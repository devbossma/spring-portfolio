package dev.saberlabs.myspringportfolio.notification;

import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));
        return emitter;
    }

    public void sendNotification(UserEntity user, String header, String content) {
        notificationRepository.save(NotificationEntity.builder()
                .user(user)
                .header(header)
                .content(content)
                .read(false)
                .build());

        SseEmitter emitter = emitters.get(user.getId());
        if (emitter != null) {
            try {
                String json = "{\"header\":\"" + escapeJson(header) + "\",\"content\":\"" + escapeJson(content) + "\"}";
                emitter.send(SseEmitter.event().name("notification").data(json));
            } catch (IOException e) {
                emitters.remove(user.getId());
            }
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
