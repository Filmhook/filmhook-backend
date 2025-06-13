package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.ShootingLocationPropertyReview;

public interface ShootingLocationPropertyReviewRepository extends JpaRepository<ShootingLocationPropertyReview, Long> {
    List<ShootingLocationPropertyReview> findByPropertyId(Integer propertyId);
    
    
    
    
}
