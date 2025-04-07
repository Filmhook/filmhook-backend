package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ReportPost;

@Repository
public interface ReportRepository extends JpaRepository<ReportPost, Integer> {

    List<ReportPost> findByPostId(Integer postId);

    List<ReportPost> findByUserId(Integer userId);

    @Query("SELECT COUNT(DISTINCT rp.postId) FROM ReportPost rp")
    int getTotalCount();


    @Query("SELECT COUNT(rp) FROM ReportPost rp WHERE rp.status = true")
    int getActiveCount();

    @Query("SELECT COUNT(rp) FROM ReportPost rp WHERE rp.status = false")
    int getInactiveCount();


}
