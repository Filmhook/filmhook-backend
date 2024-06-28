package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.FriendRequestService;
import com.annular.filmhook.webmodel.FollowersRequestWebModel;


@RestController
@RequestMapping("/friendRequest")
public class FriendRequestController {

    public static final Logger logger = LoggerFactory.getLogger(FriendRequestController.class);

    @Autowired
    FriendRequestService friendRequestService;

    @PostMapping("/saveFriendRequest")
    public ResponseEntity<?> saveFollowersRequest(@RequestBody FollowersRequestWebModel followersRequestWebModel) {
        try {
            return friendRequestService.saveFollowersRequest(followersRequestWebModel);
        } catch (Exception e) {
            logger.error("saveFollowersRequest Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @GetMapping("/getFriendRequest")
    public ResponseEntity<?> getFriendRequest(@RequestParam("userId") Integer userId) {
        try {
            return friendRequestService.getFriendRequest(userId);
        } catch (Exception e) {
            logger.error("getFriendRequest Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PutMapping("/updateFriendRequest")
    public ResponseEntity<?> updateFriendRequest(@RequestBody FollowersRequestWebModel followersRequestWebModel) {
        try {
            return friendRequestService.updateFriendRequest(followersRequestWebModel);
        } catch (Exception e) {
            logger.error("updateFriendRequest Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

}
