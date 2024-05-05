package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AuditionAcceptanceDetails;

@Repository
public interface AuditionAcceptanceRepository extends JpaRepository<AuditionAcceptanceDetails, Integer>{

	@Query("SELECT COUNT(a) FROM AuditionAcceptanceDetails a WHERE a.auditionRefId = :auditionId and a.isAuditionAccepted = true")
	Integer getAttendedCount(Integer auditionId);

	@Query("SELECT COUNT(a) FROM AuditionAcceptanceDetails a WHERE a.auditionRefId = :auditionId and a.isAuditionAccepted = false")
	Integer getIgnoredCount(Integer auditionId);

}
