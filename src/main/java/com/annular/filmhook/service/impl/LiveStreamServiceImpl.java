package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.LiveChannel;
import com.annular.filmhook.model.LiveStreamComment;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.LiveDetailsRepository;
import com.annular.filmhook.repository.LiveStreamCommentRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.LiveStreamService;
import com.annular.filmhook.webmodel.LiveDetailsWebModel;
import com.annular.filmhook.webmodel.LiveStreamCommentWebModel;

@Service
public class LiveStreamServiceImpl implements LiveStreamService {

    @Autowired
    LiveDetailsRepository liveDetailsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LiveStreamCommentRepository liveStreamCommentRepository;

    @Override
    public ResponseEntity<?> saveLiveDetails(LiveDetailsWebModel liveDetailsWebModel) {
        try {

            LiveChannel liveDetails = new LiveChannel();
            liveDetails.setUserId(liveDetailsWebModel.getUserId());
            liveDetails.setChannelName(liveDetailsWebModel.getChannelName());
            liveDetails.setLiveIsActive(true);
//			liveDetails.setCreatedBy(liveDetailsWebModel.getCreatedBy());
            liveDetails.setToken(liveDetailsWebModel.getToken());
            liveDetails.setLiveId(liveDetailsWebModel.getLiveId());
            liveDetails.setStartTime(liveDetailsWebModel.getStartTime());
            liveDetails.setEndTime(liveDetailsWebModel.getEndTime());
            liveDetails.setLiveDate(liveDetailsWebModel.getLiveDate());

            liveDetailsRepository.save(liveDetails);

            // Return a success response
            return ResponseEntity.ok(new Response(1, "Live Details saved Successfully", liveDetails));
        } catch (Exception e) {
            // Handle any exceptions or errors
            return ResponseEntity.internalServerError().body(new Response(-1, "failed to save live details", e.getMessage()));
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
}
