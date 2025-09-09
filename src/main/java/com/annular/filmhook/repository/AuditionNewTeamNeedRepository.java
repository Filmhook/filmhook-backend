package com.annular.filmhook.repository;

import java.util.List;

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
}
