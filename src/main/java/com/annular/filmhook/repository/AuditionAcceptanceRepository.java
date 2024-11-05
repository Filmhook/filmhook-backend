package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AuditionAcceptanceDetails;

@Repository
public interface AuditionAcceptanceRepository extends JpaRepository<AuditionAcceptanceDetails, Integer> {

    @Query("SELECT COUNT(a) FROM AuditionAcceptanceDetails a WHERE a.auditionRefId = :auditionId and a.isAuditionAccepted = true")
    Integer getAttendedCount(Integer auditionId);

    @Query("SELECT COUNT(a) FROM AuditionAcceptanceDetails a WHERE a.auditionRefId = :auditionId and a.isAuditionAccepted = false")
    Integer getIgnoredCount(Integer auditionId);

    @Modifying
    @Query("DELETE FROM AuditionAcceptanceDetails aad WHERE aad.auditionRefId = :auditionRefId")
    void deleteByAuditionRefId(Integer auditionRefId);

    boolean existsByAuditionAcceptanceUserAndAuditionRefId(Integer auditionAcceptanceUser, Integer auditionRefId);

	

	List<AuditionAcceptanceDetails> findByAuditionRefId(Integer auditionRefId);


}
