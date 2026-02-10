package com.annular.filmhook.service.impl;

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
import com.annular.filmhook.webmodel.EndCallRequest;
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

    @PostMapping("/end")
    public Response end(@RequestBody EndCallRequest request) {
        return callService.endCall(request);
    }
    
    @PostMapping("/get-rtc")
    public Response getRtc(@RequestBody AgoraWebModel model) {
      
        return callService.getRtcTokenByChannel(model.getChannelName());
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

}