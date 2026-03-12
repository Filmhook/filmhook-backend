package com.annular.filmhook.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.CallLog;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.FcmService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.FCMRequestWebModel;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FcmServiceImpl implements FcmService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userServices;

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
					.setNotification(Notification.builder().setTitle("Incoming Call...")
							.setBody(request.getUserName() + " is calling you.").build())
					.putAllData(dataPayload).setToken(request.getToken()).build();

			// Sending the message
			FirebaseMessaging.getInstance().send(message);
			logger.info("Successfully sent FCM message");

		} catch (FirebaseMessagingException e) {
			logger.error("Error sending FCM message: {}", e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Error sending FCM message", e);
		}
	}
	// @Override
	// public void sendFCMMessage(FCMRequestWebModel request) {
	// try {
	// // Data payload only (NO notification)
	// Map<String, String> dataPayload = new HashMap<>();
	// dataPayload.put("fromUser", request.getUserName());
	// dataPayload.put("callType", request.getCallType()); // video / voice
	// dataPayload.put("userId", request.getUserId());
	// dataPayload.put("channelName", request.getChannelName());
	// dataPayload.put("channelToken", request.getChannelToken());
	//
	// // Android config
	// AndroidConfig androidConfig = AndroidConfig.builder()
	// .setPriority(AndroidConfig.Priority.HIGH)
	// .setTtl(24 * 60 * 60 * 1000) // 24 hours
	//// .setNotification(AndroidNotification.builder()
	//// .setTitle("Incoming Call")
	//// .setBody(request.getUserName() + " is calling you")
	//// .setChannelId("calls") // channel must exist on Android app
	//// .setClickAction("OPEN_CALL_ACTIVITY")
	//// .build())
	// .build();
	//
	// // iOS / APNs config
	// ApnsConfig apnsConfig = ApnsConfig.builder()
	// .putHeader("apns-priority", "10")
	// .setAps(Aps.builder()
	// .setContentAvailable(true) // background fetch
	// .build())
	// .build();
	//
	// // Build message
	// Message message = Message.builder()
	// .putAllData(dataPayload)
	// .setToken(request.getToken())
	// .setAndroidConfig(androidConfig)
	// .setApnsConfig(apnsConfig)
	// .build();
	//
	// FirebaseMessaging.getInstance().send(message);
	// logger.info("FCM call notification sent successfully to {}",
	// request.getUserId());
	//
	// } catch (FirebaseMessagingException e) {
	// logger.error("Error sending FCM message: {}", e.getMessage());
	// e.printStackTrace();
	// }
	// }

	@Override
	public void sendIncomingCallNotification(Integer callerId, Integer receiverId, String callType, String channelName,
			String deviceToken, String callerName, String callerPicUrl) {

		try {

			// Notification title and body (displayed to user)
			Notification notification = Notification.builder().setTitle("Incoming " + callType + " call")
					.setBody(callerName + " is calling you").build();

			// Payload sent to the device
			Message message = Message.builder()
					// .setNotification(notification)
					.putData("type", "incoming_call").putData("callerId", callerId.toString())
					.putData("receiverId", receiverId.toString()).putData("callerName", callerName)
					.putData("callerPic", callerPicUrl).putData("callType", callType)
					.putData("channelName", channelName)
					.putData("timestamp", String.valueOf(System.currentTimeMillis())).setToken(deviceToken).build();

			// Send async
			FirebaseMessaging.getInstance().sendAsync(message)
					.addListener(() -> System.out.println("FCM Sent to: " + deviceToken), Runnable::run);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error sending FCM: " + e.getMessage());
		}
	}

	/* -------------------- Call End / Reject ---------------------- */
	@Override
	public void sendCallStatusNotification(CallLog log, Integer userId, String status, String token) {

		Message msg = Message.builder().putData("type", "call_status").putData("status", status)
				.putData("channelName", log.getChannelName()).putData("callerId", log.getCallerId().toString())
				.putData("receiverId", log.getReceiverId().toString()).setToken(token).build();

		FirebaseMessaging.getInstance().sendAsync(msg);
	}

	@Override
	public void sendGroupCallNotification(Integer hostId, Integer receiverId, String callType, String channelName,
			String deviceToken, String hostName, String hostPic, String groupNames, Integer groupId) {
		try {
			Message message = Message.builder().setToken(deviceToken).putData("type", "incoming_group_call")
					.putData("fromUserId", hostId.toString()).putData("receiverId", receiverId.toString())
					.putData("callType", callType).putData("channelName", channelName).putData("callerName", hostName)
					.putData("callerPic", hostPic != null ? hostPic : "").putData("title", "Group Call")
					.putData("body", groupNames + " invited you to a group " + callType + " call")
					.putData("groupUsers", groupNames).putData("groupId", groupId.toString()).build();

			FirebaseMessaging.getInstance().send(message);

			System.out.println("FCM → Group call sent to: " + receiverId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendMissedCallNotification(
	        Integer callerId,
	        Integer receiverId,
	        String callerName,
	        String callerPic,
	        String token) {

	    try {

	        Message message = Message.builder()
	                .setToken(token)
	                .putData("type", "MISSED_CALL")
	                .putData("callerId", callerId.toString())
	                .putData("callerName", callerName)
	                .putData("callerPic", callerPic)
	                .build();

	        FirebaseMessaging.getInstance().send(message);

	        System.out.println("FCM → Missed call notification sent to user: " + receiverId);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@Override
	public void sendMissedGroupCallNotification(
	        Integer hostId,
	        Integer receiverId,
	        String callType,
	        String channelName,
	        String deviceToken,
	        String hostName,
	        String hostPic,
	        String groupNames,
	        Integer groupId) {

	    try {

	        Message message = Message.builder()
	                .setToken(deviceToken)
	                .putData("type", "missed_group_call")
	                .putData("fromUserId", hostId.toString())
	                .putData("receiverId", receiverId.toString())
	                .putData("callType", callType)
	                .putData("channelName", channelName)
	                .putData("callerName", hostName)
	                .putData("callerPic", hostPic != null ? hostPic : "")
	                .putData("title", "Missed Group Call")
	                .putData("body", "You missed a group " + callType + " call from " + groupNames)
	                .putData("groupUsers", groupNames)
	                .putData("groupId", groupId.toString())
	                .build();

	        FirebaseMessaging.getInstance().send(message);

	        System.out.println("FCM → Missed group call sent to: " + receiverId);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
