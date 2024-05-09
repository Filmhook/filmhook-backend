package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.FriendRequestWebModel;

public interface FriendRequestService {

	ResponseEntity<?> saveFriendRequest(FriendRequestWebModel friendRequestWebModel);

	ResponseEntity<?> getFriendRequest(Integer userId,String friendRequestSenderStatus);

}
