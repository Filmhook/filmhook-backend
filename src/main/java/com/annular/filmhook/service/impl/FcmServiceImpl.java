package com.annular.filmhook.service.impl;

import java.util.HashMap;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.annular.filmhook.service.FcmService;
import com.annular.filmhook.webmodel.FCMRequestWebModel;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FcmServiceImpl implements FcmService {

    private static final Logger logger = LoggerFactory.getLogger(FcmServiceImpl.class);

    @Override
    public void sendFCMMessage(FCMRequestWebModel request) {
        try {
            // Create a Map for the data payload
            Map<String, String> dataPayload = new HashMap<>();
            dataPayload.put("fromUser", request.getUserName());
            dataPayload.put("callType", request.getCallType());
            dataPayload.put("userId", request.getUserId());
            dataPayload.put("channelNameFromNotify", request.getChannelName());
            dataPayload.put("channelToken", request.getChannelToken());
            dataPayload.put("fcm", request.getToken());

            // Constructing the message with notification and data payload
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle("Incoming Call...")
                            .setBody(request.getUserName() + " is calling you.")
                            .build())
                    .putAllData(dataPayload)
                    .setToken(request.getToken())
                    .build();

            // Sending the message
            FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent FCM message");

        } catch (FirebaseMessagingException e) {
            logger.error("Error sending FCM message: {}", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error sending FCM message", e);
        }
    }
//    @Override
//    public void sendFCMMessage(FCMRequestWebModel request) {
//        try {
//            // Data payload only (NO notification)
//            Map<String, String> dataPayload = new HashMap<>();
//            dataPayload.put("fromUser", request.getUserName());
//            dataPayload.put("callType", request.getCallType()); // video / voice
//            dataPayload.put("userId", request.getUserId());
//            dataPayload.put("channelName", request.getChannelName());
//            dataPayload.put("channelToken", request.getChannelToken());
//
//            // Android config
//            AndroidConfig androidConfig = AndroidConfig.builder()
//                    .setPriority(AndroidConfig.Priority.HIGH)
//                    .setTtl(24 * 60 * 60 * 1000) // 24 hours
////                    .setNotification(AndroidNotification.builder()
////                            .setTitle("Incoming Call")
////                            .setBody(request.getUserName() + " is calling you")
////                            .setChannelId("calls") // channel must exist on Android app
////                            .setClickAction("OPEN_CALL_ACTIVITY")
////                            .build())
//                    .build();
//
//            // iOS / APNs config
//            ApnsConfig apnsConfig = ApnsConfig.builder()
//                    .putHeader("apns-priority", "10")
//                    .setAps(Aps.builder()
//                            .setContentAvailable(true) // background fetch
//                            .build())
//                    .build();
//
//            // Build message
//            Message message = Message.builder()
//                    .putAllData(dataPayload)
//                    .setToken(request.getToken())
//                    .setAndroidConfig(androidConfig)
//                    .setApnsConfig(apnsConfig)
//                    .build();
//
//            FirebaseMessaging.getInstance().send(message);
//            logger.info("FCM call notification sent successfully to {}", request.getUserId());
//
//        } catch (FirebaseMessagingException e) {
//            logger.error("Error sending FCM message: {}", e.getMessage());
//            e.printStackTrace();
//        }
//    }
}


