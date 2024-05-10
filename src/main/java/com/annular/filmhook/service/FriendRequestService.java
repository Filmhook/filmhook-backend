package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.FollowersRequestWebModel;


public interface FriendRequestService {

	//ResponseEntity<?> getFriendRequest(Integer userId,String friendRequestSenderStatus);

	ResponseEntity<?> saveFollowersRequest(FollowersRequestWebModel followersRequestWebModel);

}
