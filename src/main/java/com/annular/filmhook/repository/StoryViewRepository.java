package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.Story;
import com.annular.filmhook.model.StoryView;
import com.annular.filmhook.model.User;


public interface StoryViewRepository extends JpaRepository<StoryView, Integer> {
	
	 boolean existsByMediaFileAndViewer(MediaFiles mediaFile, User viewer);

	    int countByMediaFile(MediaFiles mediaFile);

	    List<StoryView> findByMediaFile(MediaFiles mediaFile);

}
 