package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.Bookings;
import com.annular.filmhook.model.NotificationTypeEnum;
import com.annular.filmhook.model.Notifications;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.NotificationRepository;
import com.annular.filmhook.service.NotificationService;

import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.MailNotification;
import com.annular.filmhook.webmodel.NotificationWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    public static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    UserService userService;

    @Autowired
    MailNotification mailNotification;

    @Autowired
    NotificationRepository notificationRepository;

    @Override
    public void saveNotification(NotificationWebModel notificationWebModel) {
        try {
            Notifications notifications = Notifications.builder()
                    .notificationType(notificationWebModel.getNotificationType())
                    .notificationFrom(notificationWebModel.getNotificationFrom())
                    .notificationTo(notificationWebModel.getNotificationTo())
                    .message(notificationWebModel.getMessage())
                    .readFlag(false)
                    .status(true)
                    .createdBy(notificationWebModel.getCreatedBy())
                    .createdOn(new Date())
                    .updatedBy(notificationWebModel.getUpdatedBy())
                    .updatedOn(new Date())
                    .build();
            notificationRepository.saveAndFlush(notifications);
        } catch (Exception e) {
            logger.error("Error occurred at saveNotifications -> {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Notifications> getNotificationsByUserId(Integer userId) {
        return notificationRepository.getNotificationByUserId(userId);
    }

    @Override
    public void sendBookingRequestNotifications(Bookings savedBookingRequest) {
        try {
            User bookedBy = userService.getUser(savedBookingRequest.getBookedBy().getUserId()).orElse(null);
            User bookedUser = userService.getUser(savedBookingRequest.getBookedUser().getUserId()).orElse(null);
            if (bookedBy != null && bookedUser != null) {
                // Sending Mail Notification
                String subject = "FilmHook - Booking Request";
                String mailContent = "<p><b>" + bookedBy.getName() + "</b> requested your dates from <b>" + savedBookingRequest.getFromDate() + "</b> to <b>" + savedBookingRequest.getToDate() + "</b>."
                        + "<br>Please check the FilmHook app notifications to confirm or reject the request.</p>";
                mailNotification.sendEmailAsync(bookedUser.getName(), bookedUser.getEmail(), subject, mailContent);

                // Saving App Notifications
                NotificationWebModel notificationWebModel = NotificationWebModel.builder()
                        .notificationType(NotificationTypeEnum.BookingRequest)
                        .notificationFrom(bookedBy.getUserId())
                        .notificationTo(bookedUser.getUserId())
                        .message("Booking request created by " + bookedBy.getName())
                        .createdBy(bookedBy.getUserId())
                        .createdOn(new Date())
                        .build();
                this.saveNotification(notificationWebModel);
            }
        } catch (Exception e) {
            logger.error("Error at sendBookingRequestNotifications -> {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
