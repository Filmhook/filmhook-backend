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
	        // Constructing the message with notification
	        Message message = Message.builder()
	                .setNotification(Notification.builder()
	                        .setTitle("Incoming Call...")
	                        .setBody(request.getUserName() + " is calling you.")
	                        .build())
	                .setToken(request.getFCMToken())
	                .build();

	        // Sending the message
	        FirebaseMessaging.getInstance().send(message);
	        System.out.println("Successfully sent FCM message");
	    } catch (FirebaseMessagingException e) {
	        // Handling any errors that occur during message sending
	        System.err.println("Error sending FCM message: " + e.getMessage());
	        throw new RuntimeException("Error sending FCM message", e);
	    }
	}

}
