package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.ChatService;
import com.annular.filmhook.webmodel.BlockWebModel;
import com.annular.filmhook.webmodel.ChatWebModel;

@RestController
@RequestMapping("/chat")
public class ChatController {
	
	@Autowired
	ChatService chatService;
	
	public static final Logger logger = LoggerFactory.getLogger(ChatController.class);
	
	@PostMapping("/saveMessage")
	public ResponseEntity<?> saveMessage(@RequestBody ChatWebModel chatWebModel) {
		try {
			logger.info("saveMessage controller start");
			return chatService.saveMessage(chatWebModel);
		} catch (Exception e) {
			logger.error("saveMessage Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}
	@PostMapping("/getAllUser")
	public ResponseEntity<?> getAllUser(@RequestBody ChatWebModel chatWebModel) {
		try {
			logger.info("getAllUser controller start");
			return chatService.getAllUser(chatWebModel);
		} catch (Exception e) {
			logger.error("getAllUser Method Exception {}" + e);
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
			logger.error("getMessageByUserId Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}
}
