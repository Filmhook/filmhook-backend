package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Platform;
import com.annular.filmhook.model.PlatformPermanentDetail;

@Repository
public interface PlatformPermanentDetailRepository extends JpaRepository<PlatformPermanentDetail, Integer>{

	@Query("SELECT i FROM Platform i WHERE i.platformName = :platformName")
	Optional<Platform> findByPlatformName(String platformName);

}
