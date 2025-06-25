package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.BusinessInformation;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;

@Repository
public interface BusinessInformationRepository extends JpaRepository<BusinessInformation, Long> {
	 Optional<BusinessInformation> findByPropertyDetails(ShootingLocationPropertyDetails propertyDetails);
}
