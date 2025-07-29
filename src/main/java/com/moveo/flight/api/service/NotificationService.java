package com.moveo.flight.api.service;

import com.moveo.flight.api.dto.NotificationDTO;
import com.moveo.flight.api.model.Notification;
import com.moveo.flight.api.model.User;
import com.moveo.flight.api.model.NotificationStatus;
import com.moveo.flight.api.model.NotificationType;
import com.moveo.flight.api.repository.NotificationRepository;
import com.moveo.flight.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * NotificationService manages the creation and sending of notifications (EMAIL, SMS, PUSH).
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create and save a notification for a user.
     */
    public Notification createNotification(Long userId, NotificationType type, String message) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();
        Notification notification = new Notification(user, type, message);
        return notificationRepository.save(notification);
    }

    /**
     * Send a notification (simulate sending email/SMS/push).
     * In real app, integrate an Email or SMS provider.
     */
    public Notification sendNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        try {
            // Simulate sending notification
            System.out.println("Sending " + notification.getType() + " to user " +
                    notification.getUser().getEmail() + ": " + notification.getMessage());

            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
        }

        return notificationRepository.save(notification);
    }

    /**
     * Get all notifications for a user.
     */
    public List<Notification> getUserNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUser(user);
    }

    /**
     * Get all pending notifications (for a scheduled job)
     */
    public List<Notification> getPendingNotifications() {
        return notificationRepository.findByStatus(NotificationStatus.PENDING);
    }

    public NotificationDTO toDTO (Notification notification) {
        return new NotificationDTO(
        notification.getId(),
        notification.getUser().getId(), // only userId
        notification.getType().name(),
        notification.getMessage(),
        notification.getStatus().name(),
        notification.getCreatedAt()
    );
    } 
}
