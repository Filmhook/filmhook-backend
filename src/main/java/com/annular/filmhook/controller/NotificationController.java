package com.annular.filmhook.controller;

import com.annular.filmhook.Response;

import com.annular.filmhook.model.Notifications;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.NotificationService;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.InAppNotificationWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    public static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    NotificationService notificationService;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getUserNotifications")
    public Response getUserNotifications(@RequestParam("userId") Integer userId) {
        try {
            List<Notifications> notificationList = notificationService.getNotificationsByUserId(userId);
            if (!Utility.isNullOrEmptyList(notificationList))
                return new Response(1, "Notifications found successfully...", notificationList);
            else
                return new Response(1, "Notifications not found for this user id " + userId, null);
        } catch (Exception e) {
            logger.error("Error at getUserNotifications() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return new Response(-1, "Fail", "");
    }
    
    @PostMapping("/send-to-users")
    public ResponseEntity<?> sendToSelectedUsers(@RequestBody InAppNotificationWebModel webModel) {
        Integer senderId = webModel.getSenderId();
        List<Integer> receiverUserIds = webModel.getReceiverIds();
        String title = webModel.getTitle();
        String message = webModel.getMessage();
        String userType = webModel.getUserType();
        Integer refId = webModel.getId();

        for (Integer receiverId : receiverUserIds) {
            Optional<User> userOpt = userRepository.findById(receiverId);
            if (userOpt.isPresent()) {
                User receiver = userOpt.get();
                notificationService.sendNotificationToUser(senderId, receiver, title, message, userType, refId);
            }
        }

        return ResponseEntity.ok("Notifications sent successfully.");
    }
    
    @PostMapping("/send-to-all-users")
    public ResponseEntity<?> sendToAllUsers(@RequestBody InAppNotificationWebModel webModel) {
        Integer senderId = webModel.getSenderId();
        String title = webModel.getTitle();
        String message = webModel.getMessage();
        String userType = webModel.getUserType();
        Integer refId = webModel.getId();

        List<User> allUsers = userRepository.findAll();

        for (User receiver : allUsers) {
            notificationService.sendNotificationToUser(senderId, receiver, title, message, userType, refId);
        }

        return ResponseEntity.ok("Notifications sent to all users successfully.");
    }
    
    @DeleteMapping("/deleteSelected")
    public ResponseEntity<Response> deleteSelectedNotifications(@RequestBody InAppNotificationWebModel model) {
        Response response = notificationService.deleteNotificationsByIds(model.getReceiverIds());
        return ResponseEntity.ok(response);
    }

}
