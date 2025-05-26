package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.MarketPlaceLike;
import com.annular.filmhook.model.User;

@Repository
public interface MarketPlaceLikeRepository extends JpaRepository<MarketPlaceLike, Integer> {



	MarketPlaceLike findByMarketPlaceIdAndMarketPlacelikedBy(Integer marketPlaceId, Integer likedById);

	 @Query("SELECT COUNT(ml) FROM MarketPlaceLike ml WHERE ml.marketPlaceId = :marketPlaceId AND ml.status = true")
	    Long countByMarketPlaceIdAndStatus(@Param("marketPlaceId") Integer marketPlaceId);

	Boolean existsByMarketPlaceIdAndMarketPlacelikedByAndStatus(Integer marketPlaceId, Integer id, boolean b);

}
