package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.FollowersRequest;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.FriendRequestRepository;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.FriendRequestService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.FollowersRequestWebModel;

@Service
public class FriendRequestServiceImpl implements FriendRequestService {

	public static final Logger logger = LoggerFactory.getLogger(FriendRequestServiceImpl.class);

	@Autowired
	UserDetails userDetails;

	@Autowired
	FriendRequestRepository friendRequestRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	S3Util s3Util;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	MediaFilesRepository mediaFilesRepository;

	@Override
	public ResponseEntity<?> saveFollowersRequest(FollowersRequestWebModel followersRequestWebModel) {
		try {
			Integer senderId = followersRequestWebModel.getFollowersRequestSenderId();
			Integer receiverId = followersRequestWebModel.getFollowersRequestReceiverId();

			// Check if senderId and receiverId are not null
			if (senderId == null || receiverId == null) {
				return ResponseEntity.badRequest().body("Sender ID or Receiver ID cannot be null");
			}

			// Check if the senderId and receiverId are different
			if (senderId.equals(receiverId)) {
				return ResponseEntity.badRequest().body("Sender ID and Receiver ID cannot be the same");
			}

			// Check if the sender already sent a request to the receiver
			Optional<FollowersRequest> existingFriendRequest = friendRequestRepository.findByFollowersRequestSenderIdAndFollowersRequestReceiverId(senderId, receiverId);

			if (existingFriendRequest.isPresent()) {
				// If a friend request already exists, return a bad request response
				return ResponseEntity.badRequest().body("User Already followed by user id -> " + senderId);
			} else {
				// If no existing request, proceed to save the new friend request
				FollowersRequest request = new FollowersRequest();
				request.setFollowersRequestSenderId(senderId);
				request.setFollowersRequestReceiverId(receiverId);
				request.setFollowersRequestStatus("Followed");
				request.setFollowersRequestCreatedBy(senderId);
				request.setFollowersRequestCreatedOn(new Date());
				request.setFollowersRequestIsActive(true);
				friendRequestRepository.save(request);
				return ResponseEntity.ok().body("User followed successfully...");
			}
		} catch (Exception e) {
			logger.error("Error at saveFollowersRequest() -> {}", e.getMessage());
			// Log the exception or handle it appropriately
			return ResponseEntity.internalServerError().body("An error occurred while saving the friend request: " + e.getMessage());
		}
	}

	@Override
	public ResponseEntity<?> updateFriendRequest(FollowersRequestWebModel followersRequestWebModel) {
		try {
			Integer senderId = followersRequestWebModel.getFollowersRequestSenderId();
			Integer receiverId = followersRequestWebModel.getFollowersRequestReceiverId();

			if (senderId == null || receiverId == null) {
				return ResponseEntity.badRequest().body("Sender ID or Receiver ID is null");
			}

            /*Optional<FollowersRequest> existingFriendRequestOpt = friendRequestRepository.findByFollowersRequestSenderIdAndFollowersRequestReceiverId(senderId, receiverId);
            Optional<FollowersRequest> existingFriendRequestOpt2 = friendRequestRepository.findByFollowersRequestSenderIdAndFollowersRequestReceiverId(receiverId, senderId);

            if (existingFriendRequestOpt.isPresent()) {
                FollowersRequest existingFriendRequest = existingFriendRequestOpt.get();

                existingFriendRequest.setFollowersRequestSenderStatus(newStatus);//confirm
                friendRequestRepository.save(existingFriendRequest);

                if (existingFriendRequestOpt2.isPresent()) {
                    FollowersRequest existingFriendRequests = existingFriendRequestOpt.get();

                    if ("confirm".equalsIgnoreCase(existingFriendRequest.getFollowersRequestSenderStatus())) {
                        existingFriendRequests.setFollowersRequestSenderStatus("unfollow");//confirm--->unfollow
                        existingFriendRequest.setFollowersRequestSenderStatus("unfollow");
                    } else if ("Reject".equalsIgnoreCase(existingFriendRequest.getFollowersRequestSenderStatus())) {
                        existingFriendRequests.setFollowersRequestSenderStatus("follow");
                        existingFriendRequest.setFollowersRequestSenderStatus("follow");
                    }

                    friendRequestRepository.save(existingFriendRequest);
                }
                // return ResponseEntity.ok().body("Friend request updated successfully");
            }*/

			Optional<FollowersRequest> existingFriendRequest = friendRequestRepository.findByFollowersRequestSenderIdAndFollowersRequestReceiverId(senderId, receiverId);
			if (existingFriendRequest.isPresent()) {
				existingFriendRequest.get().setFollowersRequestStatus("UnFollowed");
				friendRequestRepository.saveAndFlush(existingFriendRequest.get());
			}
		} catch (Exception e) {
			logger.error("Error at updateFriendRequest() -> {}", e.getMessage());
			// Log the exception or handle it appropriately
			return ResponseEntity.internalServerError().body("An error occurred while updating the friend request: " + e.getMessage());
		}
		return ResponseEntity.ok().body("Unfollowed the user successfully...");
	}


	@Override
	public ResponseEntity<?> getFriendRequest(Integer userId) {
		List<FollowersRequestWebModel> responseList = new ArrayList<>();
		try {
			List<FollowersRequest> followersList = friendRequestRepository.findByFollowersRequestReceiverIdAndFollowersRequestIsActive(userId, true);
			// Iterating received-request list
			followersList.stream()
					.filter(Objects::nonNull)
					.forEach(request -> {
						Optional<User> user = userRepository.findById(request.getFollowersRequestSenderId());
						if (user.isPresent()) {
							User receiverUser = user.get();

							FollowersRequestWebModel.FollowersRequestWebModelBuilder followersRequestWebModel = FollowersRequestWebModel.builder()
									.followersRequestId(request.getFollowersRequestId())
									.followersRequestSenderId(request.getFollowersRequestSenderId())
									.followersRequestReceiverId(request.getFollowersRequestReceiverId())
									.followersRequestIsActive(request.getFollowersRequestIsActive())
									.userType(request.getUserType())
									.followersRequestCreatedBy(request.getFollowersRequestCreatedBy())
									.followersRequestCreatedOn(request.getFollowersRequestCreatedOn())
									.followersRequestUpdatedBy(request.getFollowersRequestUpdatedBy())
									.followersRequestUpdatedOn(request.getFollowersRequestUpdatedOn());

							Optional<MediaFiles> profilePic = mediaFilesRepository.findByUserIdAndCategory(receiverUser.getUserId(), MediaFileCategory.ProfilePic);
							profilePic.ifPresent(mediaFiles -> followersRequestWebModel.receiverProfilePicUrl(s3Util.generateS3FilePath(mediaFiles.getFilePath() + mediaFiles.getFileType())));

							responseList.add(followersRequestWebModel.build());
						}
					});

			return ResponseEntity.ok().body(responseList);
		} catch (Exception e) {
			logger.error("Error at getFriendRequest() -> {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("An error occurred while fetching friend requests");
		}
	}

}
