package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.ChatRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.ChatService;
import com.annular.filmhook.webmodel.ChatWebModel;




@Service
public class ChatServiceImpl implements ChatService{
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ChatRepository chatRepository;
	
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

	        // If users exist, map them to a list of simplified user models containing only ID and name
	        if (!users.isEmpty()) {
	            List<Map<String, Object>> userResponseList = users.stream()
	                    .map(user -> {
	                        Map<String, Object> userData = new HashMap<>();
	                        userData.put("userId", user.getUserId());
	                        userData.put("userName", user.getName());
	                        return userData;
	                    })
	                    .collect(Collectors.toList());

	            return ResponseEntity.ok(userResponseList);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    } catch (Exception e) {
	        logger.error("Error occurred while retrieving users: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	@Override
	public ResponseEntity<?> getMessageByUserId(ChatWebModel message) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        User user = userRepository.findById(message.getChatReceiverId()).orElse(null);

//	        if (user == null) {
//	            return new Response(-1, "User not found", "");
//	        }
	        logger.info("Get Messages by User ID Method Start");
           Integer senderId = userDetails.userInfo().getId();
	        List<Chat> senderMessages = chatRepository.getMessageListBySenderIdAndReceiverId(senderId,
	                message.getChatReceiverId());
	        List<Chat> receiverMessages = chatRepository
	                .getMessageListBySenderIdAndReceiverId(message.getChatReceiverId(), message.getChatSenderId());

	        List<Chat> allMessages = new ArrayList<>();
	        allMessages.addAll(senderMessages);
	        allMessages.addAll(receiverMessages);

	        response.put("userChat", allMessages);
	        response.put("numberOfItems", allMessages.size());

	    } catch (Exception e) {
	        logger.error("Error occurred while retrieving messages: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

	}

    
