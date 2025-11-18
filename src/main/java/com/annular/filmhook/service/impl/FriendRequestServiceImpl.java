package com.annular.filmhook.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Objects;
import java.util.Date;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.User;
import com.annular.filmhook.service.UserService;

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

            // 1️⃣ Validate IDs
            if (senderId == null || receiverId == null) {
                return ResponseEntity.badRequest().body("Sender ID or Receiver ID cannot be null");
            }

            if (senderId.equals(receiverId)) {
                return ResponseEntity.badRequest().body("Sender ID and Receiver ID cannot be the same");
            }

            // 2️⃣ Check if relationship already exists
            Optional<FollowersRequest> existingRequestOpt =
                    friendRequestRepository.findByFollowersRequestSenderIdAndFollowersRequestReceiverId(senderId, receiverId);

            if (existingRequestOpt.isPresent()) {
                FollowersRequest existingRequest = existingRequestOpt.get();

                // If already following — prevent re-follow
                if (existingRequest.getFollowersRequestStatus().equalsIgnoreCase(FOLLOWED)) {
                    return ResponseEntity.ok("You are already following this user.");
                }

                // If previously unfollowed — allow follow again
                if (existingRequest.getFollowersRequestStatus().equalsIgnoreCase(UNFOLLOWED)) {
                    existingRequest.setFollowersRequestStatus(FOLLOWED);
                    existingRequest.setFollowersRequestIsActive(true);
                    existingRequest.setFollowersRequestUpdatedOn(new Date());
                    existingRequest.setFollowersRequestUpdatedBy(senderId);
                    friendRequestRepository.save(existingRequest);

                    return ResponseEntity.ok("You started following this user.");
                }
            }

            // 3️⃣ If no record found — create a new follow request
            FollowersRequest newRequest = new FollowersRequest();
            newRequest.setFollowersRequestSenderId(senderId);
            newRequest.setFollowersRequestReceiverId(receiverId);
            newRequest.setFollowersRequestStatus(FOLLOWED);
            newRequest.setFollowersRequestCreatedBy(senderId);
            newRequest.setFollowersRequestCreatedOn(new Date());
            newRequest.setFollowersRequestIsActive(true);
            friendRequestRepository.save(newRequest);

            return ResponseEntity.ok("You started following this user.");
        } catch (Exception e) {
            logger.error("Error at saveFollowersRequest() -> {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("An error occurred while saving the follower request: " + e.getMessage());
        }
    }


    @Override
    public ResponseEntity<?> updateFriendRequest(FollowersRequestWebModel followersRequestWebModel) {
        try {
            Integer senderId = followersRequestWebModel.getFollowersRequestSenderId();
            Integer receiverId = followersRequestWebModel.getFollowersRequestReceiverId();

            // 1️⃣ Validate IDs
            if (senderId == null || receiverId == null)
                return ResponseEntity.badRequest().body("Sender ID or Receiver ID cannot be null");

            Optional<FollowersRequest> existingRequestOpt =
                    friendRequestRepository.findByFollowersRequestSenderIdAndFollowersRequestReceiverId(senderId, receiverId);

            if (existingRequestOpt.isEmpty()) {
                return ResponseEntity.ok("You are not following this user.");
            }

            FollowersRequest existingRequest = existingRequestOpt.get();

            // 2️⃣ Check current follow status
            if (existingRequest.getFollowersRequestStatus().equalsIgnoreCase(UNFOLLOWED)) {
                return ResponseEntity.ok("You have already unfollowed this user.");
            }
            if (existingRequest.getFollowersRequestStatus().equalsIgnoreCase(FOLLOWED)) {
                existingRequest.setFollowersRequestStatus(UNFOLLOWED);
                existingRequest.setFollowersRequestUpdatedOn(new Date());
                existingRequest.setFollowersRequestUpdatedBy(senderId);
                friendRequestRepository.saveAndFlush(existingRequest);

                return ResponseEntity.ok().body("profile unfollowed successfully...");
            }

            // 3️⃣ If the status is neither FOLLOWED nor UNFOLLOWED (unexpected)
            return ResponseEntity.badRequest().body("Invalid follow status for this user.");

        } catch (Exception e) {
            logger.error("Error at updateFriendRequest() -> {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("An error occurred while updating the follow request: " + e.getMessage());
        }
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
                        User user = userService.getUser(type.equalsIgnoreCase(FOLLOWERS) ? request.getFollowersRequestSenderId() : request.getFollowersRequestReceiverId()).orElse(null);
                        if (user != null) {
                            FollowersRequestWebModel followersRequestWebModel = FollowersRequestWebModel.builder()
                                    .followersRequestId(request.getFollowersRequestId())
                                    .followersRequestSenderId(request.getFollowersRequestSenderId())
                                    .followersRequestReceiverId(request.getFollowersRequestReceiverId())
                                    .followersRequestStatus(request.getFollowersRequestStatus())
                                    .followersRequestIsActive(request.getFollowersRequestIsActive())
                                    .userName(user.getName())
                                    .userGender(user.getGender())
                                    .userType(user.getUserType())
                                    .review(user.getAdminReview())
                                    .userProfilePicUrl(userService.getProfilePicUrl(user.getUserId()))
                                    .followersRequestCreatedBy(request.getFollowersRequestCreatedBy())
                                    .followersRequestCreatedOn(request.getFollowersRequestCreatedOn())
                                    .followersRequestUpdatedBy(request.getFollowersRequestUpdatedBy())
                                    .followersRequestUpdatedOn(request.getFollowersRequestUpdatedOn())
                                    .build();
                            outputList.add(followersRequestWebModel);
                        }
                    });
        } catch (Exception e) {
            logger.error("Error at transformUserData -> {}", e.getMessage());
            e.printStackTrace();
        }
        return outputList;
    }

}
