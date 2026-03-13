package com.annular.filmhook.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.CallLog;
import com.annular.filmhook.model.GroupCall;
import com.annular.filmhook.model.GroupCallMember;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserSession;
import com.annular.filmhook.repository.CallLogRepository;
import com.annular.filmhook.repository.GroupCallMemberRepository;
import com.annular.filmhook.repository.GroupCallRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.repository.UserSessionRepository;
import com.annular.filmhook.service.AgoraTokenService;
import com.annular.filmhook.service.CallService;
import com.annular.filmhook.service.FcmService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.WebSocketService;
import com.annular.filmhook.webmodel.AgoraWebModel;
import com.annular.filmhook.webmodel.CallHistoryResponse;
import com.annular.filmhook.webmodel.EndCallRequest;
import com.annular.filmhook.webmodel.GroupCallEndRequest;
import com.annular.filmhook.webmodel.GroupCallInviteRequest;
import com.annular.filmhook.webmodel.GroupCallJoinRequest;
import com.annular.filmhook.webmodel.GroupCallStartRequest;
import com.annular.filmhook.webmodel.GroupNameResult;
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
    private UserService userService;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private WebSocketService ws;
    
    @Autowired
    private GroupCallRepository groupRepo;

    @Autowired
    private GroupCallMemberRepository groupMemberRepo;
    
    @Autowired
    UserService userServices;

    /* ---------------------------------------------------------
     * START CALL
     * --------------------------------------------------------- */
