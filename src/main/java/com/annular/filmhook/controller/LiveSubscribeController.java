package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.LiveSubscribeService;
import com.annular.filmhook.webmodel.LiveSubscribeWebModel;

@RestController
@RequestMapping("/liveSubscribe")
public class LiveSubscribeController {

    @Autowired
    LiveSubscribeService liveSubscribeService;

    public static final Logger logger = LoggerFactory.getLogger(LiveSubscribeController.class);

    @PostMapping("/saveLiveSubscribe")
    public ResponseEntity<?> saveLiveSubscribe(@RequestBody LiveSubscribeWebModel liveSubscribeWebModel) {
        try {
            logger.info("saveLiveSubscribe controller start");
            return liveSubscribeService.saveLiveSubscribe(liveSubscribeWebModel);
        } catch (Exception e) {
            logger.error("saveLiveSubscribe Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/getLiveSubcribes")
    public ResponseEntity<?> getLiveSubscribes(@RequestBody LiveSubscribeWebModel liveSubscribeWebModel) {
        try {
            logger.info("getLiveSubscribes controller start");
            return liveSubscribeService.getLiveSubscribes(liveSubscribeWebModel);
        } catch (Exception e) {
            logger.error("getLiveSubscribes Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }
}
