package com.annular.filmhook.service.impl;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.annular.filmhook.configuration.FirebaseConfig;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;

import com.annular.filmhook.model.Chat;
import com.annular.filmhook.model.ChatMediaDeleteTracker;
import com.annular.filmhook.model.InAppNotification;
import com.annular.filmhook.model.MarketPlaceChat;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.ShootingLocationChat;
import com.annular.filmhook.model.Story;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.ChatMediaDeleteTrackerRepository;
import com.annular.filmhook.repository.ChatRepository;
import com.annular.filmhook.repository.InAppNotificationRepository;
import com.annular.filmhook.repository.MarketPlaceChatRepository;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.ShootingLocationChatRepository;
import com.annular.filmhook.repository.StoryRepository;
import com.annular.filmhook.repository.UserRepository;

import com.annular.filmhook.service.ChatService;
import com.annular.filmhook.service.MediaFilesService;

import com.annular.filmhook.util.Utility;

import com.annular.filmhook.webmodel.ChatWebModel;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.InAppNotificationWebModel;
import com.annular.filmhook.webmodel.UserWebModel;
import com.annular.filmhook.webmodel.ChatUserWebModel;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class ChatServiceImpl implements ChatService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	ChatRepository chatRepository;

	@Autowired
	MediaFilesService mediaFilesService;

	@Autowired
	UserDetails userDetails;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
	@Autowired
	private UserService userService;

	@Autowired
	MarketPlaceChatRepository marketPlaceChatRepository;
	
	@Autowired
	InAppNotificationRepository inAppNotificationRepository;

	@Autowired
	FirebaseConfig firebaseConfig;
	
	@Autowired
	ShootingLocationChatRepository shootingLocationChatRepository;
	
	@Autowired
	MediaFilesRepository mediaFilesRepository;
	
	@Autowired
    StoryRepository storyRepository;
	
	@Autowired
	 ChatMediaDeleteTrackerRepository chatMediaDeleteTrackerRepository;

	public static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);

