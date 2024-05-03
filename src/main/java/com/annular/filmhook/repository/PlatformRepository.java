package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Platform;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Integer>{

	@Query("SELECT p FROM Platform p WHERE p.platformName = :platformName")
	Optional<Platform> findByPlatformName(String platformName);

}
