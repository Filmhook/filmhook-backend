package com.annular.filmhook.controller;

import com.annular.filmhook.service.UserRecentActivityService;
import com.annular.filmhook.webmodel.RecentUserWebModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/recent")
public class UserRecentActivityController {

    @Autowired
    private UserRecentActivityService userRecentActivityService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecentUserWebModel>> getRecentUsers(@PathVariable Integer userId) {
        List<RecentUserWebModel> recentUsers = userRecentActivityService.getRecentUserActivities(userId);
        return ResponseEntity.ok(recentUsers);
    }

    @PostMapping("/search")
    public ResponseEntity<Void> saveSearch(@RequestParam Integer userId, @RequestParam Integer searchedUserId) {
        userRecentActivityService.saveSearchHistory(userId, searchedUserId);
        return ResponseEntity.ok().build();
    }
}

