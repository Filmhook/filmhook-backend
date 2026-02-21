package com.annular.filmhook.service.impl;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.CallLog;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserSession;
import com.annular.filmhook.repository.CallLogRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.repository.UserSessionRepository;
import com.annular.filmhook.service.*;
import com.annular.filmhook.util.WebSocketService;
import com.annular.filmhook.webmodel.AgoraWebModel;
import com.annular.filmhook.webmodel.EndCallRequest;
import com.annular.filmhook.webmodel.StartCallRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message; import com.google.firebase.messaging.Notification;

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
    private UserService userService;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private WebSocketService ws;

    /* ---------------------------------------------------------
     * START CALL
     * --------------------------------------------------------- */
    @Override
    public Response startCall(StartCallRequest req) {

        // 1. Busy Check
        if (callRepo.isUserInActiveCall(req.getReceiverId())) {

            // Notify caller through WebSocket
            ws.notifyUser(req.getCallerId(), "CALL_BUSY",
                    Map.of("receiverId", req.getReceiverId()));

            return new Response(-1, "User is busy", null);
        }

        // 2. Generate Channel
        String channelName = "call_" + req.getCallerId() + "_" +
                req.getReceiverId() + "_" + System.currentTimeMillis();

        // 3. Generate RTC Token
        AgoraWebModel model = new AgoraWebModel();
        model.setChannelName(channelName);
        model.setUserId(req.getCallerId());
        model.setExpirationTimeInSeconds(6000);

        String rtcToken = agoraTokenService.getRTCToken(model);

        // 4. Save CallLog
        CallLog log = new CallLog();
        log.setCallerId(req.getCallerId());
        log.setReceiverId(req.getReceiverId());
        log.setChannelName(channelName);
        log.setCallType(req.getCallType());
        log.setStatus("initiated");
        log.setRtcToken(rtcToken);
        log.setStartTime(LocalDateTime.now());
        callRepo.save(log);

        // Caller & Receiver details
        User caller = userRepository.findById(req.getCallerId()).orElse(null);
        User receiver = userRepository.findById(req.getReceiverId()).orElse(null);

        String callerPic = userService.getProfilePicUrl(req.getCallerId());
        String receiverPic = userService.getProfilePicUrl(req.getReceiverId());

        String callerName = caller != null ? caller.getName() : "";
        String receiverName = receiver != null ? receiver.getName() : "";

        // 5. Push Notification to all active receiver devices
        List<UserSession> activeSessions =
                userSessionRepository.findByUserIdAndIsActive(req.getReceiverId(), true);

        for (UserSession s : activeSessions) {

            String token = s.getFirebaseToken();

            if (token != null && !token.trim().isEmpty()) {

                fcm.sendIncomingCallNotification(
                        req.getCallerId(),
                        req.getReceiverId(),
                        req.getCallType(),
                        channelName,
                        token,
                        callerName,
                        callerPic
                );
            }
        }

        // 6. Response to Caller
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
     * END / REJECT / MISSED
     * --------------------------------------------------------- */
    @Override
    public Response endCall(EndCallRequest req) {

        CallLog log = callRepo.findByChannelName(req.getChannelName());
        if (log == null) {
            System.out.println("Invalid channel: " + req.getChannelName());
            return new Response(-1, "Invalid channel", null);
        }

        log.setStatus(req.getStatus());
        log.setEndTime(LocalDateTime.now());
        callRepo.save(log);

        // Identify the opposite user
        Integer otherUser =
                req.getUserId().equals(log.getCallerId()) ?
                        log.getReceiverId() : log.getCallerId();

        System.out.println("EndCall Triggered by User " + req.getUserId());
        System.out.println("Other User: " + otherUser);
        System.out.println("Call Status: " + req.getStatus());
        System.out.println("Channel Name: " + req.getChannelName());

        /* ---------------------------------------------------------
         * WebSocket Call Events (Real-time)
         * --------------------------------------------------------- */
        switch (req.getStatus()) {

            case "rejected":
                System.out.println("🚫 CALL_REJECTED event sent to user " + otherUser);

                ws.notifyUser(otherUser, "CALL_REJECTED",
                        Map.of("channelName", req.getChannelName()));
                break;
                
            case "cancelled":
                System.out.println("🚫 CALL_CANCELLED event sent to user " + otherUser);

                ws.notifyUser(otherUser, "CALL_CANCELLED",
                        Map.of("channelName", req.getChannelName()));
                break;

            case "accepted":
                System.out.println("📞 CALL_ACCEPTED event sent to user " + otherUser);

                ws.notifyUser(otherUser, "CALL_ACCEPTED",
                        Map.of("channelName", req.getChannelName()));
                break;

            case "ended":
                System.out.println("🛑 CALL_ENDED event sent to user " + otherUser);

                ws.notifyUser(otherUser, "CALL_ENDED",
                        Map.of("channelName", req.getChannelName()));
                break;

            case "missed":
                System.out.println("⏰ CALL_MISSED event sent to user " + otherUser);

                ws.notifyUser(otherUser, "CALL_MISSED",
                        Map.of("channelName", req.getChannelName()));
                break;

            default:
                System.out.println("⚠ Unknown status: " + req.getStatus());
        }

        /* ---------------------------------------------------------
         * Push Notification to All Active Devices (UserSession)
         * --------------------------------------------------------- */

       
        

        return new Response(1, "Call updated: " + req.getStatus(), null);
    }

    /* ---------------------------------------------------------
     * Get RTC Token by Channel
     * --------------------------------------------------------- */
    @Override
    public Response getRtcTokenByChannel(String channelName) {

        CallLog log = callRepo.findByChannelName(channelName);

        if (log == null)
            return new Response(-1, "Invalid channel name", null);

        return new Response(1, "Success", log.getRtcToken());
    }

    /* ---------------------------------------------------------
     * Test Push Message
     * --------------------------------------------------------- */
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
