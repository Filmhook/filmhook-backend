package com.annular.filmhook.service.impl;

import com.annular.filmhook.controller.StoriesController;
import com.annular.filmhook.model.Story;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.StoryRepository;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.StoriesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.StoriesWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StoriesServiceImpl implements StoriesService {

    public static final Logger logger = LoggerFactory.getLogger(StoriesController.class);

    @Autowired
    FileUtil fileUtil;

    @Autowired
    StoryRepository storyRepository;

    @Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    UserService userService;

    @Override
    public StoriesWebModel uploadStory(StoriesWebModel inputData) {
        StoriesWebModel storiesWebModel = null;
        try {
            Optional<User> userFromDB = userService.getUser(inputData.getUserId());
            if (userFromDB.isPresent()) {
                storiesWebModel = new StoriesWebModel();

                Story story = this.prepareStories(inputData, userFromDB.get());
                // 1. Save first in Stories table MySQL
                storyRepository.save(story);
                logger.info("Story unique id saved in mysql :- " + story.getStoryId());

                // 2. Save in media files table MySQL
                FileOutputWebModel fileOutputWebModel = mediaFilesService.saveMediaFiles(inputData.getFileInputWebModel(), userFromDB.get());
                if (fileOutputWebModel != null) {
                    List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
                    outputWebModelList.add(fileOutputWebModel);
                    storiesWebModel.setFileOutputWebModel(outputWebModelList);

                    // 3. Upload into S3
                    File file = File.createTempFile(fileOutputWebModel.getFileId(), null);
                    FileUtil.convertMultiPartFileToFile(inputData.getFileInputWebModel().getFile(), file);
                    String response = fileUtil.uploadFile(file, fileOutputWebModel.getFilePath());
                    logger.info("File Upload in S3 response :- " + response);
                    if (response != null && response.equalsIgnoreCase("File Uploaded")) {
                        file.delete(); // deleting temp file
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error at saveGalleryFiles()...", e);
        }
        return storiesWebModel;
    }

    private Story prepareStories(StoriesWebModel inputData, User user) {

        Story story = new Story();
        story.setStoryId(UUID.randomUUID().toString());
        story.setType(inputData.getType());
        story.setDescription(inputData.getDescription());
        story.setExpiryTime(inputData.getExpiryTime());
        story.setExpiryExpression(inputData.getExpiryExpression());
        story.setViewCount(inputData.getViewCount());
        story.setStatus(true);
        story.setUser(user);
        story.setCreatedBy(user.getUserId());
        //story.setUpdatedBy();
        //story.setUpdatedOn();

        return story;
    }

    @Override
    public List<StoriesWebModel> getStoryByUserId(Integer userId) {
        List<StoriesWebModel> storiesWebModelList = new ArrayList<>();
        try {
            List<Story> storyList = storyRepository.getStoryByUserId(userId);
            if (storyList != null && !storyList.isEmpty()) {
                storiesWebModelList = storyList.stream().map(this::transformData).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error at getStoryByUserId()...", e);
        }
        return storiesWebModelList;
    }

    private StoriesWebModel transformData(Story story) {
        StoriesWebModel storiesWebModel = new StoriesWebModel();

        storiesWebModel.setStoryId(story.getStoryId());
        storiesWebModel.setType(story.getType());
        storiesWebModel.setDescription(story.getDescription());
        storiesWebModel.setExpiryExpression(story.getExpiryExpression());
        storiesWebModel.setExpiryTime(story.getExpiryTime());
        storiesWebModel.setViewCount(story.getViewCount());
        storiesWebModel.setStatus(story.getStatus());
        storiesWebModel.setUserId(story.getUser().getUserId());
        storiesWebModel.setCreatedOn(story.getCreatedOn());
        storiesWebModel.setCreatedBy(story.getCreatedBy());

        List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.getMediaFilesByUserAndCategory(story.getUser().getUserId(), "Stories");
        if (fileOutputWebModelList != null && !fileOutputWebModelList.isEmpty()) {
            storiesWebModel.setFileOutputWebModel(fileOutputWebModelList);
        }

        return storiesWebModel;
    }

    @Override
    public Resource getStoryFile(Integer userId, String category, String fileId) {
        try {
            Optional<User> userFromDB = userService.getUser(userId);
            if (userFromDB.isPresent()) {
                String filePath = FileUtil.generateFilePath(userFromDB.get(), category, fileId);
                return new ByteArrayResource(fileUtil.downloadFile(filePath));
            }
        } catch (Exception e) {
            logger.error("Error at getGalleryFile()...", e);
        }
        return null;
    }

    @Override
    public void deleteStoryById(Integer id) {
        Optional<Story> story = storyRepository.findById(id);
        if (story.isPresent()) {
            story.get().setStatus(false);
            storyRepository.save(story.get());
        }
    }

    @Override
    public void deleteStoryByUserId(Integer userId) {
        try {
            // Deleting the MySQL Records
            List<Story> storyList = storyRepository.getStoryByUserId(userId);
            if (storyList != null && !storyList.isEmpty()) {
                storyList.forEach(item -> this.deleteStoryById(item.getId()));
            }
            mediaFilesService.deleteMediaFilesByUserIdAndCategory(userId, "Stories");
        } catch (Exception e) {
            logger.error("Error at deleteStoryByUserId()...", e);
        }
    }

    @Override
    public Optional<Story> updateStoryView(Integer userId, String storyId) {
        Optional<Story> story = storyRepository.getStoryByUserIdAndStoryId(userId, storyId);
        if (story.isPresent()) {
            story.get().setViewCount(story.get().getViewCount() + 1);
            storyRepository.save(story.get());
        }
        return story;
    }

    @Override
    public List<Story> getMoreThanOneDayStories() {
        return storyRepository.getMoreThanOneDayStories();
    }

    @Override
    public void deleteExpiredStories(List<Story> activeStories) {
        logger.info("Active Stories size :- " + activeStories.size());
        if (!activeStories.isEmpty()) {
            activeStories.stream()
                    .filter(Objects::nonNull)
                    .forEach(item -> this.deleteStoryById(item.getId()));
        }
    }
}