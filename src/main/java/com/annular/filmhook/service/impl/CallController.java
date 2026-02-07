package com.annular.filmhook.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.CallService;
import com.annular.filmhook.webmodel.EndCallRequest;
import com.annular.filmhook.webmodel.StartCallRequest;

@RestController
@RequestMapping("/api/call")
public class CallController {

    @Autowired
    private CallService callService;

    @PostMapping("/start")
    public Response start(@RequestBody StartCallRequest request) {
        return callService.startCall(request);
    }

    @PostMapping("/end")
    public Response end(@RequestBody EndCallRequest request) {
        return callService.endCall(request);
    }
}