package com.annular.filmhook.service.impl;

import java.util.ArrayList;

import java.util.Date;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;

import com.annular.filmhook.model.Chat;
import com.annular.filmhook.model.InAppNotification;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.User;

import com.annular.filmhook.repository.ChatRepository;
import com.annular.filmhook.repository.InAppNotificationRepository;
import com.annular.filmhook.repository.MediaFilesRepository;
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
	private UserService userService;

	@Autowired
	InAppNotificationRepository inAppNotificationRepository;

	@Autowired
	FirebaseConfig firebaseConfig;
	
	@Autowired
	MediaFilesRepository mediaFilesRepository;

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
				Chat chat = Chat.builder().message(chatWebModel.getMessage())
						.chatReceiverId(chatWebModel.getChatReceiverId()).userAccountName(user.getName())
						.chatSenderId(userId).userType(user.getUserType()).timeStamp(new Date()).chatIsActive(true)
						.chatCreatedBy(userId).senderRead(true).receiverRead(false).chatCreatedOn(new Date()).build();
				chatRepository.save(chat);

				if (!Utility.isNullOrEmptyList(chatWebModel.getFiles())) {
					// Saving the chat files in the media_files table
					FileInputWebModel fileInputWebModel = FileInputWebModel.builder().userId(chatWebModel.getUserId())
							.category(MediaFileCategory.Chat).categoryRefId(chat.getChatId())
							.files(chatWebModel.getFiles()).build();
					mediaFilesService.saveMediaFiles(fileInputWebModel, userOptional.get());
				}

				// Sending push notification
				if (chatWebModel.getChatReceiverId() != null) {
					Optional<User> receiverOptional = userRepository.findById(chatWebModel.getChatReceiverId());
					if (receiverOptional.isPresent()) {
						User receiver = receiverOptional.get();
						String notificationTitle = "filmHook";
						String notificationMessage = "You have a new message from " + user.getName();
						// Save the notification to the InAppNotification table
						InAppNotification inAppNotification = InAppNotification.builder().senderId(userId)
								.receiverId(receiver.getUserId()).title(notificationTitle)
								.userType("chat")
								.id(chat.getChatId())
								.message(notificationMessage).createdOn(new Date()).isRead(true).createdBy(userId)
								.build();
						inAppNotificationRepository.save(inAppNotification);

						Message message = Message.builder()
								.setNotification(Notification.builder().setTitle(notificationTitle)
										.setBody(notificationMessage).build())
								.putData("chatId", Integer.toString(chat.getChatId())) // Add chatId to data
								.setToken(receiver.getFirebaseDeviceToken()) // Set the receiver's device token
								.build();

						// Send the message using FirebaseMessaging
						try {
							String response = FirebaseMessaging.getInstance().send(message);
							//String response = firebaseConfig.firebaseMessaging().send(message);
							logger.info("Successfully sent message: " + response);
							// Save the notification to the InAppNotification table
//							InAppNotification inAppNotification = InAppNotification.builder().senderId(userId)
//									.receiverId(receiver.getUserId()).title(notificationTitle)
//									.message(notificationMessage).createdOn(new Date()).isRead(true).createdBy(userId)
//									.build();
//							inAppNotificationRepository.save(inAppNotification);
						} catch (FirebaseMessagingException e) {
							logger.error("Failed to send push notification: " + e.getMessage());
						}
					} else {
						logger.warn("Receiver user not found for id: " + chatWebModel.getChatReceiverId());
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
	public ResponseEntity<?> getAllUser() {
		try {
			logger.info("Get All Users Method Start");
			Integer loggedInUserId = userDetails.userInfo().getId();

			// Fetch all distinct user IDs associated with the logged-in user from
			// ChatRepository
			Set<Integer> chatUserIds = new HashSet<>();
			chatUserIds.addAll(chatRepository.findSenderIdsByReceiverId(loggedInUserId));
			chatUserIds.addAll(chatRepository.findReceiverIdsBySenderId(loggedInUserId));

			// Remove the logged-in user's ID from the set
			chatUserIds.remove(loggedInUserId);

			if (chatUserIds.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			// Fetch user details for the user IDs
			List<User> users = userRepository.findAllById(chatUserIds);

			// If users exist, map them to a list of simplified user models containing ID,
			// name, and profile picture URLs
			if (!users.isEmpty()) {
				List<ChatUserWebModel> userResponseList = this.transformUserDetailsForChat(users, loggedInUserId);
				// Sort userResponseList based on latestMsgTime in descending order
				Comparator<ChatUserWebModel> comparator = Comparator.comparing(ChatUserWebModel::getLatestMsgTime);
				userResponseList.sort(comparator.reversed());
				return ResponseEntity.ok(userResponseList);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error("Error occurred while retrieving users -> {}", e.getMessage());
			e.printStackTrace();
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
			this.getLatestChatMessage(user, chatUserWebModel); // To display in the chat user list
			int unreadCount = chatRepository.countUnreadMessages(user.getUserId(), loggedInUserId);
			chatUserWebModel.setReceiverUnreadCount(unreadCount);
			return chatUserWebModel;
		}).collect(Collectors.toList());
	}

	public void getLatestChatMessage(User user, ChatUserWebModel chatUserWebModel) {
		String latestMsg = "";
		Date latestMsgTime = null;
		try {
			Integer loggedInUserId = userDetails.userInfo().getId();
			Optional<Chat> lastChat = chatRepository.getLatestMessage(loggedInUserId, user.getUserId());
			if (lastChat.isPresent()) {
				logger.debug("Latest chat details -> {}", lastChat.get());
				if (!Utility.isNullOrBlankWithTrim(lastChat.get().getMessage())) {
					latestMsg = lastChat.get().getMessage();
				} else {
					List<FileOutputWebModel> files = mediaFilesService
							.getMediaFilesByCategoryAndRefId(MediaFileCategory.Chat, lastChat.get().getChatId())
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
				latestMsgTime = lastChat.get().getTimeStamp();
			}
		} catch (Exception e) {
			logger.error("Error while getting latest chat message -> {}", e.getMessage());
			e.printStackTrace();
		}
		chatUserWebModel.setLatestMessage(latestMsg);
		chatUserWebModel.setLatestMsgTime(latestMsgTime);
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
	        allMessages.addAll(senderMessages);
	        allMessages.addAll(receiverMessages);

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
	            Optional<User> userData = userRepository.findById(chat.getChatSenderId());
	            Optional<User> userDatas = userRepository.findById(receiverId);

	            // Fetch profile picture URLs
	            String senderProfilePicUrl = userService.getProfilePicUrl(chat.getChatSenderId());
	            String receiverProfilePicUrl = userService.getProfilePicUrl(chat.getChatReceiverId());

	            if (userData.isPresent()) {
	                List<FileOutputWebModel> mediaFiles = mediaFilesService
	                        .getMediaFilesByCategoryAndRefId(MediaFileCategory.Chat, chat.getChatId());
	                ChatWebModel chatWebModel = ChatWebModel.builder().chatId(chat.getChatId())
	                        .chatSenderId(chat.getChatSenderId()).chatReceiverId(chat.getChatReceiverId())
	                        .chatIsActive(chat.getChatIsActive()).chatCreatedBy(chat.getChatCreatedBy())
	                        .chatCreatedOn(chat.getChatCreatedOn()).senderProfilePic(senderProfilePicUrl)
	                        .receiverProfilePic(receiverProfilePicUrl)
	                        .chatUpdatedBy(chat.getChatUpdatedBy()).chatUpdatedOn(chat.getChatUpdatedOn())
	                        .receiverRead(chat.getReceiverRead()).senderRead(chat.getSenderRead())
	                        .chatFiles(mediaFiles).message(chat.getMessage())
	                        .userType(userData.get().getUserType()).userAccountName(userData.get().getName())
	                        .receiverAccountName(userDatas.get().getName()).userId(userData.get().getUserId()).build();

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

	@Override
	public Response getAllSearchByChat(String searchKey) {
		try {
			List<User> users = userRepository.findByNameContainingIgnoreCaseAndStatus(searchKey, true);
			if (!Utility.isNullOrEmptyList(users)) {
				List<ChatUserWebModel> responseList = this.transformsUserDetailsForChat(users);
				return new Response(1, "Success", responseList);
			} else {
				return new Response(-1, "User not found...", null);
			}
		} catch (Exception e) {
			logger.error("Error while fetching users by search key -> {}", e.getMessage());
			e.printStackTrace();
			return new Response(-1, "Error", e.getMessage());
		}

	}

	private List<ChatUserWebModel> transformsUserDetailsForChat(List<User> users) {
		return users.stream().map(user -> {
			ChatUserWebModel chatUserWebModel = new ChatUserWebModel();
			chatUserWebModel.setUserId(user.getUserId());
			chatUserWebModel.setUserName(user.getName());
			chatUserWebModel.setUserType(user.getUserType());
			chatUserWebModel.setProfilePicUrl(userService.getProfilePicUrl(user.getUserId()));
			this.getLatestChatMessage(user, chatUserWebModel); // To display in the chat user list
			return chatUserWebModel;
		}).collect(Collectors.toList());
	}

	@Override
	public Response getInAppNotification() {
	    try {
	        Integer userId = userDetails.userInfo().getId();
	        // Fetch notifications for the given userId
	        List<InAppNotification> notifications = inAppNotificationRepository
	                .findByReceiverIdOrderByCreatedOnDesc(userId);

	        if (notifications.isEmpty()) {
	            return new Response(0, "No Notifications", "No notifications found for the given user ID");
	        }
	        // Initialize a counter for unread notifications
	        long unreadCount = notifications.stream()
	                                        .filter(notification -> notification.getIsRead() == true)
	                                        .count();
	       
	        List<InAppNotificationWebModel> notificationDtos = notifications.stream().map(notification -> {
	        	InAppNotificationWebModel dto = new InAppNotificationWebModel();
	            dto.setInAppNotificationId(notification.getInAppNotificationId());
	            dto.setSenderId(notification.getSenderId());
	            dto.setProfilePicUrl(userService.getProfilePicUrl(notification.getSenderId())); // Fetch profile picture URL
	            dto.setReceiverId(notification.getReceiverId());
	            dto.setTitle(notification.getTitle());
	            dto.setMessage(notification.getMessage());
	            dto.setCreatedOn(notification.getCreatedOn());
	            dto.setIsRead(notification.getIsRead());
	            
	            // Fetch user details using senderId
	            Optional<User> sender = userRepository.getByUserId(notification.getSenderId());
	            dto.setSenderName(sender.get().getName());

	            dto.setCreatedBy(notification.getCreatedBy());
	            dto.setUpdatedBy(notification.getUpdatedBy());
	            dto.setUpdatedOn(notification.getUpdatedOn());
	            dto.setUserType(notification.getUserType());
	            dto.setId(notification.getId());
	            dto.setPostId(notification.getPostId());
	            return dto;
	        }).collect(Collectors.toList());
	        
	        // Include the unread count in the response
	        Map<String, Object> response = new HashMap<>();
	        response.put("notifications", notificationDtos);
	        response.put("unreadCount", unreadCount);
	        return new Response(1, "Success", response);

	       // return new Response(1, "Success", notificationDtos);
	    } catch (Exception e) {
	        logger.error("Error occurred while fetching notifications -> {}", e.getMessage());
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
	public Response deleteByChatId(ChatWebModel chatWebModel) {
	    try {
	        Optional<Chat> chatData = chatRepository.findById(chatWebModel.getChatId());
	        if (chatData.isPresent()) {
	            Chat data = chatData.get();
	            data.setChatIsActive(false);
	            chatRepository.save(data); // Save the updated chat object
	            // Update mediaFiles status where refId matches chatId
	            List<MediaFiles> mediaFiles = mediaFilesRepository.findByCategoryRefId(chatWebModel.getChatId());
	            for (MediaFiles mediaFile : mediaFiles) {
	                mediaFile.setStatus(false);
	                mediaFilesRepository.save(mediaFile); // Save the updated media file object
	            }
	            return new Response(1,"success","Chat deactivated successfully");
	        } else {
	            return new Response(0,"Not Found","Chat not found");
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); // Log the exception for debugging
	        return new Response(0, "Error","An error occurred while deactivating the chat");
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

}
