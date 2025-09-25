package com.annular.filmhook.controller;

import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void sendPushNotification(String fcmToken, String title, String body) {
        try {
            // ✅ Add your company logo URL (must be HTTPS)
            String logoUrl = "https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmhook-logo.png";

            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .setImage(logoUrl) // 👈 This will show the logo in notification
                    .build();

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification)
                    .putData("type", "chat") // custom data for navigation
                    .putData("chatUserId", "12345") // dynamic value for chat
                    .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("✅ Notification sent successfully: {}", response);
        } catch (FirebaseMessagingException e) {
            logger.error("❌ Failed to send push notification: {}", e.getMessage());
        }
    }
}
