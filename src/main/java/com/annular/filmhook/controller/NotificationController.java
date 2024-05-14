package com.annular.filmhook.controller;

import com.annular.filmhook.Response;

import com.annular.filmhook.model.Notifications;
import com.annular.filmhook.service.NotificationService;
import com.annular.filmhook.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    public static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    NotificationService notificationService;

    @GetMapping("/getUserNotifications")
    public Response getUserNotifications(@RequestParam("userId") Integer userId) {
        try {
            List<Notifications> notificationList = notificationService.getNotificationsByUserId(userId);
            if(!Utility.isNullOrEmptyList(notificationList)) return new Response(1, "Notifications found successfully...", notificationList);
        } catch (Exception e) {
            logger.error("Error at getUserNotifications() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return new Response(-1, "Fail", "");
    }

}
