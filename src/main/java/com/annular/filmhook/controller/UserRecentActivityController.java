package com.annular.filmhook.controller;

import com.annular.filmhook.service.UserRecentActivityService;
import com.annular.filmhook.webmodel.RecentUserWebModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/recent")
public class UserRecentActivityController {

    @Autowired
    private UserRecentActivityService userRecentActivityService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getRecentUsers(@PathVariable Integer userId) {
        List<RecentUserWebModel> list = userRecentActivityService.getRecentUserActivities(userId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/search/save")
    public ResponseEntity<?> saveSearch(
            @RequestParam Integer userId,
            @RequestParam Integer searchedUserId,
            @RequestParam String source // "search" or "chat"
    ) {
        userRecentActivityService.saveSearchHistory(userId, searchedUserId, source);
        return ResponseEntity.ok("Saved");
    }
    
    
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteSingleSearchHistory(
            @RequestParam Integer userId,
            @RequestParam Integer targetUserId,
            @RequestParam String source) {

        userRecentActivityService.deleteSearchHistory(userId, targetUserId, source);
        return ResponseEntity.ok("Search history entry deleted.");
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllSearchHistory(@RequestParam Integer userId) {
        userRecentActivityService.deleteAllSearchHistory(userId);
        return ResponseEntity.ok("All search history entries deleted.");
    }
    
    @PostMapping("/pin")
    public ResponseEntity<?> pinOrUnpinProfile(
            @RequestParam Integer userId,
            @RequestParam Integer targetUserId,
            @RequestParam String source,
            @RequestParam Boolean pin
    ) {
        try {
            userRecentActivityService.pinUserProfile(userId, targetUserId, source, pin);
            return ResponseEntity.ok(pin ? "Profile pinned successfully" : "Profile unpinned successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

