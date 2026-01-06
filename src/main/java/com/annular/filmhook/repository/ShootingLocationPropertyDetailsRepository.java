package com.annular.filmhook.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ShootingLocationPropertyDetails;
import com.annular.filmhook.model.ShootingPropertyStatus;

@Repository
public interface ShootingLocationPropertyDetailsRepository extends JpaRepository<ShootingLocationPropertyDetails, Integer>{
	
	List<ShootingLocationPropertyDetails> findAllByUser_UserIdAndStatusIn(Integer userId, List<ShootingPropertyStatus> statuses);


//	@Query("SELECT DISTINCT p FROM ShootingLocationPropertyDetails p LEFT JOIN FETCH p.mediaFiles")
//	List<ShootingLocationPropertyDetails> findAllWithMediaFiles();
	@Query(
		    "SELECT p " +
		    "FROM ShootingLocationPropertyDetails p " +
		    "WHERE p.industry.industryId IN :industryIds " +
		    "AND p.status = 'APPROVED'"
		)
		List<ShootingLocationPropertyDetails>
		findAllActiveByIndustryIndustryId(
		        @Param("industryIds") List<Integer> industryIds
		);


	 
	 @Modifying
	    @Transactional
	    @Query("UPDATE ShootingLocationPropertyDetails s SET s.status = :status WHERE s.user.id = :userId")
	    void deactivateShootingPropertyByUserId(  @Param("userId") Integer userId, @Param("status") ShootingPropertyStatus status);
	
	 List<ShootingLocationPropertyDetails> findByIndustryIndustryIdAndStatusTrue(Integer industryId);
	 boolean existsByPropertyCode(String propertyCode);

	 List<ShootingLocationPropertyDetails> findByStatusOrderByIdDesc(ShootingPropertyStatus status);
	 
	 
}
	