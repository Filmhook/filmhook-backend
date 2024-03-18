package com.annular.filmhook.service;

import com.annular.filmhook.model.Story;
import com.annular.filmhook.webmodel.StoriesWebModel;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Optional;

public interface StoriesService {

    StoriesWebModel uploadStory(StoriesWebModel inputData);
    List<StoriesWebModel> getStoryByUserId(Integer userId);
    Resource getStoryFile(Integer userId, String category, String fileId);
    void deleteStoryById(Integer id);
    void deleteStoryByUserId(Integer userId);
    Optional<Story> updateStoryView(Integer userId, String storyId);
}
