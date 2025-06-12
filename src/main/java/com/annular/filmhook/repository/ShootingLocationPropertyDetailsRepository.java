package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ShootingLocationPropertyDetails;

@Repository
public interface ShootingLocationPropertyDetailsRepository extends JpaRepository<ShootingLocationPropertyDetails, Integer>{
	
	@Query("SELECT p FROM ShootingLocationPropertyDetails p WHERE p.user.id = :userId")
	List<ShootingLocationPropertyDetails> findAllByUserId(@Param("userId") Integer userId);

	@Query("SELECT p FROM ShootingLocationPropertyDetails p LEFT JOIN FETCH p.mediaFiles")
	List<ShootingLocationPropertyDetails> findAllWithMediaFiles();
 
//	@Query("SELECT p FROM ShootingLocationPropertyDetails p WHERE p.industry.id = :industryId")
//	List<ShootingLocationPropertyDetails> findAllByIndustryId(@Param("industryId") Integer industryId);
	
}
	