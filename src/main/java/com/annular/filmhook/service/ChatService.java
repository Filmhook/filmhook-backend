package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.Response;
import com.annular.filmhook.webmodel.ChatWebModel;

public interface ChatService {

	ResponseEntity<?> saveMessage(ChatWebModel chatWebModel);

    ResponseEntity<?> getAllUser();

	ResponseEntity<?> getMessageByUserId(ChatWebModel chatWebModel);

	ResponseEntity<?> getFirebaseTokenByuserId(Integer userId);

	Response getLastMessageById(ChatWebModel message);

}
