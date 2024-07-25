package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.Response;
import com.annular.filmhook.webmodel.ChatWebModel;
import com.annular.filmhook.webmodel.InAppNotificationWebModel;

public interface ChatService {

    ResponseEntity<?> saveMessage(ChatWebModel chatWebModel);

    ResponseEntity<?> getAllUser();

    ResponseEntity<?> getMessageByUserId(ChatWebModel chatWebModel);

    ResponseEntity<?> getFirebaseTokenByUserId(Integer userId);

	Response getLastMessageById(ChatWebModel message);

	Response getAllSearchByChat(String searchKey);

	Response getInAppNotification();

	Response updateInAppNotification(InAppNotificationWebModel inAppNotificationWebModel);

}
