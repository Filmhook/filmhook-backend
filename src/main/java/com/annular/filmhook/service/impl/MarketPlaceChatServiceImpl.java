package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.annular.filmhook.model.MarketPlace;
import com.annular.filmhook.model.MarketPlaceChat;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.InAppNotificationRepository;
import com.annular.filmhook.repository.MarketPlaceChatRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.MarketPlaceChatService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.ChatUserWebModel;
import com.annular.filmhook.webmodel.ChatWebModel;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.MarketPlaceChatWebModel;
import com.annular.filmhook.webmodel.MarketPlaceUserWebModel;
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
	private UserService userService;
	
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
	                 // Additional check for existing notifications
	                    if (marketPlaceChatRepository.existsByMarketPlaceSenderIdAndMarketPlaceReceiverIdAndAcceptTrue(userId, receiver.getUserId())) {
	                        skipNotification = true; // Existing notification with accept = true
	                    }

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
	                        String notificationMessage = "You’ve received a message request from a " + user.getName()+ " .You may review their profile and response";

	                        InAppNotification inAppNotification = InAppNotification.builder()
	                                .senderId(userId)
	                                .receiverId(receiver.getUserId())
	                                .title(notificationTitle)
	                                .userType("marketType")
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
	            chat.setAccept(marketPlaceChatWebModel.getAccept());
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


	@Override
	public ResponseEntity<?> getMessageByUserIdAndMarketType(MarketPlaceChatWebModel message) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        // Fetch the user by receiver ID
	        User user = userRepository.findById(message.getMarketPlaceReceiverId()).orElse(null);
	        if (user == null) {
	            return ResponseEntity.ok().body(new Response(-1, "User not found", ""));
	        }

	        logger.info("Get Messages by User ID and Market Type Method Start");

	        Integer senderId = message.getMarketPlaceSenderId(); // Using senderId from the incoming message
	        Integer receiverId = message.getMarketPlaceReceiverId();
	        String marketType = message.getMarketType(); // Using marketType from the incoming message

	        // Fetch messages sent by the sender to the receiver for the specified marketType
	        List<MarketPlaceChat> senderMessages = marketPlaceChatRepository
	            .getMessageListByMarketPlaceSenderIdAndMarketPlaceReceiverIdAndMarketType(senderId, receiverId, marketType);

	        // Fetch messages received by the receiver from the sender for the specified marketType
	        List<MarketPlaceChat> receiverMessages = marketPlaceChatRepository
	            .getMessageListByMarketPlaceSenderIdAndMarketPlaceReceiverIdAndMarketType(receiverId, senderId, marketType);

	        // Combine both lists of messages and filter duplicates
	        List<MarketPlaceChat> allMessages = new ArrayList<>();
	        allMessages.addAll(senderMessages);
	        allMessages.addAll(receiverMessages);

	        // Use a Set to track seen chatIds and filter duplicates
	        Set<Integer> seenChatIds = new HashSet<>();
	        List<MarketPlaceChat> uniqueMessages = new ArrayList<>();

	        for (MarketPlaceChat chat : allMessages) {
	            if (seenChatIds.add(chat.getMarketPlaceChatId())) {
	                uniqueMessages.add(chat);
	            }
	        }

	        // Sort unique messages by marketPlaceCreatedOn in descending order
	        uniqueMessages.sort(Comparator.comparing(MarketPlaceChat::getMarketPlaceCreatedOn).reversed());

	        // Construct the response structure
	        List<MarketPlaceChatWebModel> messagesWithFiles = new ArrayList<>();
	        int senderUnreadCount = 0;
	        int receiverUnreadCount = 0;

	        for (MarketPlaceChat chat : uniqueMessages) {
	            Optional<User> userData = userRepository.findById(chat.getMarketPlaceSenderId());
	            Optional<User> receiverData = userRepository.findById(receiverId); // Fetch receiver data once

	            // Fetch profile picture URLs
	            String senderProfilePicUrl = userService.getProfilePicUrl(chat.getMarketPlaceSenderId());
	            String receiverProfilePicUrl = userService.getProfilePicUrl(receiverId); // Use receiverId

	            if (userData.isPresent() && receiverData.isPresent()) {
	                List<FileOutputWebModel> mediaFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Chat, chat.getMarketPlaceChatId());

	                MarketPlaceChatWebModel chatWebModel = MarketPlaceChatWebModel.builder()
	                        .marketPlaceChatId(chat.getMarketPlaceChatId())
	                        .marketPlaceSenderId(chat.getMarketPlaceSenderId())
	                        .marketPlaceReceiverId(chat.getMarketPlaceReceiverId())
	                        .marketPlaceIsActive(chat.getMarketPlaceIsActive())
	                        .marketPlaceCreatedBy(chat.getMarketPlaceCreatedBy())
	                        .marketPlaceCreatedOn(chat.getMarketPlaceCreatedOn())
	                        .marketPlaceUpdatedBy(chat.getMarketPlaceUpdatedBy())
	                        .marketPlaceUpdatedOn(chat.getMarketPlaceUpdatedOn())
	                        .message(chat.getMessage())
	                        .chatFiles(mediaFiles) // Use files for chat files
	                        .userId(userData.get().getUserId())
	                        .userAccountName(userData.get().getName())
	                        .receiverAccountName(receiverData.get().getName())
	                        .senderProfilePic(senderProfilePicUrl)
	                        .receiverProfilePic(receiverProfilePicUrl)
	                        .build();

	                messagesWithFiles.add(chatWebModel);
	            }
	        }

	        response.put("messages", messagesWithFiles);
	        response.put("senderUnreadCount", senderUnreadCount);
	        response.put("receiverUnreadCount", receiverUnreadCount);

	        logger.info("Get Messages by User ID and Market Type Method End");
	        return ResponseEntity.ok(new Response(1, "Success", response));
	    } catch (Exception e) {
	        logger.error("Error occurred while retrieving messages", e);
	        return ResponseEntity.internalServerError().body(new Response(-1, "Internal Server Error", ""));
	    }
	}



