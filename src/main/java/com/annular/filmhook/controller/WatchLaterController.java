package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.service.WatchLaterService;
import com.annular.filmhook.webmodel.PostWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/watchLater")
public class WatchLaterController {

	   public static final Logger logger = LoggerFactory.getLogger(WatchLaterController.class);
    @Autowired
    private WatchLaterService watchLaterService;

    // ✅ Add / Remove (toggle)
    @PostMapping("/toggle")
    public Response toggleWatchLater(@RequestParam Integer userId, @RequestParam Integer Id) {
        return watchLaterService.toggleWatchLater(userId, Id);
    }
    @GetMapping("/{userId}")
    public Response getWatchLaterPosts(@PathVariable String userId) {
    	return watchLaterService.getActiveWatchLaterPosts(userId);
    }
}
