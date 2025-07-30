package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.ChatService;
import com.annular.filmhook.service.FcmService;
import com.annular.filmhook.webmodel.ChatWebModel;
import com.annular.filmhook.webmodel.FCMRequestWebModel;
import com.annular.filmhook.webmodel.InAppNotificationWebModel;
import com.annular.filmhook.webmodel.UserWebModel;

@RestController
@RequestMapping("/chat")
public class ChatController {

	public static final Logger logger = LoggerFactory.getLogger(ChatController.class);

	@Autowired
	ChatService chatService;

	@Autowired
	FcmService fcmService;

	@RequestMapping(path = "/saveMessage", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> saveMessage(@ModelAttribute ChatWebModel chatWebModel) {
		try {
			logger.info("saveMessage controller start");
			return chatService.saveMessage(chatWebModel);
		} catch (Exception e) {
			logger.error("saveMessage Method Exception {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@GetMapping("/getAllUser")
	public ResponseEntity<?> getAllUser() {
		try {
			logger.info("getAllUser controller start");
			return chatService.getAllUser();
		} catch (Exception e) {
			logger.error("getAllUser Method Exception {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@PostMapping("/getMessageByUserId")
	public ResponseEntity<?> getMessageByUserId(@RequestBody ChatWebModel chatWebModel) {
		try {
			logger.info("getMessageByUserIde controller start");
			return chatService.getMessageByUserId(chatWebModel);
		} catch (Exception e) {
			logger.error("getMessageByUserId Method Exception {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@PostMapping("/send-fcm-message")
	public ResponseEntity<?> sendFCMMessage(@RequestBody FCMRequestWebModel request) {
		try {
			fcmService.sendFCMMessage(request);
			return ResponseEntity.ok("FCM message sent successfully.");
		} catch (Exception e) {
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
	}

	@GetMapping("/getFirebaseTokenByuserId")
	public ResponseEntity<?> getFirebaseTokenByUserId(@RequestParam("userId") Integer userId) {
		try {
			logger.info("getFirebaseTokenByUserId controller start");
			return chatService.getFirebaseTokenByUserId(userId);
		} catch (Exception e) {
			logger.error("getFirebaseTokenByUserId Method Exception {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Fail", ""));
		}
	}

	@PostMapping("getLastMessagebyid")
	public ResponseEntity<Response> getLastMessageById(@RequestBody ChatWebModel message) {
		try {
			Response response = chatService.getLastMessageById(message);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			logger.error("Error at getLastMessageById -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Error", e.getMessage()));
		}
	}

	@GetMapping("getAllSearchByChat")
	public ResponseEntity<Response> getAllSearchByChat(@RequestParam("searchKey") String searchKey) {
		try {
			Response response = chatService.getAllSearchByChat(searchKey);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			logger.error("Error at getAllSearchByChat -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Error", e.getMessage()));
		}
	}

	@GetMapping("getInAppNotification")
	public ResponseEntity<Response> getInAppNotification() {
		try {
			Response response = chatService.getInAppNotification();
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			logger.error("Error at getInAppNotification -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Error", e.getMessage()));
		}
	}

	@PostMapping("updateInAppNotification")
	public ResponseEntity<Response> updateInAppNotification(
			@RequestBody InAppNotificationWebModel inAppNotificationWebModel) {
		try {
			Response response = chatService.updateInAppNotification(inAppNotificationWebModel);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			logger.error("Error at getInAppNotification -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Error", e.getMessage()));
		}
	}

	@PostMapping("/deleteByChatId")
	public ResponseEntity<Response> deleteByChatId(@RequestBody ChatWebModel chatWebModel) {
	    try {
	        Response response = chatService.deleteChatMessage(chatWebModel);

	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        logger.error("Error while deleting chat message for chatId {}: {}", chatWebModel.getChatId(), e.getMessage(), e);
	        return ResponseEntity.internalServerError()
	                .body(new Response(-1, "Error", "Something went wrong while deleting the chat message"));
	    }
	}

	@PostMapping("updateOnlineStatus")
	public ResponseEntity<Response> updateOnlineStatus(@RequestBody UserWebModel userWebModel) {
		try {
			Response response = chatService.updateOnlineStatus(userWebModel);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			logger.error("Error at userWebModel -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Response(-1, "Error", e.getMessage()));
		}

	}
	
	@PostMapping("/deleteChatProfile")
	public ResponseEntity<Response> deleteChatProfile(@RequestBody ChatWebModel chatWebModel) {
	    try {
	        Integer currentUserId = chatWebModel.getChatSenderId();
	        Integer targetUserId = chatWebModel.getChatReceiverId();

	        Response response = chatService.deleteChatProfile(currentUserId, targetUserId);
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        logger.error("Error in deleteChatProfile: {}", e.getMessage(), e);
	        return ResponseEntity.internalServerError()
	                .body(new Response(-1, "Error", "Internal server error"));
	    }
	}

}
