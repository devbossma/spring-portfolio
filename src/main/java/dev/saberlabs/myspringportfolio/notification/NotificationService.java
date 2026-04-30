package dev.saberlabs.myspringportfolio.notification;

import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
/*
 * Manages real-time notification delivery via Server-Sent Events (SSE) and persists notifications to the database.
 * Maintains a ConcurrentHashMap of active SSE emitters keyed by user ID.
 * When a notification is sent, it is saved to the database and, if the user is connected, pushed live via SSE.
 * */
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /*
     * Registers an SSE emitter for the given user and sets up cleanup callbacks for completion, timeout, and errors.
     * Params:
     * - userId: The ID of the user subscribing to notifications.
     * Returns: A long-lived SseEmitter tied to the user's connection.
     * */
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(3 * 60 * 1000L);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));
        return emitter;
    }

    /*
     * Persists a notification to the database and, if the user has an active SSE connection, pushes it immediately.
     * The SSE event is formatted as a JSON string with "header" and "content" fields.
     * If the SSE send fails, the emitter is removed from the active connections map.
     * Params:
     * - user: The recipient user.
     * - header: A short notification title.
     * - content: The notification body text.
     * Returns: void.
     * */
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

    /*
     * Escapes backslashes and double quotes in a string to produce safe JSON string values.
     * Params:
     * - s: The raw string to escape.
     * Returns: The escaped string safe for embedding in a JSON value.
     * */
    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
