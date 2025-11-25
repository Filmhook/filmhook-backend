package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ShootingLocationOwnerBankDetails;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;

@Repository
public interface BankDetailsRepository extends JpaRepository<ShootingLocationOwnerBankDetails, Long> {
	 Optional<ShootingLocationOwnerBankDetails> findByPropertyDetails(ShootingLocationPropertyDetails propertyDetails);
}
