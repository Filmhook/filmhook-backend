package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.AuditionNewTeamNeed;
import com.annular.filmhook.model.AuditionView;
import com.annular.filmhook.model.User;

public interface AuditionViewRepository extends JpaRepository<AuditionView, Long> {
	   boolean existsByTeamNeedId_IdAndUser_UserId(Integer teamNeedId, Integer userId);
	    @Query("SELECT COUNT(v) FROM AuditionView v WHERE v.teamNeedId.id = :teamNeedId")
	    Integer countByTeamNeedId(Integer teamNeedId);
	    
	   
	  
    
}