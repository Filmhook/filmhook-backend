package com.annular.filmhook.service.impl;

import com.annular.filmhook.controller.StoriesController;
import com.annular.filmhook.model.FilmProfessionPermanentDetail;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.Story;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.FilmProfessionPermanentDetailRepository;
import com.annular.filmhook.repository.StoryRepository;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.StoriesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.StoriesWebModel;
import com.annular.filmhook.webmodel.UserIdAndNameWebModel;

import com.annular.filmhook.webmodel.UserWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.Map;
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

    @Autowired
    FilmProfessionPermanentDetailRepository professionPermanentDetailsRepository;
    

    @Override
    public StoriesWebModel uploadStory(StoriesWebModel inputData) {
        try {
            Optional<User> userFromDB = userService.getUser(inputData.getUserId());
            if (userFromDB.isPresent()) {
                Story story = this.prepareStories(inputData, userFromDB.get());

                // 1. Save the story in the Stories table (MySQL)
                storyRepository.saveAndFlush(story);
                logger.info("Story unique id saved in MySQL: {}", story.getStoryId());

                // 2. Save media files in the media_files table (MySQL)
                inputData.getFileInputWebModel().setCategory(MediaFileCategory.Stories);
                inputData.getFileInputWebModel().setCategoryRefId(story.getId()); // Add story table reference in media files table
                List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.saveMediaFiles(inputData.getFileInputWebModel(), userFromDB.get());

                // Transform story data to include fileOutputWebModel
                StoriesWebModel storiesWebModel = this.transformData(story);
                storiesWebModel.setFileOutputWebModel(fileOutputWebModelList); // Set fileOutputWebModel

                return storiesWebModel;
                    
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error at uploadStory() -> {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    private Story prepareStories(StoriesWebModel inputData, User user) {

        Story story = new Story();
        story.setStoryId(UUID.randomUUID().toString());
        story.setType(inputData.getType());
        story.setDescription(inputData.getDescription());
        story.setViewCount(inputData.getViewCount() == null ? 0 : inputData.getViewCount());
        story.setStatus(true);
        story.setUser(user);
        story.setCreatedBy(user.getUserId());
        //story.setUpdatedBy();
        //story.setUpdatedOn();

        return story;
    }

//    @Override
//    public List<StoriesWebModel> getStoryByUserId(Integer userId) {
//        List<StoriesWebModel> storiesWebModelList = new ArrayList<>();
//        try {
//            List<Story> storyList = storyRepository.getStoryByUserId(userId);
//            if (!Utility.isNullOrEmptyList(storyList)) {
//                storiesWebModelList = storyList.stream().map(this::transformData).collect(Collectors.toList());
//            }
//        } catch (Exception e) {
//            logger.error("Error at getStoryByUserId() -> {}", e.getMessage());
//        }
//        return storiesWebModelList;
//    }


//    @Override
//    public List<StoriesWebModel> getStoryByUserId(Integer userId) {
//        List<StoriesWebModel> storiesWebModelList = new ArrayList<>();
//        try {
//            List<Story> storyList = storyRepository.getStoryByUserId(userId);
//            if (!Utility.isNullOrEmptyList(storyList)) {
//                // Get the current time and the time 24 hours ago
//                LocalDateTime now = LocalDateTime.now();
//                LocalDateTime twentyFourHoursAgo = now.minusHours(24);
//
//                // Filter stories created within the last 24 hours
//                storiesWebModelList = storyList.stream()
//                        .filter(story -> convertToLocalDateTimeViaInstant(story.getCreatedOn()).isAfter(twentyFourHoursAgo))
//                        .map(this::transformData)
//                        .collect(Collectors.toList());
//            }
//        } catch (Exception e) {
//            logger.error("Error at getStoryByUserId() -> {}", e.getMessage());
//        }
//        return storiesWebModelList;
//    }
//
//    private LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
//        return Instant.ofEpochMilli(dateToConvert.getTime())
//                .atZone(ZoneId.systemDefault())
//                .toLocalDateTime();
//    }
//
//    private StoriesWebModel transformData(Story story) {
//        StoriesWebModel storiesWebModel = new StoriesWebModel();
//
//        storiesWebModel.setStoryId(story.getStoryId());
//        storiesWebModel.setType(story.getType());
//        storiesWebModel.setDescription(story.getDescription());
//        storiesWebModel.setViewCount(story.getViewCount());
//        storiesWebModel.setStatus(story.getStatus());
//        storiesWebModel.setUserId(story.getUser().getUserId());
//        storiesWebModel.setCreatedOn(story.getCreatedOn());
//        storiesWebModel.setCreatedBy(story.getCreatedBy());
//
//        List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Stories, story.getId());
//        if (!Utility.isNullOrEmptyList(fileOutputWebModelList)) {
//            storiesWebModel.setFileOutputWebModel(fileOutputWebModelList);
//        }
//
//        return storiesWebModel;
//    }

    @Override
    public List<StoriesWebModel> getStoryByUserId(Integer userId) {
        List<StoriesWebModel> storiesWebModelList = new ArrayList<>();
        try {
            List<Story> storyList = storyRepository.getAllActiveStories(); // Fetch all stories
            if (!Utility.isNullOrEmptyList(storyList)) {
                // Get the current time and the time 24 hours ago
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime twentyFourHoursAgo = now.minusHours(24);

                // Map to aggregate stories by userId
                Map<Integer, StoriesWebModel> userStoriesMap = new LinkedHashMap<>();

                for (Story story : storyList) {
                    Integer storyUserId = story.getUser().getUserId();

                    // Check if the story was created within the last 24 hours
                    LocalDateTime storyCreatedOn = convertToLocalDateTimeViaInstant(story.getCreatedOn());
                    if (storyCreatedOn.isAfter(twentyFourHoursAgo)) {
                        StoriesWebModel storiesWebModel = transformData(story);
                        List<FileOutputWebModel> mediaFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Stories, story.getId());

                        // Add or update the story in the map for the user
                        if (!userStoriesMap.containsKey(storyUserId)) {
                            storiesWebModel.setFileOutputWebModel(mediaFiles);
                            userStoriesMap.put(storyUserId, storiesWebModel);
                        } else {
                            // Add media files to the existing story
                            StoriesWebModel existingStoriesWebModel = userStoriesMap.get(storyUserId);
                            existingStoriesWebModel.getFileOutputWebModel().addAll(mediaFiles);
                        }
                    }
                }


                // Convert the map values to a list and sort to put the specified userId first
                storiesWebModelList = userStoriesMap.values().stream()
                    .sorted((s1, s2) -> {
                        if (s1.getUserId().equals(userId)) return -1; // Place specified userId stories first
                        if (s2.getUserId().equals(userId)) return 1;  // Place specified userId stories first
                        return 0; // No change in order for other users
                    })
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error at getStoryByUserId() -> {}", e.getMessage());
        }
        return storiesWebModelList;
    }


    private LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private StoriesWebModel transformData(Story story) {
        StoriesWebModel storiesWebModel = new StoriesWebModel();

        storiesWebModel.setStoryId(story.getStoryId());
        storiesWebModel.setProfileUrl(userService.getProfilePicUrl(story.getUser().getUserId()));
        storiesWebModel.setUserName(story.getUser().getName());
        storiesWebModel.setDescription(story.getDescription());
        storiesWebModel.setViewCount(story.getViewCount());
        storiesWebModel.setStatus(story.getStatus());
        storiesWebModel.setUserId(story.getUser().getUserId());
        storiesWebModel.setCreatedOn(story.getCreatedOn());
        storiesWebModel.setCreatedBy(story.getCreatedBy());
        // Fetch user professions
        Set<String> professionNames = new HashSet<>();
        List<FilmProfessionPermanentDetail> professionPermanentDataList = professionPermanentDetailsRepository
            .getProfessionDataByUserId(story.getUser().getUserId());

        if (!Utility.isNullOrEmptyList(professionPermanentDataList)) {
            professionNames = professionPermanentDataList.stream()
                .map(FilmProfessionPermanentDetail::getProfessionName)
                .collect(Collectors.toSet());
        } else {
            professionNames.add("Public User");
        }

        // Add profession names to the StoriesWebModel
        storiesWebModel.setProfessionNames(professionNames);
        

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
            logger.error("Error at getStoryFile() -> {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public void deleteStory(Story storyToUpdate) {
        try {
            storyToUpdate.setStatus(false);
            storyRepository.saveAndFlush(storyToUpdate);
        } catch (Exception e) {
            logger.error("Exception at deleteStory() -> {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Story deleteStoryById(Integer id) {
        Optional<Story> story = storyRepository.findById(id);
        if (story.isPresent()) {
            Story storyToUpdate = story.get();
            this.deleteStory(storyToUpdate); // Deactivating the Story table Records
            List<Integer> storyIdsList = Collections.singletonList(storyToUpdate.getId());
            mediaFilesService.deleteMediaFilesByCategoryAndRefIds(MediaFileCategory.Stories, storyIdsList); // Deactivating the MediaFiles table Records and S3 as well
            return storyToUpdate;
        } else {
            return null;
        }
    }

    @Override
    public List<Story> deleteStoryByUserId(Integer userId) {
        List<Story> storyList = new ArrayList<>();
        try {
            storyList = storyRepository.getStoryByUserId(userId);
            if (!Utility.isNullOrEmptyList(storyList)) {
                storyList.forEach(this::deleteStory); // Deactivating the Story table Records
                List<Integer> storyIdsList = storyList.stream().map(Story::getId).collect(Collectors.toList());
                mediaFilesService.deleteMediaFilesByUserIdAndCategoryAndRefIds(userId, MediaFileCategory.Stories, storyIdsList); // Deactivating the MediaFiles table Records and S3 as well
            }
        } catch (Exception e) {
            logger.error("Error at deleteStoryByUserId() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return storyList;
    }

    @Override
    public Optional<Story> updateStoryView(Integer userId, String storyId) {
        Optional<Story> story = storyRepository.getStoryByUserIdAndStoryId(userId, storyId);
        if (story.isPresent()) {
            story.get().setViewCount(story.get().getViewCount() + 1);
            storyRepository.saveAndFlush(story.get());
        }
        return story;
    }

    @Override
    public List<Story> getMoreThanOneDayStories() {
        return storyRepository.getMoreThanOneDayStories();
    }

    @Override
    public void deleteExpiredStories(List<Story> activeStories) {
        logger.info("Active Stories size :- {}", activeStories.size());
        if (!activeStories.isEmpty()) {
            activeStories.forEach(this::deleteStory); // Deactivating the Story table Records
            List<Integer> storyIdList = activeStories.stream().filter(Objects::nonNull).map(Story::getId).collect(Collectors.toList());
            mediaFilesService.deleteMediaFilesByCategoryAndRefIds(MediaFileCategory.Stories, storyIdList); // Deactivating the MediaFiles table Records and S3 as well
        }
    }

    //    @Override
//    public List<UserIdAndNameWebModel> getUserIdAndName(Integer loginUserId) {
//        List<Story> storyList = storyRepository.findAll(); // Fetch all stories
//        
//        // Filter out the login user's details
//        Map<Integer, String> userIdToNameMap = storyList.stream()
//                .filter(story -> !story.getUser().getUserId().equals(loginUserId))
//                .collect(Collectors.toMap(story -> story.getUser().getUserId(), 
//                                          story -> story.getUser().getName(),
//                                          (existing, replacement) -> existing)); // Keep existing name in case of duplicates
//
//        List<UserIdAndNameWebModel> userIdAndNames = userIdToNameMap.entrySet().stream()
//                .map(entry -> {
//                    Integer userId = entry.getKey();
//                    String userName = entry.getValue();
//                    String profilePicUrl = getProfilePicUrl(userId); // Get profile picture URL
//                    return new UserIdAndNameWebModel(userId, userName, profilePicUrl);
//                })
//                .collect(Collectors.toList());
//
//        return userIdAndNames;
//    }
    @Override
    public List<UserIdAndNameWebModel> getUserIdAndName(Integer loginUserId) {
        List<Story> storyList = storyRepository.findAll(); // Fetch all stories

        // Get the current time
        LocalDateTime currentTime = LocalDateTime.now();

        // Filter stories to include only those created within the last 24 hours
        Map<Integer, String> userIdToNameMap = storyList.stream()
                .filter(story -> {
                    LocalDateTime storyCreatedTime = convertToLocalDateTimeViaInstant(story.getCreatedOn());
                    return Duration.between(storyCreatedTime, currentTime).toHours() <= 24;
                }) // Include only stories created within the last 24 hours
                .collect(Collectors.toMap(
                        story -> story.getUser().getUserId(),
                        story -> story.getUser().getName(),
                        (existing, replacement) -> existing // Keep existing name in case of duplicates
                ));

        List<UserIdAndNameWebModel> userIdAndNames = new ArrayList<>();

        // Add the login user's details first if they have a story within the last 24 hours
        if (userIdToNameMap.containsKey(loginUserId)) {
            UserIdAndNameWebModel loginUserDetails = createUserIdAndNameWebModel(loginUserId, userIdToNameMap.get(loginUserId));
            userIdAndNames.add(loginUserDetails);
            userIdToNameMap.remove(loginUserId); // Remove the login user's entry to avoid duplication
        }

        // Add other user details
        userIdAndNames.addAll(userIdToNameMap.entrySet().stream()
                .map(entry -> createUserIdAndNameWebModel(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));

        return userIdAndNames;
    }

    private UserIdAndNameWebModel createUserIdAndNameWebModel(Integer userId, String userName) {
        String profilePicUrl = userService.getProfilePicUrl(userId); // Get profile picture URL
        List<String> professionNames = getProfessionNames(userId); // Get profession names
        return new UserIdAndNameWebModel(userId, userName, profilePicUrl, professionNames);
    }

    private List<String> getProfessionNames(Integer userId) {
        List<FilmProfessionPermanentDetail> professions = professionPermanentDetailsRepository.getProfessionDataByUserId(userId);
        return professions.stream()
                .map(FilmProfessionPermanentDetail::getProfessionName)
                .collect(Collectors.toList());
    }

    @Override
    public List<StoriesWebModel> getUserStoriesByUserId(Integer userId) {
        List<StoriesWebModel> storiesWebModelList = new ArrayList<>();
        try {
            List<Story> storyList = storyRepository.getAllActiveStories(); // Fetch all stories
            if (!Utility.isNullOrEmptyList(storyList)) {
                // Get the current time and the time 24 hours ago
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime twentyFourHoursAgo = now.minusHours(24);

                // Map to store aggregated media files for the specific userId
                Map<Integer, StoriesWebModel> userStoriesMap = new LinkedHashMap<>();

                for (Story story : storyList) {
                    Integer storyUserId = story.getUser().getUserId();

                    // Check if the story is for the specified userId and created within the last 24 hours
                    if (storyUserId.equals(userId)) {
                        LocalDateTime storyCreatedOn = convertToLocalDateTimeViaInstant(story.getCreatedOn());
                        if (storyCreatedOn.isAfter(twentyFourHoursAgo)) {
                            // Transform the story data
                            StoriesWebModel storiesWebModel = transformData(story);
                            
                            // Retrieve media files for the current story
                            List<FileOutputWebModel> mediaFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Stories, story.getId());

                            // Add or update the media files in the StoriesWebModel
                            if (userStoriesMap.containsKey(storyUserId)) {
                                StoriesWebModel existingStoriesWebModel = userStoriesMap.get(storyUserId);
                                existingStoriesWebModel.getFileOutputWebModel().addAll(mediaFiles);
                            } else {
                                storiesWebModel.setFileOutputWebModel(mediaFiles);
                                userStoriesMap.put(storyUserId, storiesWebModel);
                            }
                        }
                    }
                }

                // Convert the map values to a list
                storiesWebModelList = new ArrayList<>(userStoriesMap.values());
            }
        } catch (Exception e) {
            logger.error("Error at getUserStoriesByUserId() -> {}", e.getMessage());
        }
        return storiesWebModelList;
    }


}


