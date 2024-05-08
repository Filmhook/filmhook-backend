package com.annular.filmhook.service.impl;

import java.util.HashMap;


import java.util.Map;

import org.springframework.stereotype.Service;

import com.annular.filmhook.service.FcmService;
import com.annular.filmhook.webmodel.FCMRequestWebModel;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;


@Service
public class FcmServiceImpl implements FcmService {

	@Override
	 public void sendFCMMessage(FCMRequestWebModel request) {
        try {
            // Create a Map for the data payload
        	System.out.println("kkkkkkkkkkkkkk");
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
            System.out.println("Successfully sent FCM message");

        } catch (FirebaseMessagingException e) {
            // Log full stack trace
            e.printStackTrace();
            System.err.println("Error sending FCM message: " + e.getMessage());
            throw new RuntimeException("Error sending FCM message", e);
        }
    }
	}


