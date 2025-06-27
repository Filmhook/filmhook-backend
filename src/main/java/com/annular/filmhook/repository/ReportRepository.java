package com.annular.filmhook.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ReportPost;

@Repository
public interface ReportRepository extends JpaRepository<ReportPost, Integer> {

    List<ReportPost> findByPostId(Integer postId);

    List<ReportPost> findByUserId(Integer userId);

    @Query("SELECT COUNT(r) FROM ReportPost r WHERE r.createdOn BETWEEN :startDate AND :endDate")
    int getTotalCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT COUNT(r) FROM ReportPost r WHERE r.status = true AND r.createdOn BETWEEN :startDate AND :endDate")
    int getActiveCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT COUNT(r) FROM ReportPost r WHERE r.status = false AND r.createdOn BETWEEN :startDate AND :endDate")
    int getInactiveCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT COUNT(r) FROM ReportPost r WHERE (r.notificationCount IS NULL OR r.notificationCount = false) AND r.status = false")
    Integer countByNotificationCountIsNullOrNotificationCountFalseAndStatusTrue();

	@Query("SELECT r FROM ReportPost r WHERE r.deletePostSuspension = 1 AND r.updatedOn <= :sevenDaysAgo")
List<ReportPost> findOldSuspendedReports(@Param("sevenDaysAgo") Date sevenDaysAgo);






}
