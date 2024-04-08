package com.annular.filmhook.repository;

import com.annular.filmhook.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, Integer> {

    @Query("Select s from Story s where s.user.userId=:userId and s.status=true")
    List<Story> getStoryByUserId(Integer userId);

    @Query("Select s from Story s where s.user.userId=:userId and s.storyId=:storyId and s.status=true")
    Optional<Story> getStoryByUserIdAndStoryId(Integer userId, String storyId);

    @Query("Select s from Story s where s.status=true")
    List<Story> getActiveStories();

    //@Query("Select s from Story s where floor(round((now()-s.createdOn))/1000000) >= 1 and s.status=true")
    //@Query("Select s from Story s where s.status=true and s.createdOn >= DATE_SUB(NOW(), INTERVAL 1 DAY)")
    @Query(value = "select * from stories s where s.created_on >= DATE_SUB(NOW(), INTERVAL 1 DAY);", nativeQuery = true)
    List<Story> getMoreThanOneDayStories();
}