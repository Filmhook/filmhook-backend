package com.annular.filmhook.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.CallLog;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserSession;
import com.annular.filmhook.repository.CallLogRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.repository.UserSessionRepository;
import com.annular.filmhook.service.AgoraTokenService;
import com.annular.filmhook.service.CallService;
import com.annular.filmhook.service.FcmService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.AgoraWebModel;
import com.annular.filmhook.webmodel.EndCallRequest;
import com.annular.filmhook.webmodel.StartCallRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
@Service
public class CallServiceImpl implements CallService {
	
	  @Autowired
	    private CallLogRepository callRepo;

	    @Autowired
	    private AgoraTokenService agoraTokenService;

	    @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private FcmService fcm;
	    
	    @Autowired
	    private UserService userServices;
	    
	    @Autowired
	    private UserSessionRepository userSessionRepository;


	    /* ---------------------------------------------------------
	     * START CALL
	     * --------------------------------------------------------- */
	    @Override
	    public Response startCall(StartCallRequest req) {

	        // 1. User Busy Check
	        if (callRepo.isUserInActiveCall(req.getReceiverId())) {
	            return new Response(-1, "User is busy", null);
	        }

	        // 2. Channel
	        String channelName = "call_" + req.getCallerId() + "_" + req.getReceiverId() + "_" + System.currentTimeMillis();

	        // 3. Generate RTC Token
	        AgoraWebModel model = new AgoraWebModel();
	        model.setChannelName(channelName);
	        model.setUserId(req.getCallerId());
	        model.setExpirationTimeInSeconds(6000);

	        String rtcToken = agoraTokenService.getRTCToken(model);
	        
	     
	        // 4. Save DB
	        CallLog log = new CallLog();
	        log.setCallerId(req.getCallerId());
	        log.setReceiverId(req.getReceiverId());
	        log.setChannelName(channelName);
	        log.setCallType(req.getCallType());
	        log.setStatus("initiated");
	        log.setRtcToken(rtcToken);
	        log.setStartTime(LocalDateTime.now());
	        callRepo.save(log);
	        
	        User caller = userRepository.findById(req.getCallerId()).orElse(null);
	        User receiver = userRepository.findById(req.getReceiverId()).orElse(null);

	        String callerPic = userServices.getProfilePicUrl(req.getCallerId());
	        String receiverPic = userServices.getProfilePicUrl(req.getReceiverId());

	        String callerName = caller != null ? caller.getName() : "";
	        String receiverName = receiver != null ? receiver.getName() : "";

	        
	        Optional<User> receiverOpt = userRepository.findById(req.getReceiverId());
	    	
	        // 6. Push Notification to All Active Devices (Receiver)
	        List<UserSession> activeSessions =
	                userSessionRepository.findByUserIdAndIsActive(req.getReceiverId(), true);

	        for (UserSession session : activeSessions) {

	            String deviceToken = session.getFirebaseToken();

	            if (deviceToken != null && !deviceToken.trim().isEmpty()) {

	                fcm.sendIncomingCallNotification(
	                        req.getCallerId(),
	                        req.getReceiverId(),
	                        req.getCallType(),
	                        channelName,
	                        deviceToken,         // Send to this device
	                        callerName,          // NEW
	                        callerPic            // NEW
	                );
	            }
	        }

	        // 7. Response Payload
	        Map<String, Object> result = new HashMap<>();
	        result.put("channelName", channelName);
	        result.put("rtcToken", rtcToken);
	        result.put("callType", req.getCallType());
	        result.put("callerName", callerName);
	        result.put("receiverName", receiverName);
	        result.put("callerPic", callerPic);
	        result.put("receiverPic", receiverPic);

	        return new Response(1, "Call Started", result);
	    }



	    /* ---------------------------------------------------------
	     * END / REJECT / MISSED / BUSY CALL
	     * --------------------------------------------------------- */
	    @Override
	    public Response endCall(EndCallRequest req) {

	        CallLog log = callRepo.findByChannelName(req.getChannelName());
	        if (log == null) return new Response(-1, "Invalid channel", null);

	        log.setStatus(req.getStatus());
	        log.setEndTime(LocalDateTime.now());
	        callRepo.save(log);

	        // Find the other user
	        Integer otherUser =
	                req.getUserId().equals(log.getCallerId()) ? log.getReceiverId() : log.getCallerId();

	        Optional<User> receiverOpt = userRepository.findById(otherUser);
	        String receiverToken = receiverOpt.get().getFirebaseDeviceToken();
	        if (receiverToken != null) {
	            fcm.sendCallStatusNotification(log, req.getUserId(), req.getStatus(), receiverToken);
	        }

	        return new Response(1, "Call updated: " + req.getStatus(), null);
	    }
	    
	    @Override
	    public Response getRtcTokenByChannel(String channelName) {
	        CallLog callLog = callRepo.findByChannelName(channelName);

	        if (callLog == null) {
	            return new Response(-1, "Invalid channel name", null);
	        }

	        return new Response(1, "Success", callLog.getRtcToken());
	    }
	    
	    public void sendTestNotification(String token, String title, String body) {
	        try {
	            Notification notification = Notification.builder()
	                    .setTitle(title)
	                    .setBody(body)
	                    .build();

	            Message message = Message.builder()
	                    .setToken(token)
	                    .setNotification(notification)
	                    .build();

	            String response = FirebaseMessaging.getInstance().send(message);
	            System.out.println("FCM Response: " + response);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}
