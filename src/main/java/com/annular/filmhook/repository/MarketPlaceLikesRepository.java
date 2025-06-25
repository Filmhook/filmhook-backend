package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.MarketPlaceLikes;

public interface MarketPlaceLikesRepository extends JpaRepository<MarketPlaceLikes, Integer> {
	Optional<MarketPlaceLikes> findByProductIdAndLikedByUserId(Integer productId, Integer userId);
	List<MarketPlaceLikes> findByLikedBy_UserIdAndStatus(Integer userId, Boolean status);


}
