package com.annular.filmhook.controller;

import com.annular.filmhook.Response;

import com.annular.filmhook.model.AgoraWebModel;
import com.annular.filmhook.service.AgoraTokenService;

import com.annular.filmhook.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agora")
public class AgoraTokenController {

    private static final Logger logger = LoggerFactory.getLogger(AgoraTokenController.class);

    @Autowired
    AgoraTokenService agoraTokenService;

    // For RTC - RealTime Transcription : Voice and Video Call
    @PostMapping("/getVideoToken")
    public Response getVideoToken(@RequestBody AgoraWebModel agoraWebModel) {
        String token = agoraTokenService.getAgoraRTCToken(agoraWebModel);
        if (Utility.isNullOrBlankWithTrim(token) || token.contains("blank"))
            return new Response(-1, "Error", null);
        else
            return new Response(1, "Success", token);
    }

    // For RTM - RealTime Messaging : Chat
    @PostMapping("/getChatToken")
    public Response getChatToken(@RequestBody AgoraWebModel agoraWebModel) {
        String token = agoraTokenService.getAgoraRTMToken(agoraWebModel);
        if (Utility.isNullOrBlankWithTrim(token) || token.contains("blank"))
            return new Response(-1, "Error", null);
        else
            return new Response(1, "Success", token);
    }
}
