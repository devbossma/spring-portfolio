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
