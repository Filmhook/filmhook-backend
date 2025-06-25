package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.BankDetails;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {
	 Optional<BankDetails> findByPropertyDetails(ShootingLocationPropertyDetails propertyDetails);
}
