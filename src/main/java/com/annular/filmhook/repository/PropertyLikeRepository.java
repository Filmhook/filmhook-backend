package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.PropertyLike;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;
import com.annular.filmhook.model.User;

public interface PropertyLikeRepository extends JpaRepository<PropertyLike, Integer> {
    Optional<PropertyLike> findByPropertyAndLikedBy(ShootingLocationPropertyDetails property, User likedBy);
	
    Long countByProperty(ShootingLocationPropertyDetails property);
    
    List<PropertyLike> findByLikedBy(User user);

    @Query("SELECT pl FROM PropertyLike pl WHERE pl.property.id = :propertyId AND pl.likedBy.id = :userId")
    Optional<PropertyLike> findByPropertyIdAndLikedById(@Param("propertyId") Integer propertyId, @Param("userId") Integer userId);

}