//    @Override
//    public ResponseEntity<?> saveMessage(ChatWebModel chatWebModel) {
//        try {
//            logger.info("Save Message Method Start");
//
//            Integer userId = userDetails.userInfo().getId();
//            Optional<User> userOptional = userRepository.findById(userId);
//
//            if (userOptional.isPresent()) {
//                User user = userOptional.get();
//                Chat chat = Chat.builder()
//                        .message(chatWebModel.getMessage())
//                        .chatReceiverId(chatWebModel.getChatReceiverId())
//                        .userAccountName(user.getName())
//                        .chatSenderId(userId)
//                        .userType(user.getUserType())
//                        .timeStamp(new Date())
//                        .chatIsActive(true)
//                        .chatCreatedBy(userId)
//                        .senderRead(true)
//                        .receiverRead(false)
//                        .chatCreatedOn(new Date())
//                        .build();
//                chatRepository.save(chat);
//
//                if (!Utility.isNullOrEmptyList(chatWebModel.getFiles())) {
//                    // Saving the chat files in the media_files table
//                    FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
//                            .userId(chatWebModel.getUserId())
//                            .category(MediaFileCategory.Chat)
//                            .categoryRefId(chat.getChatId())
//                            .files(chatWebModel.getFiles())
//                            .build();
//                    mediaFilesService.saveMediaFiles(fileInputWebModel, userOptional.get());
//                }
//                return ResponseEntity.ok(new Response(1, "Success", "Message Saved Successfully"));
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            logger.error("Error occurred while saving message -> {}", e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//    }
	@Override
	public ResponseEntity<?> saveMessage(ChatWebModel chatWebModel) {
	    try {
	        logger.info("Save Message Method Start");

	        Integer userId = userDetails.userInfo().getId();
	        Optional<User> userOptional = userRepository.findById(userId);

	        if (userOptional.isPresent()) {
	            User user = userOptional.get();

	            Chat chat = Chat.builder()
	                .message(chatWebModel.getMessage())
	                .chatReceiverId(chatWebModel.getChatReceiverId())
	                .userAccountName(user.getName())
	                .chatSenderId(userId)
	                .userType(user.getUserType())
	                .timeStamp(new Date())
	                .chatIsActive(true)
	                .chatCreatedBy(userId)
	                .senderRead(true)
	                .receiverRead(false)
	                .chatCreatedOn(new Date())
	                .storyId(chatWebModel.getStoryId())
	                .replyType(chatWebModel.getStoryId() != null ? "story" : "normal")
	                .build();

	            chatRepository.save(chat);

	            if (!Utility.isNullOrEmptyList(chatWebModel.getFiles())) {
	                FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
	                    .userId(chatWebModel.getUserId())
	                    .category(MediaFileCategory.Chat)
	                    .categoryRefId(chat.getChatId())
	                    .files(chatWebModel.getFiles())
	                    .build();

	                mediaFilesService.saveMediaFiles(fileInputWebModel, user);
	            }

//	            // Push notification
//	            if (chatWebModel.getChatReceiverId() != null) {
//	                Optional<User> receiverOptional = userRepository.findById(chatWebModel.getChatReceiverId());
//
//	                if (receiverOptional.isPresent()) {
//	                    User receiver = receiverOptional.get();
//
//	                    String notificationTitle = "filmHook";
//	                    String notificationMessage;
//	                    if (chatWebModel.getStoryId() != null) {
//	                        notificationMessage = user.getName() + " replied to your story";
//	                    } else {
//	                        notificationMessage = "You have a new message from " + user.getName();
//	                    }
//
//
//	                    InAppNotification inAppNotification = InAppNotification.builder()
//	                        .senderId(userId)
//	                        .receiverId(receiver.getUserId())
//	                        .title(notificationTitle)
//	                        .userType(chatWebModel.getStoryId() != null ? "story_reply" : "chat")
//	                        .id(chat.getChatId())
//	                        .message(notificationMessage)
//	                        .createdOn(new Date())
//	                        .isRead(true)
//	                        .createdBy(userId)
//	                        .build();
//
//	                    inAppNotificationRepository.save(inAppNotification);
//
//	                    String deviceToken = receiver.getFirebaseDeviceToken();
//	                    if (deviceToken != null && !deviceToken.trim().isEmpty()) {
//	                        try {
//	                            Message message = Message.builder()
//	                                .setNotification(Notification.builder()
//	                                    .setTitle(notificationTitle)
//	                                    .setBody(notificationMessage)
//	                                    .build())
//	                                .putData("chatId", Integer.toString(chat.getChatId()))
//	                                .setToken(deviceToken)
//	                                .build();
//
//	                            String response = FirebaseMessaging.getInstance().send(message);
//	                            logger.info("Successfully sent push notification: " + response);
//	                        } catch (FirebaseMessagingException e) {
//	                            logger.error("Failed to send push notification", e);
//	                        }
//	                    } else {
//	                        logger.warn("Device token is null or empty for user ID: " + receiver.getUserId());
//	                    }
//
//	                } else {
//	                    logger.warn("Receiver user not found for id: " + chatWebModel.getChatReceiverId());
//	                }
//	            }

	            return ResponseEntity.ok(new Response(1, "Success", "Message Saved Successfully"));
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(0, "Failed", "Sender user not found"));
	        }
	    } catch (Exception e) {
	        logger.error("Error occurred while saving message", e);
	        return ResponseEntity.internalServerError().body(new Response(0, "Failed", "An error occurred while saving message"));
	    }
	}

	// Full Java code for getAllUser in ChatServiceImpl

	@Override
	public ResponseEntity<?> getAllUser() {
	    try {
	        logger.info("Get All Users Method Start");
	        Integer loggedInUserId = userDetails.userInfo().getId();

	        // Fetch all chats involving the logged-in user
	        List<Chat> allChats = chatRepository.findAllChatsByUserId(loggedInUserId);
	        Set<Integer> chatUserIds = new HashSet<>();

	        for (Chat chat : allChats) {
	            if (chat.getChatSenderId().equals(loggedInUserId) && Boolean.TRUE.equals(chat.getDeletedBySender())) {
	                continue;
	            }
	            if (chat.getChatReceiverId().equals(loggedInUserId) && Boolean.TRUE.equals(chat.getDeletedByReceiver())) {
	                continue;
	            }
	            // Add the other user's ID
	            if (chat.getChatSenderId().equals(loggedInUserId)) {
	                chatUserIds.add(chat.getChatReceiverId());
	            } else {
	                chatUserIds.add(chat.getChatSenderId());
	            }
	        }

	        if (chatUserIds.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }

	        // Fetch user details
	        List<User> users = userRepository.findAllById(chatUserIds);
	        if (!users.isEmpty()) {
	            List<ChatUserWebModel> userResponseList = transformUserDetailsForChat(users, loggedInUserId);

	            userResponseList.sort(
	                Comparator.comparing(ChatUserWebModel::getLatestMsgTime, Comparator.nullsLast(Date::compareTo)).reversed()
	            );

	            return ResponseEntity.ok(userResponseList);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    } catch (Exception e) {
	        logger.error("Error occurred while retrieving users -> {}", e.getMessage());
	        return ResponseEntity.internalServerError().build();
	    }
	}

	private List<ChatUserWebModel> transformUserDetailsForChat(List<User> users, Integer loggedInUserId) {
	    return users.stream().map(user -> {
	        ChatUserWebModel chatUserWebModel = new ChatUserWebModel();
	        chatUserWebModel.setUserId(user.getUserId());
	        chatUserWebModel.setUserName(user.getName());
	        chatUserWebModel.setUserType(user.getUserType());
	        chatUserWebModel.setAdminReview(user.getAdminReview());
	        chatUserWebModel.setProfilePicUrl(userService.getProfilePicUrl(user.getUserId()));
	        chatUserWebModel.setOnlineStatus(user.getOnlineStatus());

	        getLatestChatMessage(user, chatUserWebModel, loggedInUserId);

	        int unreadCount = chatRepository.countUnreadMessages(user.getUserId(), loggedInUserId);
	        chatUserWebModel.setReceiverUnreadCount(unreadCount);

	        return chatUserWebModel;
	    }).collect(Collectors.toList());
	}

	public void getLatestChatMessage(User user, ChatUserWebModel chatUserWebModel, Integer loggedInUserId) {
	    String latestMsg = "";
	    Date latestMsgTime = null;
	    boolean isLatestStory = false;
	    try {
	        Optional<Chat> lastChat = chatRepository.getLatestMessage(loggedInUserId, user.getUserId());

	        if (lastChat.isPresent()) {
	            Chat chat = lastChat.get();
	            if (Boolean.TRUE.equals(chat.getIsDeletedForEveryone())) {
	                latestMsg = "This message was deleted";
	            } else if ("story".equalsIgnoreCase(chat.getReplyType())) {	               
	                isLatestStory = true;
	                latestMsg = chat.getMessage();
	            } else if(!Utility.isNullOrBlankWithTrim(chat.getMessage())) {
	                latestMsg = chat.getMessage();
	            } else {
	                List<FileOutputWebModel> files = mediaFilesService
	                        .getMediaFilesByCategoryAndRefId(MediaFileCategory.Chat, chat.getChatId())
	                        .stream().sorted(Comparator.comparing(FileOutputWebModel::getId).reversed())
	                        .collect(Collectors.toList());

	                if (!files.isEmpty()) {
	                    String fileType = files.get(0).getFileType();
	                    if (FileUtil.isImageFile(fileType)) {
	                        latestMsg = "Photo";
	                    } else if (FileUtil.isVideoFile(fileType)) {
	                        latestMsg = "Audio/Video";
	                    } else {
	                        latestMsg = "Attachment";
	                    }
	                }
	            }

	            latestMsgTime = chat.getTimeStamp();
	        }

	    } catch (Exception e) {
	        logger.error("Error while getting latest chat message -> {}", e.getMessage());
	    }

	    chatUserWebModel.setLatestMessage(latestMsg);
	    chatUserWebModel.setLatestMsgTime(latestMsgTime);
	    chatUserWebModel.setIsLatestStory(isLatestStory);
	}

//	@Override
//	public ResponseEntity<?> getMessageByUserId(ChatWebModel message) {
//	    Map<String, Object> response = new HashMap<>();
//	    try {
//	        // Fetch the user by receiver ID
//	        User user = userRepository.findById(message.getChatReceiverId()).orElse(null);
//	        if (user == null) {
//	            return ResponseEntity.ok().body(new Response(-1, "User not found", ""));
//	        }
//
//	        logger.info("Get Messages by User ID Method Start");
//
//	        // Fetch sender and receiver IDs
//	        Integer senderId = userDetails.userInfo().getId();
//	        Integer receiverId = message.getChatReceiverId();
//
//	        // Define the pagination parameters
//	        Pageable paging = PageRequest.of(message.getPageNo() - 1, message.getPageSize(), Sort.by("chatCreatedOn").descending());
//
//	        // Fetch messages sent by the current user to the receiver with pagination
//	        Page<Chat> senderMessages = chatRepository.getMessageListBySenderIdAndReceiverId(senderId, receiverId, paging);
//
//	        // Fetch messages received by the current user from the receiver with pagination
//	        Page<Chat> receiverMessages = chatRepository.getMessageListBySenderIdAndReceiverId(receiverId, senderId, paging);
//
//	        // Combine both lists of messages without duplicates
//	        Set<Chat> allMessages = new HashSet<>();
//	        allMessages.addAll(senderMessages.getContent());
//	        allMessages.addAll(receiverMessages.getContent());
//
//	        // Construct the response structure
//	        List<ChatWebModel> messagesWithFiles = new ArrayList<>();
//	        int senderUnreadCount = 0; // Initialize sender unread messages count
//	        int receiverUnreadCount = 0; // Initialize receiver unread messages count
//	        for (Chat chat : allMessages) {
//	            Optional<User> userData = userRepository.findById(chat.getChatSenderId());
//	            Optional<User> userDatas = userRepository.findById(receiverId);
//
//	            // Fetch profile picture URLs
//	            String senderProfilePicUrl = userService.getProfilePicUrl(chat.getChatSenderId());
//	            String receiverProfilePicUrl = userService.getProfilePicUrl(chat.getChatReceiverId());
//
//	            if (userData.isPresent()) {
//	                List<FileOutputWebModel> mediaFiles = mediaFilesService
//	                        .getMediaFilesByCategoryAndRefId(MediaFileCategory.Chat, chat.getChatId());
//	                ChatWebModel chatWebModel = ChatWebModel.builder().chatId(chat.getChatId())
//	                        .chatSenderId(chat.getChatSenderId()).chatReceiverId(chat.getChatReceiverId())
//	                        .chatIsActive(chat.getChatIsActive()).chatCreatedBy(chat.getChatCreatedBy())
//	                        .chatCreatedOn(chat.getChatCreatedOn()).senderProfilePic(senderProfilePicUrl) 
//	                        .receiverProfilePic(receiverProfilePicUrl)
//	                        .chatUpdatedBy(chat.getChatUpdatedBy()).chatUpdatedOn(chat.getChatUpdatedOn())
//	                        .receiverRead(chat.getReceiverRead())
//	                        .senderRead(chat.getSenderRead()).chatFiles(mediaFiles).message(chat.getMessage())
//	                        .userType(userData.get().getUserType()).userAccountName(userData.get().getName())
//	                        .receiverAccountName(userDatas.get().getName()).userId(userData.get().getUserId()).build();
//
//	                // Update read status if the current user is the receiver
//	                if (chat.getChatReceiverId().equals(senderId) && !chat.getReceiverRead()) {
//	                    receiverUnreadCount++; 
//	                    chat.setReceiverRead(true);
//	                    chatRepository.save(chat);
//	                }
//	                if (!chat.getSenderRead()) {
//	                    senderUnreadCount++;
//	                }
//
//	                messagesWithFiles.add(chatWebModel);
//	            }
//	        }
//
////	        // Sort messagesWithFiles by chatId in descending order
////	        messagesWithFiles.sort(Comparator.comparing(ChatWebModel::getChatId).reversed());
//
//	        // Put the final response together
//	        response.put("userChat", messagesWithFiles);
//	        response.put("numberOfItems", messagesWithFiles.size());
//	        response.put("currentPage", message.getPageNo());
//	        response.put("totalPages", senderMessages.getTotalPages());
//
//	        logger.info("Get Messages by User ID Method End");
//	        return ResponseEntity.ok(new Response(1, "Success", response));
//	    } catch (Exception e) {
//	        logger.error("Error occurred while retrieving messages -> {}", e.getMessage());
//	        return ResponseEntity.internalServerError().body(new Response(-1, "Internal Server Error", ""));
//	    }
	//	}
		@Override
		public ResponseEntity<?> getMessageByUserId(ChatWebModel message) {
		    Map<String, Object> response = new HashMap<>();
		    try {
		        // Fetch the user by receiver ID
		        User user = userRepository.findById(message.getChatReceiverId()).orElse(null);
		        if (user == null) {
		            return ResponseEntity.ok().body(new Response(-1, "User not found", ""));
		        }
	
		        logger.info("Get Messages by User ID Method Start");
	
		        // Fetch sender and receiver IDs
		        Integer senderId = userDetails.userInfo().getId();
		        Integer receiverId = message.getChatReceiverId();
	
		        // Fetch messages sent by the current user to the receiver
		        List<Chat> senderMessages = chatRepository.getMessageListBySenderIdAndReceiverId(senderId, receiverId);
	
		        // Fetch messages received by the current user from the receiver
		        List<Chat> receiverMessages = chatRepository.getMessageListBySenderIdAndReceiverId(receiverId, senderId);
	
		        // Combine both lists of messages
		 
		        List<Chat> allMessages = new ArrayList<>();
		        for (Chat c : senderMessages) {
		            if (Boolean.TRUE.equals(c.getIsDeletedForEveryone()) || !Boolean.TRUE.equals(c.getDeletedBySender())) {
		                allMessages.add(c);
		            }
		        }

		        for (Chat c : receiverMessages) {
		            if (Boolean.TRUE.equals(c.getIsDeletedForEveryone()) || !Boolean.TRUE.equals(c.getDeletedByReceiver())) {
		                allMessages.add(c);
		            }
		        }
	
	
		        // Sort combined messages by chatCreatedOn in descending order
		        allMessages.sort(Comparator.comparing(Chat::getChatCreatedOn).reversed());
	
		        // Use a Set to track seen chatIds and filter duplicates
		        Set<Integer> seenChatIds = new HashSet<>();
		        List<Chat> uniqueMessages = new ArrayList<>();
		        for (Chat chat : allMessages) {
		            if (!seenChatIds.contains(chat.getChatId())) {
		                seenChatIds.add(chat.getChatId());
		                uniqueMessages.add(chat);
		            }
		        }
	
		        // Adjust pagination to accumulate messages from page 1 to the current page
		        int end = Math.min(message.getPageNo() * message.getPageSize(), uniqueMessages.size());
		        List<Chat> paginatedMessages = uniqueMessages.subList(0, end);
	
		        // Construct the response structure
		        List<ChatWebModel> messagesWithFiles = new ArrayList<>();
		        int senderUnreadCount = 0;
		        int receiverUnreadCount = 0;
		        for (Chat chat : paginatedMessages) {
		        	if (!Boolean.TRUE.equals(chat.getIsDeletedForEveryone()) &&
		        		    ((chat.getChatSenderId().equals(senderId) && Boolean.TRUE.equals(chat.getDeletedBySender())) ||
		        		     (chat.getChatReceiverId().equals(senderId) && Boolean.TRUE.equals(chat.getDeletedByReceiver())))) {
		        		    continue;
		        		}
		            Optional<User> userData = userRepository.findById(chat.getChatSenderId());
		            Optional<User> userDatas = userRepository.findById(receiverId);
	
		            // Fetch profile picture URLs
		            String senderProfilePicUrl = userService.getProfilePicUrl(chat.getChatSenderId());
		            String receiverProfilePicUrl = userService.getProfilePicUrl(chat.getChatReceiverId());
	
		            if (userData.isPresent()) {
		                List<FileOutputWebModel> mediaFiles = mediaFilesService
		                        .getMediaFilesByCategoryAndRefId(MediaFileCategory.Chat, chat.getChatId());
		                
		                FileOutputWebModel storyMedia = null;
		                if (chat.getStoryId() != null) {
		                
							Story story = storyRepository.findByStoryId(chat.getStoryId());
		                    if (story != null && story.getId() != null) {
		                        List<FileOutputWebModel> storyFiles = mediaFilesService
		                            .getMediaFilesByCategoryAndRefId(MediaFileCategory.Stories, story.getId());

		                        if (!storyFiles.isEmpty()) {
		                            storyMedia = storyFiles.get(0); 
		                        }
		                    }
		                }
		                // Set deleted message text if isDeletedForEveryone = true
		                String finalMessage = Boolean.TRUE.equals(chat.getIsDeletedForEveryone())
		                        ? "🚫 This message was deleted"
		                        : chat.getMessage();
		                
		                       ChatWebModel chatWebModel = ChatWebModel.builder().chatId(chat.getChatId())
		                        .chatSenderId(chat.getChatSenderId()).chatReceiverId(chat.getChatReceiverId())
		                        .chatIsActive(chat.getChatIsActive()).chatCreatedBy(chat.getChatCreatedBy())
		                        .chatCreatedOn(chat.getChatCreatedOn()).senderProfilePic(senderProfilePicUrl)
		                        .receiverProfilePic(receiverProfilePicUrl)
		                        .chatUpdatedBy(chat.getChatUpdatedBy()).chatUpdatedOn(chat.getChatUpdatedOn())
		                        .receiverRead(chat.getReceiverRead()).senderRead(chat.getSenderRead())
		                        .chatFiles(mediaFiles).message(finalMessage)
		                        .userType(userData.get().getUserType()).userAccountName(userData.get().getName())
		                        .receiverAccountName(userDatas.get().getName()).userId(userData.get().getUserId())
		                        .storyId(chat.getStoryId())     
		                        .storyMediaUrl(storyMedia != null ? storyMedia.getFilePath() : null)
		                        .storyMediaType(storyMedia != null ? storyMedia.getFileType() : null)
		                        .replyType(chat.getReplyType())
		                        .isDeletedForEveryone(chat.getIsDeletedForEveryone())
		                        .build();
		                
		                
	
		                // Update read status if the current user is the receiver
		                if (chat.getChatReceiverId().equals(senderId) && !chat.getReceiverRead()) {
		                    receiverUnreadCount++;
		                    chat.setReceiverRead(true);
		                    chatRepository.save(chat);
		                }
		                if (!chat.getSenderRead()) {
		                    senderUnreadCount++;
		                }
	
		                messagesWithFiles.add(chatWebModel);
		            }
		        }
	
		        // Put the final response together
		        response.put("userChat", messagesWithFiles);
		        response.put("numberOfItems", messagesWithFiles.size());
		        response.put("currentPage", message.getPageNo());
		        response.put("totalPages", (int) Math.ceil((double) uniqueMessages.size() / message.getPageSize()));
	
		        logger.info("Get Messages by User ID Method End");
		        return ResponseEntity.ok(new Response(1, "Success", response));
		    } catch (Exception e) {
		        logger.error("Error occurred while retrieving messages -> {}", e.getMessage());
		        return ResponseEntity.internalServerError().body(new Response(-1, "Internal Server Error", ""));
		    }
		}



//    @Override
//    public ResponseEntity<?> getMessageByUserId(ChatWebModel message) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            // Fetch the user by receiver ID
//            User user = userRepository.findById(message.getChatReceiverId()).orElse(null);
//            if (user == null) {
//                return ResponseEntity.ok().body(new Response(-1, "User not found", ""));
//            }
//
//            logger.info("Get Messages by User ID Method Start");
//
//            // Fetch sender and receiver IDs
//            Integer senderId = userDetails.userInfo().getId();
//            Integer receiverId = message.getChatReceiverId();
//
//            // Fetch messages sent by the current user to the receiver
//            List<Chat> senderMessages = chatRepository.getMessageListBySenderIdAndReceiverId(senderId, receiverId);
//
//            // Fetch messages received by the current user from the receiver
//            List<Chat> receiverMessages = chatRepository.getMessageListBySenderIdAndReceiverId(receiverId, senderId);
//
//            // Combine both lists of messages without duplicates
//            Set<Chat> allMessages = new HashSet<>();
//            allMessages.addAll(senderMessages);
//            allMessages.addAll(receiverMessages);
//
//            // Construct the response structure
//            List<ChatWebModel> messagesWithFiles = new ArrayList<>();
//            int unreadCount = 0; // Initialize unread messages count
//            for (Chat chat : allMessages) {
//                Optional<User> userData = userRepository.findById(chat.getChatSenderId());
//                Optional<User> userDatas = userRepository.findById(receiverId);
//                // Fetch profile picture URLs
//                String senderProfilePicUrl = userService.getProfilePicUrl(chat.getChatSenderId());
//                String receiverProfilePicUrl = userService.getProfilePicUrl(chat.getChatReceiverId());
//
//                if (userData.isPresent()) {
//                    List<FileOutputWebModel> mediaFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Chat, chat.getChatId());
//                    ChatWebModel chatWebModel = ChatWebModel.builder()
//                            .chatId(chat.getChatId())
//                            .chatSenderId(chat.getChatSenderId())
//                            .chatReceiverId(chat.getChatReceiverId())
//                            .chatIsActive(chat.getChatIsActive())
//                            .chatCreatedBy(chat.getChatCreatedBy())
//                            .chatCreatedOn(chat.getChatCreatedOn())
//                            .senderProfilePic(senderProfilePicUrl) // Set sender profile pic URL
//                            .receiverProfilePic(receiverProfilePicUrl) // Set receiver profile pic URL
//                            .chatUpdatedBy(chat.getChatUpdatedBy())
//                            .chatUpdatedOn(chat.getChatUpdatedOn())
//                            .receiverRead(true)
//                            .senderRead(chat.getSenderRead())
//                            .chatFiles(mediaFiles)
//                            .message(chat.getMessage())
//                            .userType(userData.get().getUserType())
//                            .userAccountName(userData.get().getName())
//                            .receiverAccountName(userDatas.get().getName())
//                            .userId(userData.get().getUserId())
//                            .build();
//                    
//                    if (!chat.getReceiverRead()) {
//                        unreadCount++; // Increment count if receiver hasn't read the message
//                    }
//                    messagesWithFiles.add(chatWebModel);
//                }
//            }
//
//            // Sort messagesWithFiles by chatId
//            messagesWithFiles.sort(Comparator.comparing(ChatWebModel::getChatId));
//
//            // Put the final response together
//            response.put("userChat", messagesWithFiles);
//            response.put("numberOfItems", messagesWithFiles.size());
//            response.put("unreadCount", unreadCount);
//            logger.info("Get Messages by User ID Method End");
//            return ResponseEntity.ok(new Response(1, "Success", response));
//        } catch (Exception e) {
//            logger.error("Error occurred while retrieving messages -> {}", e.getMessage());
//            return ResponseEntity.internalServerError().body(new Response(-1, "Internal Server Error", ""));
//        }
//    }

	@Override
	public ResponseEntity<?> getFirebaseTokenByUserId(Integer userId) {
		try {
			Optional<User> userOptional = userRepository.findById(userId);
			if (userOptional.isPresent()) {
				String firebaseToken = userOptional.get().getFirebaseDeviceToken();
				if (firebaseToken != null) {
					return ResponseEntity.ok(new Response(1, "Success", firebaseToken));
				} else {
					return ResponseEntity.ok().body("Firebase token not found for the user.");
				}
			} else {
				return ResponseEntity.ok().body("User not found with ID: " + userId);
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
					.body("Error occurred while retrieving Firebase token: " + e.getMessage());
		}
	}

	@Override
	public Response getLastMessageById(ChatWebModel message) {
		try {
			List<Chat> lastMessages = chatRepository.findTopByChatSenderIdAndChatReceiverIdOrderByTimeStampDesc(
					message.getChatSenderId(), message.getChatReceiverId());
			logger.info("message sender {}", message.getChatSenderId());
			logger.info("message receiver {}", message.getChatReceiverId());
			if (!lastMessages.isEmpty()) {
				Chat lastMessage = lastMessages.get(0);
				Map<String, Object> response = new HashMap<>();
				response.put("lastMessage", lastMessage);
				return new Response(1, "Success", response);
			} else {
				return new Response(-1, "No messages found between the sender and receiver", null);
			}
		} catch (Exception e) {
			logger.error("getLastMessageById Method Exception {}", e.getMessage());
			e.printStackTrace();
			return new Response(-1, "Error", e.getMessage());
		}
	}

//	@Override
//	public Response getAllSearchByChat(String searchKey) {
//		try {
//			List<User> users = userRepository.findByNameContainingIgnoreCaseAndStatus(searchKey, true);
//			if (!Utility.isNullOrEmptyList(users)) {
//				List<ChatUserWebModel> responseList = this.transformsUserDetailsForChat(users);
//				return new Response(1, "Success", responseList);
//			} else {
//				return new Response(-1, "User not found...", null);
//			}
//		} catch (Exception e) {
//			logger.error("Error while fetching users by search key -> {}", e.getMessage());
//			e.printStackTrace();
//			return new Response(-1, "Error", e.getMessage());
//		}
//
//	}
	@Override
	public Response getAllSearchByChat(String searchKey) {
	    try {
	        Integer loggedInUserId = userDetails.userInfo().getId();
	        String loggedInUserType = userDetails.userInfo().getUserType();
	        Float loggedInAdminReview = userDetails.userInfo().getAdminReview();

	        List<User> users;

	        if ("public User".equalsIgnoreCase(loggedInUserType)) {
	            // Public user can chat with Public or Industry users having adminReview 1–5
	            users = userRepository.findByNameContainingIgnoreCaseAndStatusAndUserTypeOrAdminReviewInRange(
	                searchKey,
	                true,
	                "public User",
	                "Industry User",
	                Arrays.asList(1f, 2f, 3f, 4f, 5f)
	            );
	        } else if ("Industry User".equalsIgnoreCase(loggedInUserType)) {
	            // Industry users can chat with Public and other Industry users having adminReview 5.1–9.9
	            users = userRepository.findByNameContainingIgnoreCaseAndStatusAndUserTypeOrAdminReviewInRange(
	                searchKey,
	                true,
	                "public User",
	                "Industry User",
	                Arrays.asList(5.1f, 6f, 7f, 8f, 9f, 9.9f)
	            );
	        } else {
	            return new Response(-1, "Invalid user type", null);
	        }

	        // Remove self from search results
	        users = users.stream()
	            .filter(user -> !user.getUserId().equals(loggedInUserId))
	            .collect(Collectors.toList());

	        if (!users.isEmpty()) {
	            List<ChatUserWebModel> responseList = this.transformsUserDetailsForChat(users, loggedInUserId);
	            return new Response(1, "Success", responseList);
	        } else {
	            return new Response(-1, "No matching users found", null);
	        }

	    } catch (Exception e) {
	        logger.error("Error while searching chat users by keyword -> {}", e.getMessage(), e);
	        return new Response(-1, "Internal server error", e.getMessage());
	    }
	}

	private List<ChatUserWebModel> transformsUserDetailsForChat(List<User> users, Integer loggedInUserId) {
	    return users.stream().map(user -> {
	        ChatUserWebModel chatUserWebModel = new ChatUserWebModel();
	        chatUserWebModel.setUserId(user.getUserId());
	        chatUserWebModel.setUserName(user.getName());
	        chatUserWebModel.setUserType(user.getUserType());
	        chatUserWebModel.setProfilePicUrl(userService.getProfilePicUrl(user.getUserId()));
	        chatUserWebModel.setOnlineStatus(user.getOnlineStatus());
	        chatUserWebModel.setAdminReview(user.getAdminReview());
	        getLatestChatMessage(user, chatUserWebModel, loggedInUserId);
	        return chatUserWebModel;
	    }).collect(Collectors.toList());
	}

	

	@Override
	public Response getInAppNotification(int page, int size) {
	    try {
	        Integer userId = userDetails.userInfo().getId();

	        // Date range: from 30 days ago to now
	        Calendar calendar = Calendar.getInstance();
	        Date endDate = calendar.getTime(); // now
	        calendar.add(Calendar.DAY_OF_MONTH, -30);
	        Date startDate = calendar.getTime();

	        Pageable pageable = PageRequest.of(page, size, Sort.by("createdOn").descending());

	        Page<InAppNotification> pageResult = inAppNotificationRepository
	                .findByReceiverIdAndCreatedOnBetweenAndIsDeletedFalseOrderByCreatedOnDesc(
	                        userId, startDate, endDate, pageable
	                );

	        List<InAppNotification> notifications = pageResult.getContent();

	        if (notifications.isEmpty()) {
	            return new Response(0, "No Notifications", "No notifications found for the given user ID");
	        }

	        long unreadCount = notifications.stream()
	                .filter(notification -> !notification.getIsRead())
	                .count();

	        // Group notifications by date
	        Map<String, List<InAppNotificationWebModel>> grouped = new LinkedHashMap<>();
	        LocalDate today = LocalDate.now(ZoneOffset.UTC);
	        LocalDate yesterday = today.minusDays(1);

	        for (InAppNotification notification : notifications) {
	            LocalDate createdDate = notification.getCreatedOn()
	                    .toInstant()
	                    .atZone(ZoneOffset.UTC)
	                    .toLocalDate();

	            String group;
	            if (createdDate.equals(today)) {
	                group = "Today";
	            } else if (createdDate.equals(yesterday)) {
	                group = "Yesterday";
	            } else {
	                group = "Earlier";
	            }

	            InAppNotificationWebModel dto = new InAppNotificationWebModel();
	            dto.setInAppNotificationId(notification.getInAppNotificationId());
	            dto.setSenderId(notification.getSenderId());
	            dto.setProfilePicUrl(userService.getProfilePicUrl(notification.getSenderId()));
	            dto.setReceiverId(notification.getReceiverId());
	            dto.setTitle(notification.getTitle());
	            dto.setMessage(notification.getMessage());
	            dto.setCreatedOn(notification.getCreatedOn());
	            dto.setIsRead(notification.getIsRead());
	            dto.setCurrentStatus(notification.getCurrentStatus());

	            Optional<User> sender = userRepository.getByUserId(notification.getSenderId());
	            dto.setSenderName(sender.map(User::getName).orElse("Unknown"));

	            dto.setCreatedBy(notification.getCreatedBy());
	            dto.setUpdatedBy(notification.getUpdatedBy());
	            dto.setUpdatedOn(notification.getUpdatedOn());
	            dto.setUserType(notification.getUserType());
	            dto.setId(notification.getId());
	            dto.setPostId(notification.getPostId());
	            dto.setProfession(notification.getProfession());
	            dto.setAdminReview(notification.getAdminReview());

	            if ("marketPlace".equals(notification.getUserType())) {
	                marketPlaceChatRepository.findByIds(notification.getId()).ifPresentOrElse(
	                        chat -> {
	                            dto.setAccept(chat.getAccept());
	                            dto.setAdditionalData(chat.getAccept() != null ? 
	                                (chat.getAccept() ? "Accepted" : "Declined") : "null");
	                        },
	                        () -> {
	                            dto.setAccept(null);
	                            dto.setAdditionalData("null");
	                        }
	                );
	            } else if ("shootingLocation".equals(notification.getUserType())) {
	                shootingLocationChatRepository.findByIds(notification.getId()).ifPresentOrElse(
	                        chat -> {
	                            dto.setAccept(chat.getAccept());
	                            dto.setAdditionalData(chat.getAccept() != null ? 
	                                (chat.getAccept() ? "Accepted" : "Declined") : "null");
	                        },
	                        () -> {
	                            dto.setAccept(null);
	                            dto.setAdditionalData("null");
	                        }
	                );
	            } else {
	                dto.setAccept(null);
	                dto.setAdditionalData("null");
	            }

	            grouped.computeIfAbsent(group, k -> new ArrayList<>()).add(dto);
	        }

	        // Build response map
	        Map<String, Object> response = new HashMap<>();
	        response.put("notifications", grouped); // grouped by Today, Yesterday, Earlier
	        response.put("unreadCount", unreadCount);
	        response.put("totalPages", pageResult.getTotalPages());
	        response.put("currentPage", pageResult.getNumber());
	        response.put("totalItems", pageResult.getTotalElements());

	        return new Response(1, "Success", response);

	    } catch (Exception e) {
	        logger.error("Error fetching notifications -> {}", e.getMessage(), e);
	        return new Response(0, "Error", "An error occurred while fetching notifications");
	    }
	}

	@Override
	public Response updateInAppNotification(InAppNotificationWebModel inAppNotificationWebModel) {
	    try {
	        Optional<InAppNotification> data = inAppNotificationRepository.findById(inAppNotificationWebModel.getInAppNotificationId());

	        if (data.isPresent()) {
	            InAppNotification notification = data.get();
	            notification.setIsRead(false);  // Update the isRead column to false
	            notification.setUpdatedOn(new Date());  // Optionally update the updatedOn column
	            notification.setUpdatedBy(inAppNotificationWebModel.getUpdatedBy());  // Optionally update the updatedBy column
	            
	            inAppNotificationRepository.save(notification);

	            return new Response(1, "Success", "Notification updated successfully");
	        } else {
	            return new Response(0, "Not Found", "Notification not found for the given ID");
	        }
	    } catch (Exception e) {
	        logger.error("Error occurred while updating notification -> {}", e.getMessage());
	        return new Response(0, "Error", "An error occurred while updating the notification");
	    }
	}

	@Override
	public Response deleteChatMessage(ChatWebModel chatWebModel) {
	    try {
	        Optional<Chat> chatOptional = chatRepository.findById(chatWebModel.getChatId());

	        if (chatOptional.isEmpty()) {
	            return new Response(0, "Not Found", "Chat not found");
	        }

	        Chat chat = chatOptional.get();

	        boolean isSender = chat.getChatSenderId().equals(chatWebModel.getUserId());
	        boolean isReceiver = chat.getChatReceiverId().equals(chatWebModel.getUserId());

	        if (!isSender && !isReceiver) {
	            return new Response(0, "Unauthorized", "User is not part of this chat");
	        }

	        String deleteType = chatWebModel.getDeleteType();

	        if ("everyone".equalsIgnoreCase(deleteType)) {
	            // Delete for everyone
	            chat.setIsDeletedForEveryone(true);
	            chat.setDeletedBySender(true);
	            chat.setDeletedByReceiver(true);
	           

	            // Soft delete media files
	            List<MediaFiles> mediaFiles = mediaFilesRepository.findByCategoryRefId(chat.getChatId());
	            for (MediaFiles file : mediaFiles) {
	                file.setStatus(false);
	            }
	            mediaFilesRepository.saveAll(mediaFiles);

	            chatRepository.save(chat);
	            return new Response(1, "Success", "Message deleted for everyone");
	        } else {
	            // Delete only for the current user
	            if (isSender) {
	                chat.setDeletedBySender(true);
	            } else if (isReceiver) {
	                chat.setDeletedByReceiver(true);
	            }

	            // If both sender and receiver have deleted, mark as fully deleted
	            if (Boolean.TRUE.equals(chat.getDeletedBySender()) && Boolean.TRUE.equals(chat.getDeletedByReceiver())) {
	                chat.setIsDeletedForEveryone(true);

	                List<MediaFiles> mediaFiles = mediaFilesRepository.findByCategoryRefId(chat.getChatId());
	                for (MediaFiles file : mediaFiles) {
	                    file.setStatus(false);
	                }
	                mediaFilesRepository.saveAll(mediaFiles);
	            }

	            chatRepository.save(chat);
	            return new Response(1, "Success", "Message deleted for current user");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new Response(0, "Error", "Something went wrong while deleting the chat message");
	    }
	}

	@Override
	public Response updateOnlineStatus(UserWebModel userWebModel) {
	    Optional<User> userData = userRepository.findById(userWebModel.getUserId());
	    
	    if (userData.isPresent()) {
	        User user = userData.get();
	        user.setOnlineStatus(userWebModel.getOnlineStatus());
	        userRepository.save(user);
	        return new Response(1,"Success", "Online status updated successfully"); // Success response
	    } else {
	        return new Response(0,"fail", "User not found"); // Failure response
	    }
	}
	
	
	@Override
	public Response deleteChatProfile(Integer currentUserId, Integer targetUserId) {
	    try {
	        // Fetch all chats between the current user and target user
	        List<Chat> chats = chatRepository.findByParticipants(currentUserId, targetUserId);

	        if (chats.isEmpty()) {
	            return new Response(0, "Not Found", "No chats found between these users");
	        }

	        for (Chat chat : chats) {
	            // Soft delete chat for the current user only
	            if (chat.getChatSenderId().equals(currentUserId)) {
	                chat.setDeletedBySender(true);
	            } else if (chat.getChatReceiverId().equals(currentUserId)) {
	                chat.setDeletedByReceiver(true);
	            }

	            chatRepository.save(chat);

	            // Get all media files linked to this chat
	            List<MediaFiles> mediaFiles = mediaFilesRepository.findByCategoryRefId(chat.getChatId());

	            for (MediaFiles media : mediaFiles) {
	                // Prevent duplicate tracker entries
	                boolean alreadyDeleted = chatMediaDeleteTrackerRepository
	                    .findByUserIdAndChatId(currentUserId, chat.getChatId())
	                    .stream()
	                    .anyMatch(t -> t.getMediaFileId().equals(media.getId()));

	                if (!alreadyDeleted) {
	                    ChatMediaDeleteTracker tracker = ChatMediaDeleteTracker.builder()
	                        .chatId(chat.getChatId())
	                        .mediaFileId(media.getId())
	                        .userId(currentUserId)
	                        .deleted(true)
	                        .build();

	                    chatMediaDeleteTrackerRepository.save(tracker);
	                }
	            }
	        }

	        return new Response(1, "Success", "Chat profile soft-deleted for current user");

	    } catch (Exception e) {
	        e.printStackTrace();
	        return new Response(0, "Error", "An error occurred while soft-deleting chat profile");
	    }
	}

}
