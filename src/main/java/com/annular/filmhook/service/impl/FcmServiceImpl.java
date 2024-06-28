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
}


