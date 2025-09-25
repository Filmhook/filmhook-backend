package com.annular.filmhook.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.annular.filmhook.model.ServiceType;
import com.annular.filmhook.model.UserOffer;

public interface UserOfferRepository extends JpaRepository<UserOffer, Integer> {

	 Optional<UserOffer> findFirstByUserIdAndServiceTypeAndActiveIsTrueAndValidTillAfterOrderByValidTillDesc(
	            Integer userId,
	            ServiceType serviceType,
	            LocalDateTime now
	    );
}
