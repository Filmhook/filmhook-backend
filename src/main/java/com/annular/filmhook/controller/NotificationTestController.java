package com.annular.filmhook.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@RestController
@RequestMapping("/api/test-notification")
public class NotificationTestController {

	 @PostMapping
	 public ResponseEntity<String> sendNotification(			   
			    @RequestParam String deviceToken) {
	        try {
	            // FCM device token for testing
	          //  String deviceToken = "YOUR_DEVICE_FCM_TOKEN";

	            // Create the notification payload
	            Notification notification = Notification.builder()
	                    .setTitle("Test Notification")
	                    .setBody("This is a test push message!")
	                    .build();

	            // Android-specific settings
	            AndroidNotification androidNotification = AndroidNotification.builder()
	                    .setIcon("ic_notification") // matches Android app drawable
	                    .setColor("#FFFFFF") // optional tint
	                    .build();

	            AndroidConfig androidConfig = AndroidConfig.builder()
	                    .setNotification(androidNotification)
	                    .build();

	            // Build the FCM message
	            Message message = Message.builder()
	                    .setToken(deviceToken)
	                    .setNotification(notification)
	                    .setAndroidConfig(androidConfig)
	                    .putData("type", "chat") // custom data for navigation
	                    .putData("chatUserId", "12345") // dynamic value for chat
	                    .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
	                    .build();

	            // Send notification
	            String response = FirebaseMessaging.getInstance().send(message);
	            return ResponseEntity.ok("Notification sent: " + response);

	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(500).body("Error sending notification: " + e.getMessage());
	        }
	    }
}