@Override
   public Response startCall(StartCallRequest req) {

    Integer callerId = req.getCallerId();
    Integer receiverId = req.getReceiverId();
    User caller = userRepository.findById(callerId).orElse(null);
    User receiver = userRepository.findById(receiverId).orElse(null);

    String callerName = caller != null ? caller.getName() : "";
    String receiverName = receiver != null ? receiver.getName() : "";
    String callerPic = userService.getProfilePicUrl(callerId);
    String receiverPic = userService.getProfilePicUrl(receiverId);
    boolean isReceiverBusy = callRepo.existsByReceiverIdAndStatusIn(
            receiverId,
            List.of("incoming")
    );

    /* ---------------------------------------------------------
     * 1. Generate Channel
     * --------------------------------------------------------- */
    String channelName = "call_" + callerId + "_" + receiverId + "_" + System.currentTimeMillis();

    AgoraWebModel model = new AgoraWebModel();
    model.setChannelName(channelName);
    model.setUserId(callerId);
    model.setExpirationTimeInSeconds(6000);

    String rtcToken = agoraTokenService.getRTCToken(model);

    /* ---------------------------------------------------------
     * 2. Save CallLog
     * --------------------------------------------------------- */
    CallLog log = new CallLog();
    log.setCallerId(callerId);
    log.setReceiverId(receiverId);
    log.setChannelName(channelName);
    log.setCallType(req.getCallType());
    log.setRtcToken(rtcToken);
    log.setStartTime(LocalDateTime.now());

if (isReceiverBusy) {

    log.setStatus("missed");
    log.setEndTime(LocalDateTime.now());

    callRepo.save(log);

    /* ---------------------------------------------------------
     * SEND WEBSOCKET TO CALLER
     * --------------------------------------------------------- */

    ws.notifyUser(callerId, "USER_BUSY",
            Map.of(
                    "receiverId", receiverId,
                    "message", "User is currently on another call"
            )
    );

    /* ---------------------------------------------------------
     * LOAD USER DETAILS
     * --------------------------------------------------------- */

    /* ---------------------------------------------------------
     * SEND MISSED CALL PUSH NOTIFICATION
     * --------------------------------------------------------- */

    List<UserSession> sessions =
            userSessionRepository.findByUserIdAndIsActive(receiverId, true);

    for (UserSession s : sessions) {

        if (s.getFirebaseToken() != null && !s.getFirebaseToken().trim().isEmpty()) {

            fcm.sendMissedCallNotification(
                    callerId,
                    receiverId,
                    callerName,
                    callerPic,
                    s.getFirebaseToken()
            );
        }
    }
  
    
    Map<String, Object> result = new HashMap<>();
    result.put("channelName", channelName);
    result.put("rtcToken", rtcToken);
    result.put("callType", req.getCallType());
    result.put("callerName", callerName);
    result.put("receiverName", receiverName);
    result.put("callerPic", callerPic);
    result.put("receiverPic", receiverPic);
    result.put("status", receiverName + "is busy");
    

    return new Response(1, "User is busy", result);
}

    /* ---------------------------------------------------------
     * 3. Normal Call Flow
     * --------------------------------------------------------- */

    log.setStatus("incoming");
    callRepo.save(log);

    /* ---------------------------------------------------------
     * 4. Send Push Notification
     * --------------------------------------------------------- */

    List<UserSession> activeSessions =
            userSessionRepository.findByUserIdAndIsActive(receiverId, true);

    for (UserSession s : activeSessions) {

        if (s.getFirebaseToken() != null && !s.getFirebaseToken().trim().isEmpty()) {

            fcm.sendIncomingCallNotification(
                    callerId,
                    receiverId,
                    req.getCallType(),
                    channelName,
                    s.getFirebaseToken(),
                    callerName,
                    callerPic
            );
        }
    }

    /* ---------------------------------------------------------
     * 5. WebSocket Event
     * --------------------------------------------------------- */

    ws.notifyUser(receiverId, "NEW_CALL_INCOMING",
            Map.of(
                    "fromUserId", callerId,
                    "channelName", channelName,
                    "callType", req.getCallType(),
                    "callerName", callerName,
                    "callerPic", callerPic
            )
    );

    /* ---------------------------------------------------------
     * 6. Response
     * --------------------------------------------------------- */

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
                        Map.of("channelName", req.getChannelName(), "userName", req.getUserName()));
                break;
                
            case "cancelled":
                System.out.println("🚫 CALL_CANCELLED event sent to user " + otherUser);

                ws.notifyUser(otherUser, "CALL_CANCELLED",
                        Map.of("channelName", req.getChannelName(),"userName", req.getUserName()));
                break;

            case "ended":
                System.out.println("🛑 CALL_ENDED event sent to user " + otherUser);

                ws.notifyUser(otherUser, "CALL_ENDED",
                        Map.of("channelName", req.getChannelName(),"userName", req.getUserName()));
                break;

            case "missed":
                System.out.println("⏰ CALL_MISSED event sent to user " + otherUser);

                ws.notifyUser(otherUser, "CALL_MISSED",
                        Map.of("channelName", req.getChannelName(),"userName", req.getUserName()));
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
    
    @Override
    public Response startGroupCall(GroupCallStartRequest req) {
        Integer hostId = req.getHostUserId();
        List<Integer> members = new ArrayList<>(req.getMemberIds());
        members.add(hostId);

        /* ---------------------------------------------------------
         * 1. Determine channelName
         * --------------------------------------------------------- */
        String channelName;

        if (req.getChannelName() != null && !req.getChannelName().isBlank()) {
            // 🔥 Reuse existing channel – converting 1-on-1 → group call
            channelName = req.getChannelName();
            System.out.println("Reusing existing channel for group call: " + channelName);
        } else {
            // 🔥 Fresh group call
            channelName = "group_" + hostId + "_" + System.currentTimeMillis();
            System.out.println("New group call created with channel: " + channelName);
        }

        /* ---------------------------------------------------------
         * 2. Create group_call record
         * --------------------------------------------------------- */
        GroupCall gc = new GroupCall();
        gc.setHostUserId(hostId);
        gc.setChannelName(channelName);
        gc.setCallType(req.getCallType());
        gc.setStatus("active");
        gc.setCreatedOn(LocalDateTime.now());
        groupRepo.save(gc);

        List<Map<String, Object>> tokenList = new ArrayList<>();
        
        List<String> memberNames = new ArrayList<>();

        for (Integer uid : members) {

            User u = userRepository.findById(uid).orElse(null);

            if (u != null && u.getName() != null) {
                memberNames.add(u.getName());
            }
        }
        String groupNames = buildGroupUserNames(memberNames);
        System.out.println("Check group names for group call " + groupNames);

        /* ---------------------------------------------------------
         * 3. Create Agora tokens for all members
         * --------------------------------------------------------- */

        for (Integer uid : members) {

            AgoraWebModel model = new AgoraWebModel();
            model.setChannelName(channelName);
            model.setUserId(uid);
            model.setExpirationTimeInSeconds(7200);

            String token = agoraTokenService.getRTCToken(model);

            // ALWAYS create new entity
            GroupCallMember m = new GroupCallMember();
            m.setGroupCallId(gc.getId());
            m.setUserId(uid);
            m.setRtcToken(token);

            if (uid.equals(hostId)) {
            	 System.out.println("Host is saved ");
                // Host automatically joins
                m.setJoined(true);
                groupMemberRepo.save(m);

            } else {

                GroupCallMember lastCall =
                        groupMemberRepo.findTopByUserIdAndLeaveTimeIsNullOrderByIdDesc(uid);
                System.out.println("lastcall response     " + lastCall);

                boolean missed = false;

                if (lastCall != null &&
                    Boolean.FALSE.equals(lastCall.getJoined())) {
                    missed = true;
                }
                m.setJoined(false);            
                groupMemberRepo.save(m);
                tokenList.add(Map.of("userId", uid, "rtcToken", token));

                /* ---------------------------------------------------------
                 * GET USER DEVICES
                 * --------------------------------------------------------- */

                List<UserSession> sessions =
                        userSessionRepository.findByUserIdAndIsActive(uid, true);

                if (missed) {
                	 GroupCallMember missedGroupCallMember = groupMemberRepo
                             .findByGroupCallIdAndUserId(gc.getId(), uid);
                	 missedGroupCallMember.setLeaveTime(LocalDateTime.now());
                	 groupMemberRepo.save(missedGroupCallMember);
                    /* ---------------------------------------------------------
                     * MISSED GROUP CALL
                     * --------------------------------------------------------- */
                    ws.notifyUser(
                            uid,
                            "MISSED_GROUP_CALL",
                            Map.of(
                                    "groupCallId", gc.getId(),
                                    "hostId", hostId,
                                    "hostName", req.getHostName()
                            )
                    );
                    for (UserSession s : sessions) {
                        if (s.getFirebaseToken() != null) {
                       fcm.sendMissedGroupCallNotification(
                                    hostId,
                                    uid,
                                    req.getCallType(),
                                    channelName,
                                    s.getFirebaseToken(),
                                    req.getHostName(),
                                    req.getHostPic(),
                                    groupNames,
                                    gc.getId()
                            );
                        }
                    }

                } else {

                    /* ---------------------------------------------------------
                     * NORMAL GROUP CALL
                     * --------------------------------------------------------- */

                    ws.notifyUser(
                            uid,
                            "NEW_GROUP_CALL",
                            Map.of(
                                    "channelName", channelName,
                                    "fromUserId", hostId,
                                    "fromUserName", req.getHostName(),
                                    "profilePicture", req.getHostPic(),
                                    "callType", req.getCallType(),
                                    "groupCallId", gc.getId(),
                                    "groupNames", groupNames
                            )
                    );

                    for (UserSession s : sessions) {

                        if (s.getFirebaseToken() != null) {                      	
                            fcm.sendGroupCallNotification(
                                    hostId,
                                    uid,
                                    req.getCallType(),
                                    channelName,
                                    s.getFirebaseToken(),
                                    req.getHostName(),
                                    req.getHostPic(),
                                    groupNames,
                                    gc.getId()
                            );
                        }
                    }
                }
            }
        }
        
        
        /* ---------------------------------------------------------
         * 4. Notify all invited users (WebSocket + FCM)
         * --------------------------------------------------------- */
 
        /* ---------------------------------------------------------
         * MISSED GROUP CALL
         * --------------------------------------------------------- */


        /* ---------------------------------------------------------
         * NORMAL GROUP CALL
         * --------------------------------------------------------- */
  

        /* ---------------------------------------------------------
         * 5. Return response
         * --------------------------------------------------------- */
        return new Response(1, "Group Call Started", Map.of(
                "groupCallId", gc.getId(),
                "channelName", channelName,
                "members", tokenList
        ));
    }
    private String buildGroupUserNames(List<String> names) {

        if (names.size() == 0)
            return "";

        if (names.size() == 1)
            return names.get(0);

        if (names.size() == 2)
            return names.get(0) + " & " + names.get(1);

        return names.get(0) + ", " + names.get(1) + " & " + (names.size() - 2) + " others";
    }

    /* ===========================================================
     * 5️⃣ JOIN GROUP CALL (Late Join)
     * =========================================================== */
    @Override
    public Response joinGroupCall(GroupCallJoinRequest req) {

        GroupCallMember m = groupMemberRepo
                .findByGroupCallIdAndUserId(req.getGroupCallId(), req.getUserId());

        if (m == null) return new Response(-1, "Not part of call", null);

        m.setJoined(true);
        m.setJoinTime(LocalDateTime.now());
        groupMemberRepo.save(m);

        ws.notifyGroup(
                req.getGroupCallId(),
                "GROUP_USER_JOINED",
                Map.of("userId", req.getUserId(), "userName", req.getUserName() )
        );

        return new Response(1, "Joined", null);
    }


    /* ===========================================================
     * 6️⃣ LEAVE GROUP CALL
     * =========================================================== */
    @Override
    public Response leaveGroupCall(GroupCallJoinRequest req) {

        GroupCallMember m = groupMemberRepo
                .findByGroupCallIdAndUserId(req.getGroupCallId(), req.getUserId());

        m.setJoined(false);
        m.setLeaveTime(LocalDateTime.now());
        groupMemberRepo.save(m);

        ws.notifyGroup(
                req.getGroupCallId(),
                "GROUP_USER_LEFT",
                Map.of("userId", req.getUserId())
        );

        return new Response(1, "Left", null);
    }


    /* ===========================================================
     * 7️⃣ END GROUP CALL
     * =========================================================== */
    @Override
    public Response endGroupCall(GroupCallEndRequest req) {

        GroupCall gc = groupRepo.findById(req.getGroupCallId()).orElse(null);
        if (gc == null) return new Response(-1, "Invalid group call", null);

        gc.setStatus("ended");
        groupRepo.save(gc);

        ws.notifyGroup(
                req.getGroupCallId(),
                "GROUP_CALL_ENDED",
                Map.of("hostId", req.getHostId())
        );

        return new Response(1, "Ended", null);
    }


    /* ===========================================================
     * 8️⃣ INVITE Into Existing 1-1 Call → Group Call
     * =========================================================== */
    @Override
    public Response inviteToGroup(GroupCallInviteRequest req) {
    	
    	User host = userRepository.findById(req.getHostUserId()).orElse(null);
    	String hostName = host != null ? host.getName() : "";
    	String hostPic = userService.getProfilePicUrl(req.getHostUserId());

        String channel = req.getChannelName();
        Integer hostId = req.getHostUserId();

        List<Map<String, Object>> list = new ArrayList<>();

        for (Integer uid : req.getMemberIds()) {

            AgoraWebModel model = new AgoraWebModel();
            model.setChannelName(channel);
            model.setUserId(uid);
            model.setExpirationTimeInSeconds(7200);

            String token = agoraTokenService.getRTCToken(model);

            GroupCallMember m = new GroupCallMember();
            m.setGroupCallId(req.getGroupCallId());
            m.setUserId(uid);
            m.setRtcToken(token);
            groupMemberRepo.save(m);

            list.add(Map.of("userId", uid, "rtcToken", token));

            ws.notifyUser(
                    uid,
                    "GROUP_CALL_INVITE",
                    Map.of(
                        "fromUserId", hostId,
                        "fromUserName", hostName,
                        "fromUserPic", hostPic,
                        "channelName", channel,
                        "groupCallId", req.getGroupCallId(),
                        "callType", req.getCallType()
                    )
                );
        }

        return new Response(1, "Invited", Map.of(
                "channelName", channel,
                "members", list
        ));
    }
    
    @Override
    public Response getCallHistory(Integer userId) {

        List<CallHistoryResponse> list = new ArrayList<>();

        /* -------------------------
           1️⃣ 1-1 CALL HISTORY
         ------------------------- */

        List<CallLog> calls = callRepo.findCallHistory(userId);

        for (CallLog c : calls) {

            CallHistoryResponse r = new CallHistoryResponse();

            boolean outgoing = c.getCallerId().equals(userId);

            Integer otherUser = outgoing ? c.getReceiverId() : c.getCallerId();
    
            User host = userRepository.findById(otherUser).orElse(null);
        	String hostName = host != null ? host.getName() : "";
            r.setUserId(otherUser);
            r.setUserName(hostName);         
            r.setProfilePicUrl( userServices.getProfilePicUrl(otherUser));

            r.setGroupCall(false);
            r.setCallType(c.getCallType());
            r.setDirection(outgoing ? "outgoing" : "incoming");

            /* Missed call logic */

            String status;

            if ("rejected".equalsIgnoreCase(c.getStatus())) {
                status = outgoing ? "rejected" : "missed";
            }
            else if (c.getEndTime() == null) {
                status = "missed";
            }
            else {
                status = "completed";
            }

            r.setStatus(status);

            r.setStartTime(c.getStartTime());
            r.setEndTime(c.getEndTime());

            r.setDurationSeconds(getDuration(c.getStartTime(), c.getEndTime()));

            list.add(r);
        }


        /* -------------------------
           2️⃣ GROUP CALL HISTORY
         ------------------------- */

        List<GroupCall> groups = groupRepo.findGroupCalls(userId);

        for (GroupCall g : groups) {

            CallHistoryResponse r = new CallHistoryResponse();

            r.setGroupCall(true);
            r.setCallType(g.getCallType());

            r.setDirection(
                    g.getHostUserId().equals(userId) ? "outgoing" : "incoming"
            );

            /* Get group member names */

            List<Object[]> members =
                    groupMemberRepo.findGroupMembers(g.getId(), userId);
            GroupNameResult groupData = buildGroupName(members);

            r.setGroupName(groupData.getGroupName());
            r.setGroupUserIds(groupData.getUserIds());

            /* Missed group call detection */
           GroupCallMember member = groupMemberRepo
                    .findByGroupCallIdAndUserId(g.getId(), userId);

          
            String status;

            if (member != null && member.getJoined() == false) {
                status = "missed";
            } else {
                status = "completed";
            }

            r.setStatus(status);

            r.setStartTime(g.getCreatedOn());
            r.setDurationSeconds(0L);

            list.add(r);
        }


        /* -------------------------
           3️⃣ SORT BY LATEST
         ------------------------- */

        list.sort((a, b) -> b.getStartTime().compareTo(a.getStartTime()));

        return new Response(1, "Call history", list);
    }
    
    private Long getDuration(LocalDateTime start, LocalDateTime end) {

        if (start == null || end == null)
            return 0L;

        return java.time.Duration.between(start, end).getSeconds();
    }
    
    private GroupNameResult buildGroupName(List<Object[]> members) {

        List<String> names = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();

        for (Object[] obj : members) {
            ids.add((Integer) obj[0]);
            names.add((String) obj[1]);
        }

        String groupName;

        if (names.size() == 0)
            groupName = "Group Call";
        else if (names.size() == 1)
            groupName = names.get(0);
        else if (names.size() == 2)
            groupName = names.get(0) + " & " + names.get(1);
        else
            groupName = names.get(0) + ", " + names.get(1) + " & " + (names.size()-2) + " others";

        return new GroupNameResult(groupName, ids);
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
	@Override
	public Response acceptOneToOne(StartCallRequest request) {
		System.out.println("Channel name "+ request.getChannelName());
		 CallLog log = callRepo.findByChannelName(request.getChannelName());
	        if (log == null) {
	            System.out.println("Invalid channel: " + request.getChannelName());
	            return new Response(-1, "Invalid channel", null);
	        }
	        
	    log.setStatus("accepted");
	    callRepo.save(log);
	    return new Response(1,"Accept the call", null);
	}
}
