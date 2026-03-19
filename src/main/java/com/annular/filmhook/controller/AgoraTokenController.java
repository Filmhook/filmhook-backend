package com.annular.filmhook.controller;

import com.annular.filmhook.Response;

import com.annular.filmhook.webmodel.AgoraWebModel;
import com.annular.filmhook.service.AgoraTokenService;

import com.annular.filmhook.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agora")
public class AgoraTokenController {

	  @Autowired
	    private AgoraTokenService service;

	    @PostMapping("/rtc-token")
	    public Response getRTCToken(@RequestBody AgoraWebModel model) {
	        String token = service.getRTCToken(model);
	        return token == null ? new Response(-1, "Error", null)
	                             : new Response(1, "Success", token);
	    }

	    @PostMapping("/rtm-token")
	    public Response getRTMToken(@RequestBody AgoraWebModel model) {
	        String token = service.getRTMToken(model);
	        return token == null ? new Response(-1, "Error", null)
	                             : new Response(1, "Success", token);
	    }

	    @PostMapping("/chat-token")
	    public Response getChatToken(@RequestBody AgoraWebModel model) {
	        String token = service.getChatToken(model);
	        return token == null ? new Response(-1, "Error", null)
	                             : new Response(1, "Success", token);
	    }

}
