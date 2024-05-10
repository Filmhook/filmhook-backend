package com.annular.filmhook.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.FollowersRequest;
import com.annular.filmhook.repository.FriendRequestRepository;
import com.annular.filmhook.service.FriendRequestService;
import com.annular.filmhook.webmodel.FollowersRequestWebModel;

@Service
public class FriendRequestServiceImpl implements FriendRequestService {

	@Autowired
	UserDetails userDetails;

	@Autowired
	FriendRequestRepository friendRequestRepository;

	@Override
	public ResponseEntity<?> saveFollowersRequest(FollowersRequestWebModel followersRequestWebModel) {

		try {
			Integer senderId = userDetails.userInfo().getId();
			Integer receiverId = followersRequestWebModel.getFollwersRequestReceiverId();
			String status = followersRequestWebModel.getFollwersRequestSenderStatus();

			Optional<FollowersRequest> existingFriendRequest1 = friendRequestRepository
					.findByFrientRequestSenderIdAndFriendRequestReceiverId(senderId, receiverId);

			if (existingFriendRequest1.isPresent()) {
				// If a friend request already exists, return a bad request response
				return ResponseEntity.badRequest()
						.body(new Response(0,"Friend request already exists", HttpStatus.BAD_REQUEST));
			} else {
				// If no existing request, proceed to save the new friend request
				FollowersRequest friendRequest = new FollowersRequest();
				friendRequest.setFollwersRequestSenderId(senderId);
				friendRequest.setFollwersRequestReceiverId(receiverId);
				friendRequest.setFollwersRequestSenderStatus(status);
				friendRequest.setFollwersRequestCreatedBy(senderId);
				friendRequest.setFollwersRequestIsActive(true);

				FollowersRequest savedFriendRequest = friendRequestRepository.save(friendRequest);

				// Return a success response

			}

			return ResponseEntity.ok().body("Friend request saved successfully");

		} catch (Exception e) {
			// Log the exception or handle it appropriately
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while saving the friend request");
		}
	}

//	@Override
//	public ResponseEntity<?> getFriendRequest(Integer userId, String friendRequestSenderStatus) {
//		try {
//
//			List<FollowersRequest> friendRequests = friendRequestRepository
//					.findByFrientRequestSenderIdAndFriendRequestSenderStatus(userId, friendRequestSenderStatus);
//
//			return ResponseEntity.ok().body(friendRequests);
//		} catch (Exception e) {
//
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body("An error occurred while fetching friend requests");
//		}
//	}
}
