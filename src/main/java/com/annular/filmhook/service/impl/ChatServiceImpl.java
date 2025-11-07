package com.annular.filmhook.service.impl;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class ChatServiceImpl implements ChatService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	MediaFilesRepository mediaFileRepository;
	@Autowired
	ChatRepository chatRepository;

	@Autowired
	MediaFilesService mediaFilesService;

	@Autowired
	UserDetails userDetails;



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
						.senderChatIsActive(true)
						.receiverChatIsActive(true)
						.chatCreatedBy(userId)
						.senderRead(true)
						.receiverRead(false)
						.chatCreatedOn(new Date())
						.storyId(chatWebModel.getStoryId())
						.replyType(chatWebModel.getStoryId() != null ? "story" : "normal")
						 .replyToMessageId(chatWebModel.getReplyToMessageId())
						.build();
				
				

				chatRepository.save(chat);
				
				//optional
				Chat replyMessage = null;
				if (chatWebModel.getReplyToMessageId() != null) {
				    replyMessage = chatRepository.findById(chatWebModel.getReplyToMessageId())
				                     .orElse(null);
				}
				// Save media files if present
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

				// ✅ Firebase Push Notification
				Optional<User> receiverOptional = userRepository.findById(chatWebModel.getChatReceiverId());
				if (receiverOptional.isPresent()) {
					User receiver = receiverOptional.get();
					String deviceToken = receiver.getFirebaseDeviceToken();

					if (deviceToken != null && !deviceToken.trim().isEmpty()) {
						 String senderName = user.getName();

						// 1️⃣ Get unread messages from this sender to this receiver
						List<String> unreadMessages = chatRepository
								.findUnreadMessagesFromSender(userId, chatWebModel.getChatReceiverId());

						String latestMessage;
						String imageUrl = null;
						String mediaType = "TEXT";
						
						// After saving chat + media files
						List<MediaFiles> savedFiles = mediaFileRepository.findByCategoryAndCategoryRefId(
						        MediaFileCategory.Chat, chat.getChatId());

						if (!savedFiles.isEmpty()) {
						    MediaFiles firstFile = savedFiles.get(0);
						    imageUrl = firstFile.getFilePath();
						    mediaType = firstFile.getFileType();

						    String fileType = firstFile.getFileType() != null ? firstFile.getFileType().toLowerCase() : "";

						    if (fileType.contains("image") || fileType.endsWith(".jpg") || fileType.endsWith(".jpeg") 
						        || fileType.endsWith(".png") || fileType.endsWith(".webp")) {
						        
						        latestMessage = "📷 Photo";

						    } else if (fileType.contains("video") || fileType.endsWith(".mp4") || fileType.endsWith(".mov") 
						               || fileType.endsWith(".avi") || fileType.endsWith(".webm")) {
						        
						        latestMessage = "🎥 Video";

						    } else if (fileType.contains("post")) {
						        
						        latestMessage = "📌 Shared Post";

						    } else {
						        
						        latestMessage = "📎 Attachment";  
						    }

						} else {
						    latestMessage = chatWebModel.getMessage();
						}

					        // Add current latestMessage if not already present
					        if (!unreadMessages.contains(latestMessage)) {
					            unreadMessages.add(latestMessage);
					        }

						// 3️⃣ Combine all unread messages into a single string for payload
						String allUnread = String.join("||", unreadMessages);

						try {
							// Build FCM Notification
							  Notification.Builder notificationBuilder = Notification.builder()
					                    .setTitle(senderName)
					                    .setBody(latestMessage);

					            // If photo exists, add image URL to FCM notification
					            if (imageUrl != null) {
					                notificationBuilder.setImage(imageUrl);
					            }

						  Notification notificationData = notificationBuilder.build();

							// Android-specific notification settings
							AndroidNotification androidNotification = AndroidNotification.builder()
									.setIcon("ic_notification")
									.setColor("#00A2E8")
									.build();

							AndroidConfig androidConfig = AndroidConfig.builder()
									.setNotification(androidNotification)
									.build();

							// Build and send FCM message
							Message message = Message.builder()
									.setNotification(notificationData)
									.setAndroidConfig(androidConfig)
									.putData("chatId", String.valueOf(chat.getChatId()))
									.putData("type", "chat")
									.putData("profilePic", userService.getProfilePicUrl(userId))
									.putData("senderId", String.valueOf(user.getUserId()))
									.putData("senderName", senderName) 
		                            .putData("allUnread", allUnread)   
		                            .putData("userType", user.getUserType())
		                            .putData("adminReview", String.valueOf(user.getAdminReview()))
		                            .putData("groupKey", "filmhook_chat") 
		                            .putData("mediaType", mediaType)  
		                            .putData("mediaUrl", imageUrl != null ? imageUrl : "")
									.setToken(deviceToken)
									.build();

							String response = FirebaseMessaging.getInstance().send(message);
							logger.info("Successfully sent push notification: " + response);

						} catch (FirebaseMessagingException e) {
							logger.error("Failed to send push notification", e);
						}

					} else {
						logger.warn("Device token is null or empty for user ID: " + receiver.getUserId());
					}
				}
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
		            boolean isSender = chat.getChatSenderId().equals(loggedInUserId);
		            boolean isReceiver = chat.getChatReceiverId().equals(loggedInUserId);

		            // Skip if the logged-in user isn't part of this chat (safety)
		            if (!isSender && !isReceiver) continue;

		            // 🧹 Skip if both sides have chat inactive 
		            if (Boolean.FALSE.equals(chat.getSenderChatIsActive()) &&
		                Boolean.FALSE.equals(chat.getReceiverChatIsActive())) {
		                continue;
		            }

		            // 🧹 Skip if the logged-in user deleted their whole chat profile
		            if ((isSender && Boolean.FALSE.equals(chat.getSenderChatIsActive())) ||
		                (isReceiver && Boolean.FALSE.equals(chat.getReceiverChatIsActive()))) {
		                continue;
		            }

		            // 🧹 Skip messages deleted for everyone
		          
		            if (Boolean.TRUE.equals(chat.getIsDeletedForEveryone())) {
		                // Still show the user if the chat profile is active
		                if (isSender && Boolean.TRUE.equals(chat.getSenderChatIsActive())) {
		                    chatUserIds.add(chat.getChatReceiverId());
		                } else if (isReceiver && Boolean.TRUE.equals(chat.getReceiverChatIsActive())) {
		                    chatUserIds.add(chat.getChatSenderId());
		                }
		                continue;
		            }

		            // 🧹 Skip messages deleted only by this user
		            if (isSender && Boolean.TRUE.equals(chat.getDeletedBySender())) {
		             
		                continue;
		            } else if (isReceiver && Boolean.TRUE.equals(chat.getDeletedByReceiver())) {
		       
		                continue;
		            }

		            // ✅ Add the other user if current user's chat profile is active
		            if (isSender && Boolean.TRUE.equals(chat.getSenderChatIsActive())) {
		                chatUserIds.add(chat.getChatReceiverId());
		            } else if (isReceiver && Boolean.TRUE.equals(chat.getReceiverChatIsActive())) {
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

			int unreadCount = chatRepository.countUnreadMessages(loggedInUserId, user.getUserId());

			chatUserWebModel.setReceiverUnreadCount(unreadCount);

			return chatUserWebModel;
		}).collect(Collectors.toList());
	}
	
	public void getLatestChatMessage(User user, ChatUserWebModel chatUserWebModel, Integer loggedInUserId) {
	    String latestMsg = "";
	    Date latestMsgTime = null;
	    boolean isLatestStory = false;

	    try {
	        // ✅ Null safety for user
	        if (user == null || user.getUserId() == null) {
	            logger.warn("User or userId is null while fetching latest chat message");
	            chatUserWebModel.setLatestMessage("");
	            chatUserWebModel.setLatestMsgTime(null);
	            chatUserWebModel.setIsLatestStory(false);
	            return;
	        }

	        Optional<Chat> lastChatOpt = getLatestChatBetweenUsers(loggedInUserId, user.getUserId());

	        if (lastChatOpt.isEmpty()) {
	            logger.info("No chat found between {} and {}", loggedInUserId, user.getUserId());
	            chatUserWebModel.setLatestMessage("");
	            chatUserWebModel.setLatestMsgTime(null);
	            chatUserWebModel.setIsLatestStory(false);
	            return;
	        }

	        Chat chat = lastChatOpt.get();

//	        // ✅ Skip if inactive for either side
//	        if (Boolean.FALSE.equals(chat.getSenderChatIsActive()) || Boolean.FALSE.equals(chat.getReceiverChatIsActive())) {
//	            chatUserWebModel.setLatestMessage("");
//	            chatUserWebModel.setLatestMsgTime(null);
//	            chatUserWebModel.setIsLatestStory(false);
//	            return;
//	        }

	        // ✅ Deleted message placeholder
	        if (Boolean.TRUE.equals(chat.getIsDeletedForEveryone())) {
	            latestMsg = "🚫 This message was deleted";

	        // ✅ Story reply
	        } else if ("story".equalsIgnoreCase(chat.getReplyType())) {
	            isLatestStory = true;
	            latestMsg = chat.getMessage();

	        // ✅ Normal text
	        } else if (chat.getMessage() != null && !chat.getMessage().trim().isEmpty()) {
	            latestMsg = chat.getMessage();

	        // ✅ Media message handling
	        } else {
	            List<FileOutputWebModel> savedFiles = mediaFilesService
	                    .getMediaFilesByCategoryAndRefId(MediaFileCategory.Chat, chat.getChatId())
	                    .stream()
	                    .sorted(Comparator.comparing(FileOutputWebModel::getId).reversed())
	                    .collect(Collectors.toList());

	            if (!savedFiles.isEmpty()) {
	                FileOutputWebModel firstFile = savedFiles.get(0);
	                String fileType = firstFile.getFileType() != null ? firstFile.getFileType().toLowerCase() : "";

	                if (fileType.contains("image") || fileType.endsWith(".jpg") || fileType.endsWith(".jpeg")
	                        || fileType.endsWith(".png") || fileType.endsWith(".webp")) {
	                    latestMsg = "📷 Photo";
	                } else if (fileType.contains("video") || fileType.endsWith(".mp4") || fileType.endsWith(".mov")
	                        || fileType.endsWith(".avi") || fileType.endsWith(".webm")) {
	                    latestMsg = "🎥 Video";
	                } else if (fileType.contains("post")) {
	                    latestMsg = "📌 Shared Post";
	                } else {
	                    latestMsg = "📎 Attachment";
	                }
	            }
	        }

	        latestMsgTime = chat.getChatCreatedOn();

	    } catch (Exception e) {
	        logger.error("Error while getting latest chat message -> {}", e.getMessage(), e);
	        // Prevent propagation of 404 or 500
	        latestMsg = "";
	        latestMsgTime = null;
	        isLatestStory = false;
	    }

	    // ✅ Set final values safely
	    chatUserWebModel.setLatestMessage(latestMsg);
	    chatUserWebModel.setLatestMsgTime(latestMsgTime);
	    chatUserWebModel.setIsLatestStory(isLatestStory);
	}
	private Optional<Chat> getLatestChatBetweenUsers(Integer loggedInUserId, Integer targetUserId) {
	    try {
	        if (loggedInUserId == null || targetUserId == null) {
	            logger.warn("Invalid user IDs while fetching latest chat");
	            return Optional.empty();
	        }

	        // ✅ Fetch both sides of conversation
	        List<Chat> senderMessages = chatRepository.getMessageListBySenderIdAndReceiverId(loggedInUserId, targetUserId);
	        List<Chat> receiverMessages = chatRepository.getMessageListBySenderIdAndReceiverId(targetUserId, loggedInUserId);

	        List<Chat> allMessages = new ArrayList<>();

	        if (senderMessages != null && !senderMessages.isEmpty()) {
	            allMessages.addAll(senderMessages);
	        }

	        if (receiverMessages != null && !receiverMessages.isEmpty()) {
	            allMessages.addAll(receiverMessages);
	        }

	        if (allMessages.isEmpty()) {
	            return Optional.empty();
	        }

	        allMessages.sort(Comparator.comparing(Chat::getChatCreatedOn).reversed());

	        // ✅ Iterate through all messages to find the latest *valid* one
	        for (Chat chat : allMessages) {
	        	  if ((chat.getChatSenderId().equals(loggedInUserId) && Boolean.FALSE.equals(chat.getSenderChatIsActive())) ||
	                      (chat.getChatReceiverId().equals(loggedInUserId) && Boolean.FALSE.equals(chat.getReceiverChatIsActive()))) {
	                      continue;
	                  }

	                  // ✅ Don't skip just because the *other user* deleted
	                  return Optional.of(chat);
	              }
	        return Optional.empty();

	    } catch (Exception e) {
	        logger.error("Error fetching latest chat between {} and {} -> {}", loggedInUserId, targetUserId, e.getMessage(), e);
	        return Optional.empty();
	    }
	}


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
				if ((Boolean.TRUE.equals(c.getIsDeletedForEveryone()) || !Boolean.TRUE.equals(c.getDeletedBySender())) && Boolean.TRUE.equals(c.getSenderChatIsActive())) {
					allMessages.add(c);
				}
			}

			for (Chat c : receiverMessages) {
				if ((Boolean.TRUE.equals(c.getIsDeletedForEveryone()) || !Boolean.TRUE.equals(c.getDeletedByReceiver()))&& Boolean.TRUE.equals(c.getReceiverChatIsActive())) {
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
					  String finalMessage = null;
			            boolean isSenderActive = Boolean.TRUE.equals(chat.getSenderChatIsActive());
			            boolean isReceiverActive = Boolean.TRUE.equals(chat.getReceiverChatIsActive());

			            if (chat.getChatSenderId().equals(senderId)) {
			                if (!isSenderActive) continue;
			                finalMessage = Boolean.TRUE.equals(chat.getIsDeletedForEveryone())
			                        ? "🚫 This message was deleted"
			                        : chat.getMessage();
			            } else if (chat.getChatReceiverId().equals(senderId)) {
			                if (!isReceiverActive) continue;
			                finalMessage = Boolean.TRUE.equals(chat.getIsDeletedForEveryone())
			                        ? "🚫 This message was deleted"
			                        : chat.getMessage();
			            }

					ChatWebModel chatWebModel = ChatWebModel.builder().chatId(chat.getChatId())
							.chatSenderId(chat.getChatSenderId()).chatReceiverId(chat.getChatReceiverId())
							.senderchatIsActive(chat.getSenderChatIsActive()).chatCreatedBy(chat.getChatCreatedBy())
							.reciverchatIsActive(chat.getReceiverChatIsActive())
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
							.edited(chat.getEdited())
							.editedOn(chat.getEditedOn())
							.isDeletedForEveryone(chat.getIsDeletedForEveryone())
							.build();

					// 👉 Fetch replied message if present
					if (chat.getReplyToMessageId() != null) {
					    chatRepository.findById(chat.getReplyToMessageId()).ifPresent(replyMsg -> {
					    	  List<FileOutputWebModel> replyMediaFiles = mediaFilesService
					                  .getMediaFilesByCategoryAndRefId(MediaFileCategory.Chat, replyMsg.getChatId());

					          FileOutputWebModel replyMedia = !replyMediaFiles.isEmpty() ? replyMediaFiles.get(0) : null;
					    	
					    	
					        chatWebModel.setReplyToMessage(
					            new ChatWebModel.ReplyMessageDTO(   // ✅ use nested DTO instead of full ChatWebModel
					                replyMsg.getChatId(),
					                replyMsg.getChatSenderId(),
					                Boolean.TRUE.equals(replyMsg.getIsDeletedForEveryone())
					                        ? "🚫 This message was deleted"
					                        : replyMsg.getMessage(),
					                replyMsg.getUserAccountName(),
					                replyMedia != null ? replyMedia.getFilePath() : null,   
					                        replyMedia != null ? replyMedia.getFileType() : null  
					            )
					        );
					    });
					}

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



	public Response getInAppNotification(int page, int size) {
		try {
			Integer userId = userDetails.userInfo().getId();

			// Get user
			Optional<User> userOptional = userRepository.findById(userId);
			if (!userOptional.isPresent()) {
				return new Response(0, "User not found", null);
			}

			User user = userOptional.get();

			// Get last notification open time
			Date lastOpenedTime = user.getLastNotificationOpenTime();
			if (lastOpenedTime == null) {
				Calendar cal = Calendar.getInstance();
				cal.set(2000, Calendar.JANUARY, 1); // set default old date
				lastOpenedTime = cal.getTime();
			}

			// ✅ Make final for lambda
			final Date finalLastOpenedTime = lastOpenedTime;

			// Reset open time to now
			Date now = new Date();
			user.setLastNotificationOpenTime(now);
			userRepository.save(user); // Save immediately to avoid stale time

			// Set 30-day filter range
			Calendar calendar = Calendar.getInstance();
			Date endDate = calendar.getTime();
			calendar.add(Calendar.DAY_OF_MONTH, -30);
			Date startDate = calendar.getTime();

			Pageable pageable = PageRequest.of(page, size, Sort.by("createdOn").descending());

			Page<InAppNotification> pageResult = inAppNotificationRepository
					.findByReceiverIdAndCreatedOnBetweenAndIsDeletedFalseOrderByCreatedOnDesc(
							userId, startDate, endDate, pageable);

			List<InAppNotification> notifications = pageResult.getContent();

			if (notifications.isEmpty()) {
				Map<String, Object> emptyResponse = new HashMap<>();
				emptyResponse.put("notifications", Collections.emptyMap());
				emptyResponse.put("unreadCount", 0);
				emptyResponse.put("unseenCount", 0); // still reset
				emptyResponse.put("totalPages", pageResult.getTotalPages());
				emptyResponse.put("currentPage", pageResult.getNumber());
				emptyResponse.put("totalItems", pageResult.getTotalElements());

				return new Response(0, "No Notifications", emptyResponse);
			}

			// Totals independent of page size
			long unreadCount = inAppNotificationRepository.countUnreadInRange(userId, startDate, endDate);
			long unseenCount = inAppNotificationRepository.countUnseenSince(userId, finalLastOpenedTime);


			// Group by Today / Yesterday / Earlier
			Map<String, List<InAppNotificationWebModel>> grouped = new LinkedHashMap<>();
			LocalDate today = LocalDate.now(ZoneOffset.UTC);
			LocalDate yesterday = today.minusDays(1);

			for (InAppNotification notification : notifications) {
				LocalDate createdDate = notification.getCreatedOn()
						.toInstant().atZone(ZoneOffset.UTC).toLocalDate();

				String group = createdDate.equals(today) ? "Today"
						: createdDate.equals(yesterday) ? "Yesterday"
								: "Earlier";

				InAppNotificationWebModel dto = new InAppNotificationWebModel();
				dto.setInAppNotificationId(notification.getInAppNotificationId());
				dto.setSenderId(notification.getSenderId());
				dto.setProfilePicUrl(userService.getProfilePicUrl(notification.getSenderId()));
				dto.setProfilePicUrl2(userService.getProfilePicUrl(notification.getSenderId2()));
				dto.setReceiverId(notification.getReceiverId());
				dto.setTitle(notification.getTitle());
				dto.setMessage(notification.getMessage());
				dto.setCreatedOn(notification.getCreatedOn());
				dto.setIsRead(notification.getIsRead());
				dto.setCurrentStatus(notification.getCurrentStatus());
				dto.setSenderId2(notification.getSenderId2());
				Optional<User> sender = userRepository.getByUserId(notification.getSenderId());
				dto.setSenderName(sender.map(User::getName).orElse("Film-hook"));

				if (notification.getSenderId2() != null) {
					Optional<User> sender2 = userRepository.getByUserId(notification.getSenderId2());
					dto.setSenderName2(sender2.map(User::getName).orElse("Unknown"));
				} else {
					dto.setSenderName2(null);
				}
				dto.setCreatedBy(notification.getCreatedBy());
				dto.setUpdatedBy(notification.getUpdatedBy());
				dto.setUpdatedOn(notification.getUpdatedOn());
				dto.setUserType(notification.getUserType());
				dto.setId(notification.getId());
				dto.setPostId(notification.getPostId());
				dto.setProfession(notification.getProfession());
				dto.setAdminReview(notification.getAdminReview());


				// Handle accept/additionalData
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
							});
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
							});
				} else {
					dto.setAccept(null);
					dto.setAdditionalData("null");
				}

				grouped.computeIfAbsent(group, k -> new ArrayList<>()).add(dto);
			}

			// Prepare final response
			Map<String, Object> response = new HashMap<>();
			response.put("notifications", grouped);
			response.put("unreadCount", unreadCount);
			response.put("unseenCount", unseenCount); // ✅ Add this to response
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
				notification.setIsRead(true);  // Update the isRead column to false
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
					chat.setSenderChatIsActive(false);
				} else if (isReceiver) {
					chat.setDeletedByReceiver(true);
					chat.setReceiverChatIsActive(false);
				}

					List<MediaFiles> mediaFiles = mediaFilesRepository.findByCategoryRefId(chat.getChatId());
					for (MediaFiles file : mediaFiles) {
						file.setStatus(false);
					}
					mediaFilesRepository.saveAll(mediaFiles);
				

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
					chat.setSenderChatIsActive(false);

				} else if (chat.getChatReceiverId().equals(currentUserId)) {
					chat.setDeletedByReceiver(true);
					chat.setReceiverChatIsActive(false);
				
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

	
	@Override
	public ResponseEntity<?> editMessage(Integer chatId, String newMessage) {
	    try {
	        logger.info("Edit Message Method Start for ChatId: {}", chatId);

	        Integer userId = userDetails.userInfo().getId();
	        Optional<User> userOptional = userRepository.findById(userId);

	        if (userOptional.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body("User not found");
	        }

	        Optional<Chat> chatOptional = chatRepository.findById(chatId);
	        if (chatOptional.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("Chat message not found");
	        }

	        Chat chat = chatOptional.get();

	        // ✅ Only sender can edit
	        if (!chat.getChatSenderId().equals(userId)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                    .body("You are not allowed to edit this message");
	        }

	        // ✅ Allow edit only within 15 minutes
	        long timeDiff = new Date().getTime() - chat.getTimeStamp().getTime();
	        long allowedMillis = 60 * 60 * 1000; // 1 hr

	        if (timeDiff > allowedMillis) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                    .body("Edit time expired");
	        }

	        // ✅ Update message
	        chat.setMessage(newMessage);
	        chat.setEdited(true);
	        chat.setEditedOn(new Date());

	        chatRepository.save(chat);

	        return ResponseEntity.ok("Message updated successfully");

	    } catch (Exception e) {
	        logger.error("Error while editing message", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An error occurred while editing the message");
	    }
	}

	
	
	
}