package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.InAppNotification;
import com.annular.filmhook.model.LiveChannel;
import com.annular.filmhook.model.LiveStreamComment;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.InAppNotificationRepository;
import com.annular.filmhook.repository.LiveDetailsRepository;
import com.annular.filmhook.repository.LiveStreamCommentRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.LiveStreamService;
import com.annular.filmhook.webmodel.LiveDetailsWebModel;
import com.annular.filmhook.webmodel.LiveStreamCommentWebModel;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class LiveStreamServiceImpl implements LiveStreamService {

    @Autowired
    LiveDetailsRepository liveDetailsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LiveStreamCommentRepository liveStreamCommentRepository;
    
    @Autowired
    InAppNotificationRepository inAppNotificationRepository;
    
    public static final Logger logger = LoggerFactory.getLogger(LiveStreamServiceImpl.class);

    @Override
    public ResponseEntity<?> saveLiveDetails(LiveDetailsWebModel liveDetailsWebModel) {
        try {
            LiveChannel liveDetails = new LiveChannel();
            liveDetails.setUserId(liveDetailsWebModel.getUserId());
            liveDetails.setChannelName(liveDetailsWebModel.getChannelName());
            liveDetails.setLiveIsActive(true);
            liveDetails.setToken(liveDetailsWebModel.getToken());
            liveDetails.setLiveId(liveDetailsWebModel.getLiveId());
            liveDetails.setStartTime(liveDetailsWebModel.getStartTime());
            liveDetails.setEndTime(liveDetailsWebModel.getEndTime());
            liveDetails.setLiveDate(liveDetailsWebModel.getLiveDate());

            liveDetailsRepository.save(liveDetails);

            // 2️⃣ Fetch ALL Users
            List<User> allUsers = userRepository.findAll();

            // 3️⃣ Notification Details
            String notificationTitle = "New Live Stream Started!";
            String notificationMessage = " is going live. Don’t miss it!";
            User sender = userRepository.findById(liveDetails.getUserId()).orElse(null);
            String senderName = (sender != null) ? sender.getName() : "Someone";

            // 4️⃣ Send Notification to All Users
            for (User user : allUsers) {
                try {
                    // Skip the live starter (optional, remove this if they should also get it)
                    if (user.getUserId().equals(liveDetails.getUserId())) {
                        continue;
                    }

                    // Save In-App Notification
                    InAppNotification inAppNotification = InAppNotification.builder()
                            .senderId(liveDetails.getUserId())
                            .receiverId(user.getUserId())
                            .title(notificationTitle)
                            .userType("Live")
                            .message(notificationMessage)
                            .createdOn(new Date())
                            .isRead(false)
                            .isDeleted(false)
                            .adminReview(user.getAdminReview())
                            .Profession(user.getUserType())
                            .id(liveDetails.getLiveChannelId())
                            .createdBy(liveDetails.getUserId())
                            .build();

                    inAppNotificationRepository.save(inAppNotification);

                    // Send Push Notification (only if token exists)
                    if (user.getFirebaseDeviceToken() != null && !user.getFirebaseDeviceToken().trim().isEmpty()) {
                        Notification firebaseNotification = Notification.builder()
                                .setTitle(notificationTitle)
                                .setBody(senderName + notificationMessage)
                                .build();

                        AndroidNotification androidNotification = AndroidNotification.builder()
                                .setIcon("ic_notification")
                                .setColor("#4d79ff")
                                .build();

                        AndroidConfig androidConfig = AndroidConfig.builder()
                                .setNotification(androidNotification)
                                .build();

                        Message firebaseMessage = Message.builder()
                                .setNotification(firebaseNotification)
                                .putData("type", "Live")
                                .putData("LiveChannelId", String.valueOf(liveDetails.getLiveChannelId()))
                                .putData("senderId", String.valueOf(liveDetails.getUserId()))
                                .putData("receiverId", String.valueOf(user.getUserId()))
                                .setAndroidConfig(androidConfig)
                                .setToken(user.getFirebaseDeviceToken())
                                .build();

                        String response = FirebaseMessaging.getInstance().send(firebaseMessage);
                        logger.info("Push notification sent to userId {}: {}", user.getUserId(), response);
                    } else {
                        logger.warn("User {} has no Firebase token. Skipping push notification.", user.getUserId());
                    }

                } catch (FirebaseMessagingException e) {
                    logger.error("Push notification failed for userId {}: {}", user.getUserId(), e.getMessage());
                } catch (Exception ex) {
                    logger.error("Error sending notification to userId {}: {}", user.getUserId(), ex.getMessage());
                }
            }

            // 5️⃣ Return Success
            return ResponseEntity.ok(new Response(1, "Live Details saved successfully", liveDetails));

        } catch (Exception e) {
            logger.error("Failed to save live details: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new Response(-1, "Failed to save live details", e.getMessage()));
        }
    }
    @Override
    public ResponseEntity<?> getLiveDetails() {
        try {
            List<LiveChannel> liveDetailsDB = liveDetailsRepository.findAll();

            List<Map<String, Object>> responseList = new ArrayList<>();
            for (LiveChannel liveChannel : liveDetailsDB) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("channelId", liveChannel.getLiveChannelId());
                responseMap.put("channelName", liveChannel.getChannelName());
                responseMap.put("userId", liveChannel.getUserId()); // Assuming a relationship between LiveChannel and
                responseMap.put("token", liveChannel.getToken());
                responseMap.put("endTime", liveChannel.getEndTime());
                responseMap.put("startTime", liveChannel.getStartTime());
                responseMap.put("liveDate", liveChannel.getLiveDate());
                responseMap.put("liveIsActive", liveChannel.getLiveIsActive());
                responseMap.put("liveId", liveChannel.getLiveId());// User
                User user = userRepository.findById(liveChannel.getUserId()).orElse(null);
                if (user != null) {
                    responseMap.put("username", user.getName());
                } else {
                    responseMap.put("username", "Unknown"); // If user not found
                }

                responseList.add(responseMap);
            }
            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error fetching live Details..", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> saveLiveStreamComment(LiveStreamCommentWebModel liveStreamCommentWebModel) {
        try {

            LiveStreamComment liveDetails = new LiveStreamComment();
            liveDetails.setUserId(liveStreamCommentWebModel.getUserId());
            liveDetails.setLiveStreamMessage(liveStreamCommentWebModel.getLiveStreamMessage());
            liveDetails.setLiveStreamCommenIsActive(true);
            liveDetails.setLiveStreamCommencreatedBy(liveStreamCommentWebModel.getLiveStreamCommencreatedBy());
            liveDetails.setUserId(liveStreamCommentWebModel.getUserId());
            liveDetails.setLiveChannelId(liveStreamCommentWebModel.getLiveChannelId());
            liveDetails.setLiveId(liveStreamCommentWebModel.getLiveId());

            liveStreamCommentRepository.save(liveDetails);

            // Return a success response
            return ResponseEntity.ok(new Response(1, "Live comment saved Successfully", liveDetails));
        } catch (Exception e) {
            // Handle any exceptions or errors
            return ResponseEntity.internalServerError().body(new Response(-1, "failed to save live details", e.getMessage()));
        }
    }

    public ResponseEntity<?> getLiveCommentDetails(Integer liveChannelId) {
        try {
            List<LiveStreamComment> liveDetailsDB = liveStreamCommentRepository.findByLiveChannelId(liveChannelId);

            List<Map<String, Object>> responseList = new ArrayList<>();
            for (LiveStreamComment comment : liveDetailsDB) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("liveChannelId", comment.getLiveChannelId());
                responseMap.put("liveStreamCommentId", comment.getLiveStreamCommentId());
                responseMap.put("liveStreamCommencreatedBy", comment.getLiveStreamCommencreatedBy());
                responseMap.put("liveStreamCommenCreatedOn", comment.getLiveStreamCommenCreatedOn());
                responseMap.put("liveStreamCommenIsActive", comment.getLiveStreamCommenIsActive());
                responseMap.put("liveStreamCommenUpdatedBy", comment.getLiveStreamCommenUpdatedBy());
                responseMap.put("liveStreamCommenUpdatedOn", comment.getLiveStreamCommenUpdatedOn());
                responseMap.put("liveStreamMessage", comment.getLiveStreamMessage());


                User user = userRepository.findById(comment.getUserId()).orElse(null);
                if (user != null) {
                    responseMap.put("username", user.getName());
                } else {
                    responseMap.put("username", "Unknown");
                }

                responseList.add(responseMap);
            }
            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error fetching live Details..", e.getMessage()));
        }
    }

    public ResponseEntity<?> getAllLiveChannelId() {
        try {
            // Fetch all live channels from the repository
            List<LiveChannel> liveChannels = liveDetailsRepository.findAll();

            List<Map<String, Object>> responseList = new ArrayList<>();
            for (LiveChannel channel : liveChannels) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("channelId", channel.getLiveChannelId());
                responseMap.put("channelName", channel.getChannelName());
                responseMap.put("userId", channel.getUserId()); // Assuming a relationship between LiveChannel and
                responseMap.put("token", channel.getToken());
                responseMap.put("endTime", channel.getEndTime());
                responseMap.put("startTime", channel.getStartTime());
                responseMap.put("liveDate", channel.getLiveDate());
                responseMap.put("liveIsActive", channel.getLiveIsActive());// User
                responseMap.put("liveId", channel.getLiveId());

                User user = userRepository.findById(channel.getUserId()).orElse(null);
                if (user != null) {
                    responseMap.put("username", user.getName());
                } else {
                    responseMap.put("username", "Unknown"); // If user not found
                }

                responseList.add(responseMap);
            }
            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error fetching live Details..", e.getMessage()));
        }

    }
    
    @Override
    public ResponseEntity<?> getLiveDetailsByChannelId(Integer liveChannelId) {
        try {
            // Fetch live channel by primary key (channel ID)
            LiveChannel liveChannel = liveDetailsRepository.findById(liveChannelId)
                    .orElseThrow(() -> new RuntimeException("Live details not found for channel ID: " + liveChannelId));

            // Prepare response map
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("channelId", liveChannel.getLiveChannelId());
            responseMap.put("channelName", liveChannel.getChannelName());
            responseMap.put("userId", liveChannel.getUserId());
            responseMap.put("token", liveChannel.getToken());
            responseMap.put("endTime", liveChannel.getEndTime());
            responseMap.put("startTime", liveChannel.getStartTime());
            responseMap.put("liveDate", liveChannel.getLiveDate());
            responseMap.put("liveIsActive", liveChannel.getLiveIsActive());
            responseMap.put("liveId", liveChannel.getLiveId());

            // Add username if available
            User user = userRepository.findById(liveChannel.getUserId()).orElse(null);
            responseMap.put("username", user != null ? user.getName() : "Unknown");

            return ResponseEntity.ok(new Response(1, "success", responseMap));

        } catch (RuntimeException e) {
            logger.warn("Live channel not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(-1, "fail", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching live details by channel ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(-1, "error", "Internal server error occurred"));
        }
    }

}
