package com.annular.filmhook.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.UserSession;
import com.annular.filmhook.repository.UserSessionRepository;
import com.annular.filmhook.service.CallService;
import com.annular.filmhook.webmodel.AgoraWebModel;
import com.annular.filmhook.webmodel.CallHistoryDTO;
import com.annular.filmhook.webmodel.EndCallRequest;
import com.annular.filmhook.webmodel.GroupCallEndRequest;
import com.annular.filmhook.webmodel.GroupCallInviteRequest;
import com.annular.filmhook.webmodel.GroupCallJoinRequest;
import com.annular.filmhook.webmodel.GroupCallStartRequest;
import com.annular.filmhook.webmodel.StartCallRequest;

@RestController
@RequestMapping("/api/call")
public class CallController {
   @Autowired
    private CallService callService;
    @Autowired
    private UserSessionRepository userSessionRepository;

 
    @PostMapping("/start")
    public Response start(@RequestBody StartCallRequest request) {
        return callService.startCall(request);
    }
    
    @PostMapping("/accept/OneToOne")
    public Response acceptOneToOne(@RequestBody StartCallRequest request) {
        return callService.acceptOneToOne(request);
    }

    @PostMapping("/end")
    public Response end(@RequestBody EndCallRequest request) {
        return callService.endCall(request);
    }
    
    @PostMapping("/get-rtc")
    public Response getRtc(@RequestBody AgoraWebModel model) {
      
        return callService.getRtcTokenByChannel(model.getChannelName());
    }
    
    /* ----------------------- Group Call START ------------------------ */
    @PostMapping("/group/start")
    public Response startGroupCall(@RequestBody GroupCallStartRequest req) {
        return callService.startGroupCall(req);
    }

    /* ----------------------- Join Group Call (Late Join) ------------- */
    @PostMapping("/group/join")
    public Response joinGroupCall(@RequestBody GroupCallJoinRequest req) {
        return callService.joinGroupCall(req);
    }

    /* ----------------------- Leave Group Call ------------------------ */
    @PostMapping("/group/leave")
    public Response leaveGroupCall(@RequestBody GroupCallJoinRequest req) {
        return callService.leaveGroupCall(req);
    }

    /* ----------------------- End Entire Group Call ------------------ */
    @PostMapping("/group/end")
    public Response endGroupCall(@RequestBody GroupCallEndRequest req) {
        return callService.endGroupCall(req);
    }

    /* ----------------------- Invite to existing 1-1 call → group ----- */
    @PostMapping("/group/invite")
    public Response inviteToGroup(@RequestBody GroupCallInviteRequest req) {
        return callService.inviteToGroup(req);
    }
    
    @PostMapping("/history")
    public Response getCallHistory(@RequestParam Integer userId) {

        return callService.getCallHistory(userId);
    }

    
    @PostMapping("/test-push")
    public ResponseEntity<?> testPush(@RequestParam Integer userId, 
                                      @RequestParam String title,
                                      @RequestParam String body) {

        List<UserSession> sessions =
                userSessionRepository.findByUserIdAndIsActive(userId, true);

        if (sessions.isEmpty()) {
            return ResponseEntity.ok(new Response(-1, "No active devices found", null));
        }

        int sentCount = 0;

        for (UserSession s : sessions) {

            String deviceToken = s.getFirebaseToken();

            if (deviceToken != null && !deviceToken.trim().isEmpty()) {

                callService.sendTestNotification(deviceToken, title, body);
                sentCount++;
            }
        }

        return ResponseEntity.ok(
                new Response(1, "Notification sent to " + sentCount + " device(s)", null)
        );
    }
    
    @PostMapping("/clear-history")
    public Response clearCallHistory(@RequestBody CallHistoryDTO dto) {

        return callService.clearCallHistory(dto);
    }

}