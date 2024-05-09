package com.annular.filmhook.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.FriendRequest;
import com.annular.filmhook.repository.FriendRequestRepository;
import com.annular.filmhook.service.FriendRequestService;
import com.annular.filmhook.webmodel.FriendRequestWebModel;

@Service
public class FriendRequestServiceImpl implements FriendRequestService {

	@Autowired
	UserDetails userDetails;

	@Autowired
	FriendRequestRepository friendRequestRepository;

	@Override
	public ResponseEntity<?> saveFriendRequest(FriendRequestWebModel friendRequestWebModel) {
		try {
			Integer senderId = userDetails.userInfo().getId();
			Integer receiverId = friendRequestWebModel.getFriendRequestReceiverId();
			String status = friendRequestWebModel.getFriendRequestStatus();

			Optional<FriendRequest> existingFriendRequest1 = friendRequestRepository
					.findByFrientRequestSenderIdAndFriendRequestReceiverId(senderId, receiverId);
			Optional<FriendRequest> existingFriendRequest2 = friendRequestRepository
					.findByFrientRequestSenderIdAndFriendRequestReceiverId(receiverId, senderId);

			if (existingFriendRequest1.isPresent() || existingFriendRequest2.isPresent()) {

				return ResponseEntity.badRequest().body("Friend request already exists");
			} else {

				FriendRequest friendRequest = new FriendRequest();
				friendRequest.setFrientRequestSenderId(senderId);
				friendRequest.setFriendRequestReceiverId(receiverId);
				friendRequest.setFriendRequestSenderStatus(status);
				friendRequest.setFriendRequestCreatedBy(senderId);
				friendRequest.setFriendRequestIsActive(true);

				FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequest);

				// Return response entity indicating success
				return ResponseEntity.ok().body("Friend request saved successfully");
			}
		} catch (Exception e) {
			// Log the exception or handle it appropriately
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while saving the friend request");
		}
	}

	@Override
	public ResponseEntity<?> getFriendRequest(Integer userId, String friendRequestSenderStatus) {
		try {

			List<FriendRequest> friendRequests = friendRequestRepository
					.findByFrientRequestSenderIdAndFriendRequestSenderStatus(userId, friendRequestSenderStatus);

			return ResponseEntity.ok().body(friendRequests);
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while fetching friend requests");
		}
	}
}
