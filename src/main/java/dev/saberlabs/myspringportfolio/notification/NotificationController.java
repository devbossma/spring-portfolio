package dev.saberlabs.myspringportfolio.notification;

import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
/*
 * REST controller that provides the Server-Sent Events (SSE) endpoint for real-time notifications.
 * Clients connect to /notifications/stream to receive live notification events pushed by NotificationService.
 * */
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/stream")
    /*
     * Opens an SSE connection for the authenticated user and registers their emitter with NotificationService.
     * The connection remains open indefinitely until closed by the client or a timeout/error occurs.
     * Params:
     * - currentUser: The authenticated user establishing the SSE connection.
     * Returns: An SseEmitter that will push notification events to the client.
     * */
    public SseEmitter stream(@AuthenticationPrincipal UserEntity currentUser) {
        return notificationService.subscribe(currentUser.getId());
    }
}
