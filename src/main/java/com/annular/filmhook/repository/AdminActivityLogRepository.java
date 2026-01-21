package com.annular.filmhook.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AdminActivityLog;

@Repository
public interface AdminActivityLogRepository extends JpaRepository<AdminActivityLog, Integer> {

	 // Count actions done today (Work Done)
    @Query("SELECT COUNT(a) FROM AdminActivityLog a " +
           "WHERE a.adminId = :adminId " +
           "AND DATE(a.createdOn) = CURRENT_DATE")
    int countTodayActivities(Integer adminId);

    // Fetch first activity of the day
    @Query("SELECT MIN(a.createdOn) FROM AdminActivityLog a " +
           "WHERE a.adminId = :adminId " +
           "AND DATE(a.createdOn) = CURRENT_DATE")
    LocalDateTime getFirstActivityOfDay(Integer adminId);

    // Fetch last activity of the day
    @Query("SELECT MAX(a.createdOn) FROM AdminActivityLog a " +
           "WHERE a.adminId = :adminId " +
           "AND DATE(a.createdOn) = CURRENT_DATE")
    LocalDateTime getLastActivityOfDay(Integer adminId);
}
