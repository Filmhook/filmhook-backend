package com.annular.filmhook.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Objects;
import java.util.Date;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.UserWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.FollowersRequest;
import com.annular.filmhook.repository.FriendRequestRepository;
import com.annular.filmhook.service.FriendRequestService;
import com.annular.filmhook.webmodel.FollowersRequestWebModel;

@Service
public class FriendRequestServiceImpl implements FriendRequestService {

    public static final Logger logger = LoggerFactory.getLogger(FriendRequestServiceImpl.class);

    public static final String FOLLOWED = "Followed";
    public static final String UNFOLLOWED = "UnFollowed";
    public static final String FOLLOWERS_LIST = "followersList";
    public static final String FOLLOWING_LIST = "followingList";
    public static final String FOLLOWERS = "followers";
    public static final String FOLLOWING = "following";

    @Autowired
    UserService userService;

    @Autowired
    FriendRequestRepository friendRequestRepository;

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

            if (existingFriendRequest.isPresent() && existingFriendRequest.get().getFollowersRequestStatus().equalsIgnoreCase(UNFOLLOWED)) {
                existingFriendRequest.get().setFollowersRequestStatus(FOLLOWED);
                friendRequestRepository.save(existingFriendRequest.get());
            } else {
                // If no existing request, proceed to save the new friend request
                FollowersRequest request = new FollowersRequest();
                request.setFollowersRequestSenderId(senderId);
                request.setFollowersRequestReceiverId(receiverId);
                request.setFollowersRequestStatus(FOLLOWED);
                request.setFollowersRequestCreatedBy(senderId);
                request.setFollowersRequestCreatedOn(new Date());
                request.setFollowersRequestIsActive(true);
                friendRequestRepository.save(request);
            }
            return ResponseEntity.ok().body("User followed successfully...");
        } catch (Exception e) {
            logger.error("Error at saveFollowersRequest() -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("An error occurred while saving the friend request: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> updateFriendRequest(FollowersRequestWebModel followersRequestWebModel) {
        try {
            Integer senderId = followersRequestWebModel.getFollowersRequestSenderId();
            Integer receiverId = followersRequestWebModel.getFollowersRequestReceiverId();
            if (senderId == null || receiverId == null)
                return ResponseEntity.badRequest().body("Sender ID or Receiver ID is null");

            Optional<FollowersRequest> existingFriendRequest = friendRequestRepository.findByFollowersRequestSenderIdAndFollowersRequestReceiverId(senderId, receiverId);
            if (existingFriendRequest.isPresent()) {
                existingFriendRequest.get().setFollowersRequestStatus(UNFOLLOWED);
                friendRequestRepository.saveAndFlush(existingFriendRequest.get());
            }
        } catch (Exception e) {
            logger.error("Error at updateFriendRequest() -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("An error occurred while updating the friend request: " + e.getMessage());
        }
        return ResponseEntity.ok().body("Unfollowed the user successfully...");
    }

    @Override
    public ResponseEntity<?> getFriendRequest(Integer userId) {
        Map<String, List<FollowersRequestWebModel>> responseMap = new HashMap<>();
        try {
            // Followed users list
            List<FollowersRequest> followersList = friendRequestRepository.findByFollowersRequestReceiverIdAndFollowersRequestIsActive(userId, true);
            responseMap.put(FOLLOWERS_LIST, this.transformUserData(followersList, FOLLOWERS));

            // Followed users list
            List<FollowersRequest> followingList = friendRequestRepository.findByFollowersRequestSenderIdAndFollowersRequestIsActive(userId, true);
            responseMap.put(FOLLOWING_LIST, this.transformUserData(followingList, FOLLOWING));

            return ResponseEntity.ok().body(new Response(1, "Success", responseMap));
        } catch (Exception e) {
            logger.error("Error at getFriendRequest() -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("An error occurred while fetching friend requests");
        }
    }

    private List<FollowersRequestWebModel> transformUserData(List<FollowersRequest> userList, String type) {
        List<FollowersRequestWebModel> outputList = new ArrayList<>();
        try {
            userList.stream()
                    .filter(Objects::nonNull)
                    .forEach(request -> {
                        FollowersRequestWebModel followersRequestWebModel = FollowersRequestWebModel.builder()
                                .followersRequestId(request.getFollowersRequestId())
                                .followersRequestSenderId(request.getFollowersRequestSenderId())
                                .followersRequestReceiverId(request.getFollowersRequestReceiverId())
                                .followersRequestStatus(request.getFollowersRequestStatus())
                                .followersRequestIsActive(request.getFollowersRequestIsActive())
                                .userProfilePicUrl(this.getProfilePicUrl(type.equalsIgnoreCase(FOLLOWERS) ? request.getFollowersRequestReceiverId() : request.getFollowersRequestSenderId()))
                                .userType(request.getUserType())
                                .followersRequestCreatedBy(request.getFollowersRequestCreatedBy())
                                .followersRequestCreatedOn(request.getFollowersRequestCreatedOn())
                                .followersRequestUpdatedBy(request.getFollowersRequestUpdatedBy())
                                .followersRequestUpdatedOn(request.getFollowersRequestUpdatedOn())
                                .build();
                        outputList.add(followersRequestWebModel);
                    });
        } catch (Exception e) {
            logger.error("Error at transformUserData -> {}", e.getMessage());
            e.printStackTrace();
        }
        return outputList;
    }

    private String getProfilePicUrl(Integer userId) {
        FileOutputWebModel profilePic = userService.getProfilePic(UserWebModel.builder().userId(userId).build());
        return profilePic != null ? profilePic.getFilePath() : "";
    }

}
