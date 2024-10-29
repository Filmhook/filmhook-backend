package com.annular.filmhook.service.impl;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.controller.MarketPlaceChatController;
import com.annular.filmhook.model.Chat;
import com.annular.filmhook.model.InAppNotification;
import com.annular.filmhook.model.MarketPlaceChat;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.InAppNotificationRepository;
import com.annular.filmhook.repository.MarketPlaceChatRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.MarketPlaceChatService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.MarketPlaceChatWebModel;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class MarketPlaceChatServiceImpl implements MarketPlaceChatService{
	
	@Autowired
	UserDetails userDetails;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MediaFilesService mediaFilesService;
	
	@Autowired
	InAppNotificationRepository inAppNotificationRepository;
	
	@Autowired
	MarketPlaceChatRepository marketPlaceChatRepository;
	
	public static final Logger logger = LoggerFactory.getLogger(MarketPlaceChatServiceImpl.class);

	@Override
	public ResponseEntity<?> saveMarketPlaceChat(MarketPlaceChatWebModel marketPlaceChatWebModel) {
	    try {
	        logger.info("Save Message Method Start");

	        Integer userId = userDetails.userInfo().getId();
	        Optional<User> userOptional = userRepository.findById(userId);

	        if (userOptional.isPresent()) {
	            User user = userOptional.get();
	            MarketPlaceChat chat = MarketPlaceChat.builder()
	                    .message(marketPlaceChatWebModel.getMessage())
	                    .marketPlaceReceiverId(marketPlaceChatWebModel.getMarketPlaceReceiverId())
	                    .marketPlaceSenderId(userId)
	                    .marketType(marketPlaceChatWebModel.getMarketType())
	                    .timeStamp(new Date())
	                    .marketPlaceIsActive(true)
	                    .marketPlaceCreatedBy(userId)
	                    .marketPlaceCreatedOn(new Date())
	                    .build();
	            marketPlaceChatRepository.save(chat);

	            // Saving chat files if they exist
	            if (!Utility.isNullOrEmptyList(marketPlaceChatWebModel.getFiles())) {
	                FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
	                        .userId(marketPlaceChatWebModel.getUserId())
	                        .category(MediaFileCategory.MarketPlaceChat)
	                        .categoryRefId(chat.getMarketPlaceChatId())
	                        .files(marketPlaceChatWebModel.getFiles())
	                        .build();
	                mediaFilesService.saveMediaFiles(fileInputWebModel, user);
	            }

	            // Sending push notification if the receiver exists
	            if (marketPlaceChatWebModel.getMarketPlaceReceiverId() != null) {
	                Optional<User> receiverOptional = userRepository.findById(marketPlaceChatWebModel.getMarketPlaceReceiverId());

	                if (receiverOptional.isPresent()) {
	                    User receiver = receiverOptional.get();

	                    // Retrieve sender's and receiver's userType and review
	                    String senderUserType = user.getUserType();
	                    Float senderReview = user.getAdminReview();
	                    String receiverUserType = receiver.getUserType();
	                    Float receiverReview = receiver.getAdminReview();

	                    // Check conditions for not saving in-app notifications
	                    boolean skipNotification = false;

	                    // Condition checks based on requirements
	                    if ("Public User".equals(senderUserType) && "Public User".equals(receiverUserType)) {
	                        skipNotification = true; // both public users
	                    } else if ("Public User".equals(senderUserType) && "Industry User".equals(receiverUserType)
	                            && (receiverReview >= 1 && receiverReview <= 5)) {
	                        skipNotification = true; // sender is Public, receiver is IndustryUser with review 1-5
	                    } else if ("Industry User".equals(senderUserType) && senderReview >= 1 && senderReview <= 5
	                            && "Public User".equals(receiverUserType)) {
	                        skipNotification = true; // both are IndustryUsers with sender review 5.1-10
	                    } else if ("Industry User".equals(senderUserType) && senderReview >= 5.1 && senderReview <= 10
	                            && "Public User".equals(receiverUserType)) {
	                        skipNotification = true; // sender is IndustryUser with review 5.1-10 to PublicUser
	                    }

	                    // Proceed with notification if conditions are not met
	                    if (!skipNotification) {
	                        String notificationTitle = "filmHook";
	                        String notificationMessage = "You’ve received a message request from a " + user.getName()+"You may review their profile and response";

	                        InAppNotification inAppNotification = InAppNotification.builder()
	                                .senderId(userId)
	                                .receiverId(receiver.getUserId())
	                                .title(notificationTitle)
	                                .userType("chat")
	                                .id(chat.getMarketPlaceChatId())
	                                .message(notificationMessage)
	                                .createdOn(new Date())
	                                .isRead(true)
	                                .createdBy(userId)
	                                .build();
	                        inAppNotificationRepository.save(inAppNotification);

	                        Message message = Message.builder()
	                                .setNotification(Notification.builder().setTitle(notificationTitle)
	                                        .setBody(notificationMessage).build())
	                                .putData("chatId", Integer.toString(chat.getMarketPlaceChatId()))
	                                .setToken(receiver.getFirebaseDeviceToken())
	                                .build();

	                        try {
	                            String response = FirebaseMessaging.getInstance().send(message);
	                            logger.info("Successfully sent message: " + response);
	                        } catch (FirebaseMessagingException e) {
	                            logger.error("Failed to send push notification: " + e.getMessage());
	                        }
	                    }
	                } else {
	                    logger.warn("Receiver user not found for id: " + marketPlaceChatWebModel.getMarketPlaceReceiverId());
	                }
	            }

	            return ResponseEntity.ok(new Response(1, "Success", "Message Saved Successfully"));
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    } catch (Exception e) {
	        logger.error("Error occurred while saving message -> {}", e.getMessage());
	        return ResponseEntity.internalServerError().build();
	    }
	}



	@Override
	public ResponseEntity<?> updateMarketPlaceChat(MarketPlaceChatWebModel marketPlaceChatWebModel) {
	    try {
	        // Retrieve the chat by marketPlaceChatId
	        Optional<MarketPlaceChat> chatOptional = marketPlaceChatRepository.findById(marketPlaceChatWebModel.getMarketPlaceChatId());
	        
	        if (chatOptional.isPresent()) {
	            MarketPlaceChat chat = chatOptional.get();
	            
	            // Update the accept status and any other fields from MarketPlaceChatWebModel
	            chat.setAccept(true);
	            chat.setMarketPlaceUpdatedBy(userDetails.userInfo().getId()); // Set the updater ID
	            chat.setMarketPlaceUpdatedOn(new Date()); // Update timestamp
	            
	            // Save the updated chat entity
	            marketPlaceChatRepository.save(chat);
	            
	            return ResponseEntity.ok(new Response(1, "Success", "MarketPlaceChat updated successfully"));
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(0, "Error", "MarketPlaceChat not found"));
	        }
	    } catch (Exception e) {
	        logger.error("Error occurred while updating MarketPlaceChat -> {}", e.getMessage());
	        return ResponseEntity.internalServerError().build();
	    }
	}


}
