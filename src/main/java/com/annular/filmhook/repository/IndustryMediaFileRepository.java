package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.IndustryMediaFiles;

@Repository
public interface IndustryMediaFileRepository extends JpaRepository<IndustryMediaFiles, Integer>{

	 @Query("Select m from IndustryMediaFiles m where m.user.userId=:userId and m.status=true")
	List<IndustryMediaFiles> getMediaFilesByUserIdAndCategory(Integer userId);

	 @Query("SELECT imf FROM IndustryMediaFiles imf WHERE imf.user IS NULL OR imf.status=true")
	    List<IndustryMediaFiles> getAllUnverifiedIndustrialUsers();

}
