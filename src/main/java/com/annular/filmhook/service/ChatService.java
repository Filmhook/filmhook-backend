package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.Response;
import com.annular.filmhook.webmodel.ChatWebModel;
import com.annular.filmhook.webmodel.InAppNotificationWebModel;
import com.annular.filmhook.webmodel.UserWebModel;

public interface ChatService {

    ResponseEntity<?> saveMessage(ChatWebModel chatWebModel);

    ResponseEntity<?> getAllUser();

    ResponseEntity<?> getMessageByUserId(ChatWebModel chatWebModel);

    ResponseEntity<?> getFirebaseTokenByUserId(Integer userId);

	Response getLastMessageById(ChatWebModel message);

	Response getAllSearchByChat(String searchKey);

	Response getInAppNotification();

	Response updateInAppNotification(InAppNotificationWebModel inAppNotificationWebModel);

	Response deleteChatMessage(ChatWebModel chatWebModel);

	Response updateOnlineStatus(UserWebModel userWebModel);
	
	Response deleteChatProfile(Integer currentUserId, Integer targetUserId);

}
