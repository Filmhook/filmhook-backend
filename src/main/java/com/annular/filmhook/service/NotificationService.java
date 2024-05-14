package com.annular.filmhook.service;

import com.annular.filmhook.model.Bookings;
import com.annular.filmhook.model.Notifications;
import com.annular.filmhook.webmodel.BookingWebModel;
import com.annular.filmhook.webmodel.NotificationWebModel;

import java.util.List;

public interface NotificationService {

    void saveNotification(NotificationWebModel notificationWebModel);

    List<Notifications> getNotificationsByUserId(Integer userId);

    void sendBookingRequestNotifications(BookingWebModel bookingWebModel, Bookings savedBookingRequest);
}
