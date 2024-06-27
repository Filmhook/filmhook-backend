package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.annular.filmhook.model.Chat;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.ChatRepository;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.ChatService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.S3Util;
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
    FileUtil fileUtil;
    
    @Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    S3Util s3Util;

    @Autowired
    MediaFilesRepository mediaFilesRepository;

    @Autowired
    UserDetails userDetails;

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
            logger.error("Error occurred while saving message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<?> getAllUser(ChatWebModel chatWebModel) {
        try {
            logger.info("Get All Users Method Start");

            // Assuming you have a method in your repository to fetch all users
            List<User> users = userRepository.findAll();

            // If users exist, map them to a list of simplified user models containing ID, name, and profile picture URLs
            if (!users.isEmpty()) {
                List<Map<String, Object>> userResponseList = users.stream()
                        .map(user -> {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("userId", user.getUserId());
                            userData.put("userName", user.getName());
                            // Fetch profile pictures URLs for the user if available
                            List<MediaFiles> mediaDataList = mediaFilesRepository.getMediaFilesByUserIdAndCategory(user.getUserId(), MediaFileCategory.ProfilePic);
                            List<String> profilePictureUrls = mediaDataList.stream()
                                    .map(media -> s3Util.generateS3FilePath(media.getFilePath() + media.getFileType()))
                                    .collect(Collectors.toList());
                            userData.put("profilePictureUrls", profilePictureUrls);
                            return userData;
                        })
                        .collect(Collectors.toList());

                return ResponseEntity.ok(userResponseList);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Override
    public ResponseEntity<?> getMessageByUserId(ChatWebModel message) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Fetch the user by receiver ID
            User user = userRepository.findById(message.getChatReceiverId()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(-1, "User not found", ""));
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

        } catch (Exception e) {
            logger.error("Error occurred while retrieving messages: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Internal Server Error", ""));
        }
        logger.info("Get Messages by User ID Method End");


        return ResponseEntity.ok(new Response(0, "Success", response));
    }

    @Override
    public ResponseEntity<?> getFirebaseTokenByuserId(Integer userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                String firebaseToken = userOptional.get().getFirebaseDeviceToken();
                if (firebaseToken != null) {
                    return ResponseEntity.ok(new Response(1, "Success", firebaseToken));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Firebase token not found for the user.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while retrieving Firebase token: " + e.getMessage());
        }
    }

    @Override
    public Response getLastMessageById(ChatWebModel message) {
        try {
            logger.info("getLastMessageById Method Start");

            List<Chat> lastMessages = chatRepository.findTop1ByChatSenderIdAndChatReceiverIdOrderByTimeStampDesc(
                    message.getChatSenderId(), message.getChatReceiverId());
System.out.println("message"+message.getChatSenderId());
System.out.println("message1"+message.getChatReceiverId());
            if (!lastMessages.isEmpty()) {
                Chat lastMessage = lastMessages.get(0);

                Map<String, Object> response = new HashMap<>();
                response.put("lastMessage", lastMessage);
                logger.info("getLastMessageById Method End");
                return new Response(1, "Success", response);
            } else {
                logger.info("getLastMessageById Method End");
                return new Response(-1, "No messages found between the sender and receiver", null);
            }
        } catch (Exception e) {
            logger.error("getLastMessageById Method Exception {}", e);
            return new Response(-1, "Error", e.getMessage());
        }
    }
	

}
