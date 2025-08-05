package com.annular.filmhook.service;

import com.annular.filmhook.model.Bookings;
import com.annular.filmhook.model.Notifications;
import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.NotificationWebModel;

import java.util.List;

public interface NotificationService {

    void saveNotification(NotificationWebModel notificationWebModel);

    List<Notifications> getNotificationsByUserId(Integer userId);

    void sendBookingRequestNotifications(Bookings savedBookingRequest);
    void sendNotificationToUser(Integer senderId, User receiver, String title, String message, String userType, Integer refId);

}
