package com.annular.filmhook.service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.Story;
import com.annular.filmhook.webmodel.StoriesWebModel;
import com.annular.filmhook.webmodel.UserIdAndNameWebModel;

import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StoriesService {

    StoriesWebModel uploadStory(StoriesWebModel inputData);

    List<StoriesWebModel> getStoryByUserId(Integer userId);

    Resource getStoryFile(Integer userId, String category, String fileId);

    void deleteStory(Story storyToUpdate);

    Story deleteStoryById(Integer id);

    List<Story> deleteStoryByUserId(Integer userId);

    Optional<Story> updateStoryView(Integer userId, String storyId);

    List<Story> getMoreThanOneDayStories();

    void deleteExpiredStories(List<Story> storyList);

    List<UserIdAndNameWebModel> getUserIdAndName(Integer userId);

	List<StoriesWebModel> getUserStoriesByUserId(Integer userId);
}
