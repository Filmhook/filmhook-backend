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
	                    .putData("PPppp", "12345") 
	                    .putData("dddddd", "12345") 
	                    
	                    .build();

	            // Send notification
	            String response = FirebaseMessaging.getInstance().send(message);
	            return ResponseEntity.ok("Notification sent: " + response);

	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(500).body("Error sending notification: " + e.getMessage());
	        }
	    }
	 
	 @PostMapping("/chat")
	 public ResponseEntity<String> sendChatNotification(
	         @RequestParam String deviceToken,
	         @RequestParam String senderId,
	         @RequestParam String senderName,
	         @RequestParam String messages // Example: "Hello||Hi||How are you"
	 ) {
	     try {
	         // Create the notification payload
	         Notification notification = Notification.builder()
	                 .setTitle(senderName) // Sender name as title
	                 .setBody("New message from " + senderName)
	                 .build();

	         // Android settings
	         AndroidNotification androidNotification = AndroidNotification.builder()
	                 .setIcon("ic_notification")
	                 .setColor("#4d79ff")
	                 .build();

	         AndroidConfig androidConfig = AndroidConfig.builder()
	                 .setNotification(androidNotification)
	                 .build();

	         // Build FCM message with grouped chat data
	         Message message = Message.builder()
	                 .setToken(deviceToken)
	                 .setNotification(notification)
	                 .setAndroidConfig(androidConfig)
	                 .putData("type", "chat")
	                 .putData("senderId", senderId) // Group key for Android / thread-id for iOS
	                 .putData("title", senderName)
	                 .putData("allUnread", messages) // Messages joined by ||
	                 .build();

	         String response = FirebaseMessaging.getInstance().send(message);
	         return ResponseEntity.ok("Chat notification sent: " + response);

	     } catch (Exception e) {
	         e.printStackTrace();
	         return ResponseEntity.status(500)
	                 .body("Error sending chat notification: " + e.getMessage());
	     }
	 }
}

