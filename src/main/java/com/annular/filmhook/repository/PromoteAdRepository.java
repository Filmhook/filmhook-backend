package com.annular.filmhook.repository;
import com.annular.filmhook.model.PromoteAd;
import com.annular.filmhook.model.PromoteAd.PromoteStatus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromoteAdRepository extends JpaRepository<PromoteAd, Integer> {

    PromoteAd findByPost_Id(Integer postId);
    
    @Query("SELECT p FROM PromoteAd p WHERE p.post.id = :postId ORDER BY p.createdOn DESC")
    List<PromoteAd> findAllByPostIdOrderByCreatedOnDesc(@Param("postId") Integer postId);
    
    @Query("SELECT p FROM PromoteAd p WHERE p.paymentStatus='SUCCESS' ORDER BY p.createdOn DESC")
    List<PromoteAd> findRecentSuccessPromotions();
    
    @Query(
    	    "SELECT p FROM PromoteAd p " +
    	    "WHERE p.paymentStatus = 'SUCCESS' " +
    	    "AND p.status IN ('Running', 'Completed') " +
    	    "AND p.post.user.userId = :userId " +
    	    "ORDER BY " +
    	    "CASE p.status " +
    	    "WHEN 'Running' THEN 1 " +
    	    "WHEN 'Completed' THEN 2 " +
    	    "END, " +
    	    "p.createdOn DESC"
    	)
    	List<PromoteAd> findRecentRunningOrCompletedByUserId(@Param("userId") Integer userId);
    
    List<PromoteAd> findByStatus(PromoteAd.PromoteStatus status);
    
    @Query("SELECT p FROM PromoteAd p JOIN FETCH p.post WHERE p.status = :status")
    List<PromoteAd> findRunningPromotesWithPost(@Param("status") PromoteStatus status);



}