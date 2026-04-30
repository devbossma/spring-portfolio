package dev.saberlabs.myspringportfolio.notification;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Spring Data JPA repository for NotificationEntity.
 * Provides standard CRUD operations for persisting and retrieving user notifications.
 * */
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
