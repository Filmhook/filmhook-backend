package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.MarketPlace;

@Repository
public interface MarketPlaceRepository extends JpaRepository<MarketPlace, Integer>{

	@Query("SELECT m FROM MarketPlace m WHERE m.rentalOrsale = :rentalOrsale and m.marketPlaceIsactive=true")
	List<MarketPlace> findByRentalOrSale(Boolean rentalOrsale);

    List<MarketPlace> findByUserId(Integer userId);

    @Query("SELECT m from MarketPlace m where m.companyName = :searchKey Or m.productName = :searchKey")
	List<MarketPlace> findBySearchKey(String searchKey);
}
