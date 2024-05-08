package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.ChatWebModel;

public interface ChatService {

	ResponseEntity<?> saveMessage(ChatWebModel chatWebModel);

	ResponseEntity<?> getAllUser(ChatWebModel chatWebModel);

	ResponseEntity<?> getMessageByUserId(ChatWebModel chatWebModel);

	ResponseEntity<?> getFirebaseTokenByuserId(Integer userId);

}
