package com.annular.filmhook.repository;

import com.annular.filmhook.model.Notifications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Integer> {

    @Query("Select n from Notifications n where n.notificationTo = :userId and n.readFlag = false")
    List<Notifications> getNotificationByUserId(Integer userId);

}