//	@Override
//	public ResponseEntity<?> getAllUserByMarketType(MarketPlaceChatWebModel marketPlaceChatWebModel) {
//		// TODO Auto-generated method stub
//		return null;
//	}



	@Override
	public ResponseEntity<?> getAllUserByMarketType(MarketPlaceChatWebModel marketPlaceChatWebModel) {
	    try {
	        logger.info("Get All Users Method Start");

	        Integer loggedInUserId = userDetails.userInfo().getId();
	        Integer senderId = marketPlaceChatWebModel.getMarketPlaceSenderId();
	        String marketType = marketPlaceChatWebModel.getMarketType();
	        // Validate the input data
	        if (marketPlaceChatWebModel == null || 
	            marketPlaceChatWebModel.getMarketPlaceSenderId() == null || 
	            marketPlaceChatWebModel.getMarketType() == null) {
	            return ResponseEntity.badRequest().body("Error: marketPlaceSenderId and marketType are required.");
	        }

	        // Check if the marketType exists in the database
	        if (!marketPlaceChatRepository.marketTypeExists(marketType)) {
	          //  return ResponseEntity.ok("The specified marketType does not exist.");
	            return ResponseEntity.ok(new Response(0, "Fail",Collections.EMPTY_LIST));
	        }

	        // Fetch all distinct user IDs associated with the logged-in user for the specified senderId, marketType
	        Set<Integer> chatUserIds = new HashSet<>();
	        chatUserIds.addAll(marketPlaceChatRepository.findSenderIdsByReceiverIdAndMarketType(loggedInUserId, marketType));
	        chatUserIds.addAll(marketPlaceChatRepository.findReceiverIdsBySenderIdAndMarketType(loggedInUserId, marketType));

	        // Remove the logged-in user's ID from the set to avoid self-inclusion
	        chatUserIds.remove(loggedInUserId);

	        if (chatUserIds.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }

	        // Fetch user details for the filtered user IDs
	        List<User> users = userRepository.findAllById(chatUserIds);

	        if (!users.isEmpty()) {
	            // Transform and sort the user details
	            List<MarketPlaceUserWebModel> userResponseList = this.transformUserDetailsForChat(users, senderId, loggedInUserId, marketType);
	            
	            // Sort userResponseList based on latestMsgTime in descending order
	            //userResponseList.sort(Comparator.comparing(MarketPlaceUserWebModel::getLatestMsgTime).reversed());
	         // Sort userResponseList based on latestMsgTime in descending order, treating nulls as the lowest values
	            userResponseList.sort(Comparator.comparing(MarketPlaceUserWebModel::getLatestMsgTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

	         // Create the response structure
	            Map<String, List<MarketPlaceUserWebModel>> response = new HashMap<>();
	            response.put("marketPlace", userResponseList);  // Wrap the list in a map with the key "marketPlace"

	            
	            return ResponseEntity.ok(response);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    } catch (Exception e) {
	        logger.error("Error occurred while retrieving users -> {}", e.getMessage());
	        e.printStackTrace();
	        return ResponseEntity.internalServerError().build();
	    }
	}

	// Transform user details for chat response
	private List<MarketPlaceUserWebModel> transformUserDetailsForChat(List<User> users, Integer senderId, Integer loggedInUserId, String marketType) {
	    return users.stream().map(user -> {
	        MarketPlaceUserWebModel chatUserWebModel = new MarketPlaceUserWebModel();
	        chatUserWebModel.setUserId(user.getUserId());
	        chatUserWebModel.setUserName(user.getName());
	        chatUserWebModel.setUserType(user.getUserType());
	        chatUserWebModel.setProfilePicUrl(userService.getProfilePicUrl(user.getUserId()));
	        chatUserWebModel.setMarketTypes("marketPlace");
	        
	        // Set adminReview only if userType is "Industry User"; otherwise, set it to null
	        if ("Industry User".equals(user.getUserType())) {
	            chatUserWebModel.setAdminReview(user.getAdminReview());
	        } else {
	            chatUserWebModel.setAdminReview(null);
	        }

	        // Retrieve the latest chat message based on senderId, user ID (as receiverId), and marketType
	        getLatestChatMessageFiltered(user, chatUserWebModel, senderId, loggedInUserId, marketType);

	        return chatUserWebModel;
	    }).collect(Collectors.toList());
	}

	// Retrieve the latest chat message based on senderId, receiverId, and marketType
	private void getLatestChatMessageFiltered(User user, MarketPlaceUserWebModel chatUserWebModel, Integer senderId, Integer loggedInUserId, String marketType) {
	    // Attempt to get the latest message for the specific user combination and marketType
	    List<MarketPlaceChat> latestMessages = marketPlaceChatRepository.getLatestMessage(loggedInUserId, user.getUserId(), marketType);

	    if (!latestMessages.isEmpty()) {
	        MarketPlaceChat chat = latestMessages.get(0);
	        chatUserWebModel.setLatestMsgTime(chat.getTimeStamp());
	        getLatestChatMessageContent(chat, chatUserWebModel);
	    } else {
	        chatUserWebModel.setLatestMessage("");
	        chatUserWebModel.setLatestMsgTime(null);
	    }
	
	}

	// Extract message content or media type for the latest message
	private void getLatestChatMessageContent(MarketPlaceChat chat, MarketPlaceUserWebModel chatUserWebModel) {
	    String latestMsg = "";
	    Date latestMsgTime = chat.getTimeStamp();

	    if (!Utility.isNullOrBlankWithTrim(chat.getMessage())) {
	        latestMsg = chat.getMessage();
	    } else {
	        List<FileOutputWebModel> files = mediaFilesService
	                .getMediaFilesByCategoryAndRefId(MediaFileCategory.MarketPlaceChat, chat.getMarketPlaceChatId())
	                .stream().sorted(Comparator.comparing(FileOutputWebModel::getId).reversed())
	                .collect(Collectors.toList());
	                
	        if (!Utility.isNullOrEmptyList(files)) {
	            String fileType = files.get(files.size() - 1).getFileType();
	            if (FileUtil.isImageFile(fileType))
	                latestMsg = "Photo";
	            else if (FileUtil.isVideoFile(fileType))
	                latestMsg = "Audio/Video";
	        }
	    }

	    chatUserWebModel.setLatestMessage(latestMsg);
	    chatUserWebModel.setLatestMsgTime(latestMsgTime);
	}

}
