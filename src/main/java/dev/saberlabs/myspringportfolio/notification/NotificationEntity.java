package dev.saberlabs.myspringportfolio.notification;

import dev.saberlabs.myspringportfolio.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="notifications")
/*
 * JPA entity representing a user notification record.
 * Each notification has a header, content body, read status, and the user it belongs to.
 * Notifications are created when significant events occur (e.g., an investment is activated).
 * */
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String header;
    private String content;
    private boolean read;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;


    @CreationTimestamp
    private LocalDateTime createdAt;

}
