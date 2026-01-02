package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.LiveSubscribe;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.LiveDetailsRepository;
import com.annular.filmhook.repository.LiveSubscribeRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.LiveSubscribeService;
import com.annular.filmhook.webmodel.LiveSubscribeWebModel;

@Service
public class LiveSubscribeServiceImpl implements LiveSubscribeService {

    @Autowired
    LiveDetailsRepository liveChannelRepository;

    @Autowired
    LiveSubscribeRepository liveSubscribeRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public ResponseEntity<?> saveLiveSubscribe(LiveSubscribeWebModel liveSubscribeWebModel) {
        try {

            LiveSubscribe liveSubscribe = new LiveSubscribe();

            liveSubscribe.setUserId(liveSubscribeWebModel.getUserId());
            liveSubscribe.setLiveSubscribeIsActive(true);
            liveSubscribe.setStartTime(liveSubscribeWebModel.getStartTime());
            liveSubscribe.setEndTime(liveSubscribeWebModel.getEndTime());

//			LiveChannel channel = liveChannelRepository.findById(liveSubscribeWebModel.getLiveChannelId())
//					.orElseThrow(() -> new RuntimeException("Channel not found"));
            liveSubscribe.setLiveChannelId(liveSubscribeWebModel.getLiveChannelId());

            liveSubscribeRepository.save(liveSubscribe);
            return ResponseEntity.ok(new Response(1, "Live Subscribe saved Successfully", liveSubscribe));

        } catch (Exception e) {
            // Handle any exceptions and return an appropriate ResponseEntity
			e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "failed to save live Subscribe", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> getLiveSubscribes(LiveSubscribeWebModel liveSubscribeWebModel) {
        List<Map<String, Object>> responseList = new ArrayList<>();
        List<LiveSubscribe> subscribeData = liveSubscribeRepository.findByChannelId(liveSubscribeWebModel.getLiveChannelId());
        if (!subscribeData.isEmpty()) {
            for (LiveSubscribe subscription : subscribeData) {
                Map<String, Object> subscriptionDetails = new HashMap<>();
                subscriptionDetails.put("userId", subscription.getUserId());
                Optional<User> userDb = userRepository.findById(subscription.getUserId());
                subscriptionDetails.put("userNmae", userDb.get().getName());
                subscriptionDetails.put("startTime", subscription.getStartTime());
                subscriptionDetails.put("endTime", subscription.getEndTime());
                responseList.add(subscriptionDetails);
            }
        }
        return ResponseEntity.ok(responseList);
    }

}
