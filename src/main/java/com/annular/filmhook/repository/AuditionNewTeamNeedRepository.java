package com.annular.filmhook.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AuditionNewTeamNeed;



@Repository
public interface AuditionNewTeamNeedRepository extends JpaRepository<AuditionNewTeamNeed, Integer> {

    // Fetch all team needs with their project, by subProfessionId
    @Query("SELECT t FROM AuditionNewTeamNeed t " +
           "JOIN FETCH t.project p " +
           "JOIN FETCH p.company c " +
           "WHERE t.subProfession.subProfessionId = :subProfessionId")
    List<AuditionNewTeamNeed> findAllBySubProfessionId(@Param("subProfessionId") Integer subProfessionId);
    
    List<AuditionNewTeamNeed> findAllByProjectId(Integer projectId);
    

    @Query("SELECT tn FROM AuditionNewTeamNeed tn WHERE tn.status = true AND tn.project.shootEndDate <= :today")
    List<AuditionNewTeamNeed> findExpiredTeamNeeds(@Param("today") LocalDate today);

    @Query("SELECT t FROM AuditionNewTeamNeed t WHERE t.subProfession.subProfessionId = :subProfessionId AND t.status = true")
    List<AuditionNewTeamNeed> findActiveBySubProfessionId(@Param("subProfessionId") Integer subProfessionId);
    
    @Query("SELECT t FROM AuditionNewTeamNeed t " +
    	       "WHERE t.profession.filmProfessionId = :professionId " +
    	       "AND t.status = true")
    	List<AuditionNewTeamNeed> findActiveByProfessionId(@Param("professionId") Integer professionId);


    Optional<AuditionNewTeamNeed> findByIdAndStatusTrue(Integer id);
}
