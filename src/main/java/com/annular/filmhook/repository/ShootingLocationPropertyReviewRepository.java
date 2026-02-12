package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.ShootingLocationPropertyReview;

public interface ShootingLocationPropertyReviewRepository extends JpaRepository<ShootingLocationPropertyReview, Integer> {
    List<ShootingLocationPropertyReview> findByPropertyId(Integer propertyId);
    List<ShootingLocationPropertyReview> findByPropertyIdAndUser_UserId(Integer propertyId, Integer userId);

    
    List<ShootingLocationPropertyReview> 
    findByProperty_IdOrderByCreatedOnDesc(Integer propertyId);
    
    
    @Query("SELECT r FROM ShootingLocationPropertyReview r " +
            "JOIN FETCH r.user u " +
            "LEFT JOIN FETCH r.ownerReplyBy o " +
            "WHERE r.property.id = :propertyId " +
            "ORDER BY r.createdOn DESC")
     List<ShootingLocationPropertyReview> 
     findAllByPropertyId(@Param("propertyId") Integer propertyId);
    
}
