package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ShootingLocationBusinessInformation;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;

@Repository
public interface BusinessInformationRepository extends JpaRepository<ShootingLocationBusinessInformation, Long> {
	 Optional<ShootingLocationBusinessInformation> findByPropertyDetails(ShootingLocationPropertyDetails propertyDetails);
}
