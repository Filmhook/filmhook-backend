package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;

import com.annular.filmhook.model.Chat;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.User;

import com.annular.filmhook.repository.ChatRepository;
import com.annular.filmhook.repository.UserRepository;

import com.annular.filmhook.service.ChatService;
import com.annular.filmhook.service.MediaFilesService;

import com.annular.filmhook.util.Utility;

import com.annular.filmhook.webmodel.ChatWebModel;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

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

    public static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);

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
                        .userAccountName(user.getName()) // Assuming 'getName()' returns the user's name
                        .chatSenderId(userId)
                        .userType(user.getUserType())
                        .chatIsActive(true) // Assuming chat is active by default
                        .chatCreatedBy(userId)
                        .chatCreatedOn(new Date())
                        .build();
                chatRepository.save(chat);

                if (!Utility.isNullOrEmptyList(chatWebModel.getFiles())) {
                    // Saving the chat files in the media_files table
                    FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
                            .userId(chatWebModel.getUserId())
                            .category(MediaFileCategory.Chat)
                            .categoryRefId(chat.getChatId())
                            .files(chatWebModel.getFiles())
                            .build();
                    mediaFilesService.saveMediaFiles(fileInputWebModel, userOptional.get());
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

            // Fetch all distinct user IDs associated with the logged-in user from ChatRepository
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

            // If users exist, map them to a list of simplified user models containing ID, name, and profile picture URLs
            if (!users.isEmpty()) {
                List<Map<String, Object>> userResponseList = users.stream()
                        .map(user -> {
                            Map<String, Object> userData = new LinkedHashMap<>();
                            userData.put("userId", user.getUserId());
                            userData.put("userName", Utility.isNullOrBlankWithTrim(user.getName()) ? "" : user.getName());
                            userData.put("profilePicUrl", userService.getProfilePicUrl(user.getUserId())); // Fetch profile pictures URLs for the user if available
                            this.getLatestChatMessage(loggedInUserId, user, userData); // To display in the chat user list
                            return userData;
                        })
                        .collect(Collectors.toList());

               //  Sort userResponseList based on latestMsgTime in descending order
                Comparator<Map<String, Object>> comparator = Comparator.comparing(data -> (Date) data.get("latestMsgTime"));
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

    public void getLatestChatMessage(Integer loggedInUserId, User user, Map<String, Object> dataMap) {
        String latestMsg = "";
		Date latestMsgTime = null;
        try {
            Pageable pageable = PageRequest.of(0, 1);
            List<Chat> chatList = chatRepository.findTop1ByChatSenderIdAndChatReceiverIdOrderByTimeStampDesc(loggedInUserId, user.getUserId(), pageable);

            if (!chatList.isEmpty()) {
                Chat lastChat = chatList.get(0);  // Get the latest message
                if (!Utility.isNullOrBlankWithTrim(lastChat.getMessage())) {
                    latestMsg = lastChat.getMessage();
                    latestMsgTime = lastChat.getChatCreatedOn();
                    System.out.print("lastMessage"+lastChat.getChatCreatedOn());
                    System.out.println("chatId"+lastChat.getChatId());
                } else {
                    List<FileOutputWebModel> files = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Chat, lastChat.getChatId()).stream()
                            .sorted(Comparator.comparing(FileOutputWebModel::getId).reversed())
                            .collect(Collectors.toList());
                    if (!Utility.isNullOrEmptyList(files)) {
                        String fileType = files.get(files.size() - 1).getFileType();
                        if (FileUtil.isImageFile(fileType)) latestMsg = "Photo";
                        else if (FileUtil.isVideoFile(fileType)) latestMsg = "Audio/Video";
                        System.out.print("audio"+files.get(0).getCreatedOn());
                    }
                }
                latestMsgTime = lastChat.getChatCreatedOn();
            }
        } catch (Exception e) {
            logger.error("Error while getting latest chat message -> {}", e.getMessage());
            e.printStackTrace();
        }
        dataMap.put("latestMessage", latestMsg);
        dataMap.put("latestMsgTime", latestMsgTime);
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

            // Combine both lists of messages without duplicates
            Set<Chat> allMessages = new HashSet<>();
            allMessages.addAll(senderMessages);
            allMessages.addAll(receiverMessages);

            // Construct the response structure
            List<ChatWebModel> messagesWithFiles = new ArrayList<>();
            for (Chat chat : allMessages) {
                Optional<User> userData = userRepository.findById(chat.getChatSenderId());
                if (userData.isPresent()) {
                    List<FileOutputWebModel> mediaFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Chat, chat.getChatId());
                    ChatWebModel chatWebModel = ChatWebModel.builder()
                            .chatId(chat.getChatId())
                            .chatSenderId(chat.getChatSenderId())
                            .chatReceiverId(chat.getChatReceiverId())
                            .chatIsActive(chat.getChatIsActive())
                            .chatCreatedBy(chat.getChatCreatedBy())
                            .chatCreatedOn(chat.getChatCreatedOn())
                            .chatUpdatedBy(chat.getChatUpdatedBy())
                            .chatUpdatedOn(chat.getChatUpdatedOn())
                            .chatFiles(mediaFiles)
                            .message(chat.getMessage())
                            .userType(userData.get().getUserType())
                            .userAccountName(userData.get().getName())
                            .userId(userData.get().getUserId())
                            .build();
                    messagesWithFiles.add(chatWebModel);
                }
            }

            // Sort messagesWithFiles by chatId
            messagesWithFiles.sort(Comparator.comparing(ChatWebModel::getChatId));

            // Put the final response together
            response.put("userChat", messagesWithFiles);
            response.put("numberOfItems", messagesWithFiles.size());

            logger.info("Get Messages by User ID Method End");
            return ResponseEntity.ok(new Response(1, "Success", response));
        } catch (Exception e) {
            logger.error("Error occurred while retrieving messages -> {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new Response(-1, "Internal Server Error", ""));
        }
    }

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
            return ResponseEntity.internalServerError().body("Error occurred while retrieving Firebase token: " + e.getMessage());
        }
    }

    @Override
    public Response getLastMessageById(ChatWebModel message) {
        try {
            List<Chat> lastMessages = chatRepository.findTopByChatSenderIdAndChatReceiverIdOrderByTimeStampDesc(message.getChatSenderId(), message.getChatReceiverId());
            logger.info("message{}", message.getChatSenderId());
            logger.info("message1{}", message.getChatReceiverId());
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
            List<User> users = userRepository.findBySearchName(searchKey);
            Integer loggedInUserId = userDetails.userInfo().getId(); // Assuming userDetails provides logged-in user info
            List<Map<String, Object>> responseList = users.stream().map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userId", user.getUserId());
                userMap.put("userName", user.getName());
               // userMap.put("userType", user.getUserType());
                userMap.put("profilePicUrl", userService.getProfilePicUrl(user.getUserId()));

                try {
                    List<Chat> chatList = chatRepository.findTop1ByChatSenderIdAndChatReceiverIdOrderByTimeStampDesc(loggedInUserId, user.getUserId(), PageRequest.of(0, 1));

                    if (!chatList.isEmpty()) {
                        Chat lastChat = chatList.get(0);  // Get the latest message
                        String latestMsg = "";
                        Date latestMsgTime = null;

                        if (!Utility.isNullOrBlankWithTrim(lastChat.getMessage())) {
                            latestMsg = lastChat.getMessage();
                            latestMsgTime = lastChat.getChatCreatedOn();
                            System.out.print("lastMessage"+lastChat.getChatCreatedOn());
                        } else {
                            List<FileOutputWebModel> files = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Chat, lastChat.getChatId()).stream()
                                    .sorted(Comparator.comparing(FileOutputWebModel::getId).reversed())
                                    .collect(Collectors.toList());
                            if (!Utility.isNullOrEmptyList(files)) {
                                String fileType = files.get(files.size() - 1).getFileType();
                                if (FileUtil.isImageFile(fileType)) {
                                    latestMsg = "Photo";
                                } else if (FileUtil.isVideoFile(fileType)) {
                                    latestMsg = "Audio/Video";
                                }
                                latestMsgTime = files.get(0).getCreatedOn();
                                System.out.print("audio"+files.get(0).getCreatedOn());
                            }
                        }
                        userMap.put("latestMessage", latestMsg);
                        userMap.put("latestMsgTime", latestMsgTime);
                    }
                } catch (Exception e) {
                    logger.error("Error while fetching latest chat for user {}", user.getUserId(), e);
                }

                return userMap;
            }).collect(Collectors.toList());

            return new Response(1, "Success", responseList);
        } catch (Exception e) {
            logger.error("Error while fetching users by search key {}", searchKey, e);
            return new Response(-1, "Error", e.getMessage());
        }
    }




}
