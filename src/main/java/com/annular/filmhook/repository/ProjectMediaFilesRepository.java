package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.IndustryMediaFiles;
import com.annular.filmhook.model.ProjectMediaFiles;

@Repository
public interface ProjectMediaFilesRepository extends JpaRepository<ProjectMediaFiles, Integer>{

	@Query("SELECT pmf FROM ProjectMediaFiles pmf WHERE pmf.user.userId = :userId AND pmf.permanentprofessionid = :platformPermanentId")
	List<ProjectMediaFiles> getMediaFilesByUserIdAndPlatformPermanentId(Integer userId, Integer platformPermanentId);

}
