package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Platform;
import com.annular.filmhook.model.PlatformPermanentDetail;

@Repository
public interface PlatformPermanentDetailRepository extends JpaRepository<PlatformPermanentDetail, Integer>{

	@Query("SELECT i FROM Platform i WHERE i.platformName = :platformName")
	Optional<Platform> findByPlatformName(String platformName);

	@Modifying
	@Transactional
	@Query("select ppd.ppdPlatformId from PlatformPermanentDetail ppd where ppd.industryUserPermanentDetails.iupdId =:iupdId")
	List<Integer> findByiupdId(Integer iupdId);

	@Query("select p from PlatformPermanentDetail p where p.userId = :userId")
	List<PlatformPermanentDetail> findByUserId(Integer userId);

	@Transactional
    @Query("DELETE FROM PlatformPermanentDetail p WHERE p.industryUserPermanentDetails.id = :iupdId")
	void deleteByIndustryUserPermanentDetailsId(Integer iupdId); 
	
}
