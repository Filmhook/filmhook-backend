package com.annular.filmhook.service.impl;

import com.annular.filmhook.Response;
import com.annular.filmhook.controller.StoriesController;
import com.annular.filmhook.model.FilmProfessionPermanentDetail;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.Story;
import com.annular.filmhook.model.StoryView;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.FilmProfessionPermanentDetailRepository;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.StoryRepository;
import com.annular.filmhook.repository.StoryViewRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.StoriesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.StoriesWebModel;
import com.annular.filmhook.webmodel.StoryViewerDTO;
import com.annular.filmhook.webmodel.UserIdAndNameWebModel;

import com.annular.filmhook.webmodel.UserWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.CrudRepository;
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
	StoryViewRepository storyViewRepository;

	@Autowired
	StoryRepository storyRepository;

	@Autowired
	MediaFilesService mediaFilesService;
	@Autowired
	MediaFilesRepository mediaFilesRepository;
	@Autowired
	UserService userService;

	@Autowired
	FilmProfessionPermanentDetailRepository professionPermanentDetailsRepository;

	@Autowired
	UserRepository userRepository;


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

	//    @Override
	//    public Response uploadStory(StoriesWebModel inputData) {
	//        try {
	//            Optional<User> userFromDB = userService.getUser(inputData.getUserId());
	//            if (userFromDB.isPresent()) {
	//                // Prepare the story object from input data
	//                Story story = this.prepareStories(inputData, userFromDB.get());
	//
	//                // 1. Save the story in the Stories table (MySQL)
	//                storyRepository.saveAndFlush(story);
	//                logger.info("Story unique id saved in MySQL: {}", story.getStoryId());
	//
	//                // 2. Save media files in the media_files table (MySQL)
	//                inputData.getFileInputWebModel().setCategory(MediaFileCategory.Stories);
	//                inputData.getFileInputWebModel().setCategoryRefId(story.getId()); // Add story table reference in media files table
	//                List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.saveMediaFiles(inputData.getFileInputWebModel(), userFromDB.get());
	//
	//                // Fetch user professions
	//                Set<String> professionNames = new HashSet<>();
	//                List<FilmProfessionPermanentDetail> professionPermanentDataList = professionPermanentDetailsRepository
	//                    .getProfessionDataByUserId(story.getUser().getUserId());
	//
	//                if (!Utility.isNullOrEmptyList(professionPermanentDataList)) {
	//                    professionNames = professionPermanentDataList.stream()
	//                        .map(FilmProfessionPermanentDetail::getProfessionName)
	//                        .collect(Collectors.toSet());
	//                } else {
	//                    professionNames.add("Public User");
	//                }
	//
	//                
	//                // Prepare stories data
	//                List<Map<String, Object>> storiesList = new ArrayList<>();
	//                for (FileOutputWebModel fileOutput : fileOutputWebModelList) {
	//                    Map<String, Object> storyMap = new HashMap<>();
	//                    storyMap.put("duration", null);  // No duration in image
	//                    storyMap.put("storyId", story.getStoryId());
	//                    storyMap.put("showOverlay", true);  // Assuming this field is true for all
	//                    storyMap.put("link", "https://google.com");  // Example link
	//                    storyMap.put("id", fileOutput.getId());  // Story ID from media files
	//                    storyMap.put("type", "image");  // Assuming image type
	//                    storyMap.put("isSeen", false);  // Initial state
	//                    storyMap.put("url", fileOutput.getFilePath());  // File URL from media
	//
	//                    storiesList.add(storyMap);
	//                }
	//
	//                // Prepare response data
	//                Map<String, Object> responseData = new HashMap<>();
	//                responseData.put("stories", storiesList);
	//                // Optional fields, uncomment if needed
	//                responseData.put("profile", userService.getProfilePicUrl(userFromDB.get().getUserId()));  // Profile URL
	//                responseData.put("id", userFromDB.get().getUserId());  // User ID
	//                responseData.put("title", professionNames);  // Example profession title
	//                responseData.put("username", "JS");  // Example username
	//
	//                return new Response(1, "Stories retrieved successfully...", responseData);
	//            } else {
	//                return new Response(-1, "User not found", null);
	//            }
	//        } catch (Exception e) {
	//            logger.error("Error at uploadStory() -> {}", e.getMessage());
	//            e.printStackTrace();
	//            return new Response(-1, "Error occurred while uploading story...", null);
	//        }
	//    }


	private Story prepareStories(StoriesWebModel inputData, User user) {

		Story story = new Story();
		story.setStoryId(UUID.randomUUID().toString());
		story.setType(inputData.getType());
		if (inputData.getFileInputWebModel() != null && inputData.getFileInputWebModel().getDescription() != null) {
			story.setDescription(inputData.getFileInputWebModel().getDescription());
		} else {
			story.setDescription(""); // fallback if null
		}
		//		story.setViewCount(inputData.getViewCount() == null ? 0 : inputData.getViewCount());   
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
	//////Orginal Code for Stories

	@Override
	public List<StoriesWebModel> getStoryByUserId(Integer userId) {
	    List<StoriesWebModel> storiesWebModelList = new ArrayList<>();

	    try {
	        List<Story> storyList = storyRepository.getAllActiveStories();

	        if (!Utility.isNullOrEmptyList(storyList)) {
	            LocalDateTime now = LocalDateTime.now();
	            LocalDateTime twentyFourHoursAgo = now.minusHours(24);

	            Map<Integer, StoriesWebModel> userStoriesMap = new LinkedHashMap<>();

	            for (Story story : storyList) {
	                LocalDateTime storyCreatedOn = convertToLocalDateTimeViaInstant(story.getCreatedOn());
	                if (storyCreatedOn.isBefore(twentyFourHoursAgo)) continue;

	                Integer storyUserId = story.getUser().getUserId();
	                StoriesWebModel storiesWebModel = userStoriesMap.getOrDefault(storyUserId, transformData(story));
	                List<FileOutputWebModel> mediaFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(
	                        MediaFileCategory.Stories, story.getId());

	                List<FileOutputWebModel> enrichedFiles = storiesWebModel.getFileOutputWebModel() != null
	                        ? storiesWebModel.getFileOutputWebModel()
	                        : new ArrayList<>();

	                for (FileOutputWebModel model : mediaFiles) {
	                    model.setStoryId(story.getStoryId()); // Always set storyId

	                    MediaFiles mediaEntity = mediaFilesRepository.findById(model.getId()).orElse(null);
	                    if (mediaEntity != null) {
	                        model.setDescription(mediaEntity.getDescription()); // ✅ Set description
	                    }

	                    // Fetch view details only for logged-in user's stories
	                   
	                        if (mediaEntity != null) {
	                            // View count
	                            int viewCount = storyViewRepository.countByMediaFile(mediaEntity);
	                            model.setViewCount(viewCount);

	                            // Viewers list
	                            List<StoryView> views = storyViewRepository.findByMediaFile(mediaEntity);

	                            // ✅ Count likes
	                            long likedCount = views.stream()
	                                    .filter(StoryView::getLiked)
	                                    .count();
	                            model.setLikedCount((int) likedCount);  // You should ensure this field exists in FileOutputWebModel
	                            
	                         // ✅ Set isStoryLiked for current (logged-in) user
	                            boolean isLikedByCurrentUser = views.stream()
	                                    .anyMatch(view -> view.getViewer() != null &&
	                                            view.getViewer().getUserId().equals(userId) &&
	                                            Boolean.TRUE.equals(view.getLiked()));

	                            model.setIsStoryLiked(isLikedByCurrentUser);

	                            List<StoryViewerDTO> viewerList = views.stream()
	                                    .map(view -> {
	                                        User viewer = view.getViewer();
	                                        if (viewer == null) return null;

	                                        return StoryViewerDTO.builder()
	                                                .viewerId(viewer.getUserId())
	                                                .viewerName(viewer.getName())
	                                                .userProfilePic(userService.getProfilePicUrl(viewer.getUserId()))
	                                                .viewedOn(view.getViewedOn())
	                                                .viewedAtText(Utility.formatRelativeTime(view.getViewedOn()))
	                                                .liked(view.getLiked())
	                                                .build();
	                                    })
	                                    .filter(Objects::nonNull)
	                                    .collect(Collectors.toList());

	                            model.setViewedBy(viewerList);
	                        }
	                    

	                    enrichedFiles.add(model);
	                }

	                storiesWebModel.setFileOutputWebModel(enrichedFiles);
	                userStoriesMap.put(storyUserId, storiesWebModel);
	            }

	            // Sort: User's own story first
	            storiesWebModelList = userStoriesMap.values().stream()
	                    .sorted((s1, s2) -> {
	                        if (s1.getUserId().equals(userId)) return -1;
	                        if (s2.getUserId().equals(userId)) return 1;
	                        return 0;
	                    })
	                    .collect(Collectors.toList());
	        }
	    } catch (Exception e) {
	        logger.error("Error at getStoryByUserId() -> {}", e.getMessage(), e);
	    }

	    return storiesWebModelList;
	}




	public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
		return Instant.ofEpochMilli(dateToConvert.getTime())
				.atZone(ZoneId.systemDefault())  
				.toLocalDateTime();
	}

	private StoriesWebModel transformData(Story story) {
		StoriesWebModel storiesWebModel = new StoriesWebModel();

		//		storiesWebModel.setStoryId(story.getStoryId());
		storiesWebModel.setProfileUrl(userService.getProfilePicUrl(story.getUser().getUserId()));
		storiesWebModel.setUserName(story.getUser().getName());
		//		storiesWebModel.setDescription(story.getDescription());
		//		storiesWebModel.setViewCount(story.getViewCount());
		storiesWebModel.setStatus(story.getStatus());
		storiesWebModel.setUserId(story.getUser().getUserId());
		storiesWebModel.setCreatedOn(story.getCreatedOn());
		storiesWebModel.setCreatedBy(story.getCreatedBy());
		// Fetch user professions
		Set<String> professionNames = new HashSet<>();
		String userType = story.getUser().getUserType(); 
		if (userType != null && !userType.isEmpty()) {
			professionNames.add(userType);
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
	public Story deleteStoryById(String storyId, Integer userId) {
		Story story = storyRepository.findByStoryId(storyId);

		if (story == null) {
			throw new RuntimeException("Story not found.");
		}

		if (!story.getUser().getUserId().equals(userId)) {
			throw new SecurityException("You are not authorized to delete this story.");
		}

		// Deactivate the story
		this.deleteStory(story);

		// Deactivate related media files
		List<Integer> storyIdsList = Collections.singletonList(story.getId());
		mediaFilesService.deleteMediaFilesByCategoryAndRefIds(MediaFileCategory.Stories, storyIdsList);

		return story;
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
			List<Story> storyList = storyRepository.getAllActiveStories();

			if (!Utility.isNullOrEmptyList(storyList)) {
				LocalDateTime now = LocalDateTime.now();
				LocalDateTime twentyFourHoursAgo = now.minusHours(24);

				// Grouping by userId, not storyId
				Map<Integer, StoriesWebModel> userStoriesMap = new LinkedHashMap<>();

				for (Story story : storyList) {
					Integer storyUserId = story.getUser().getUserId();

					// Filter only for the requested user and stories within 24 hours
					if (storyUserId.equals(userId)) {
						LocalDateTime storyCreatedOn = convertToLocalDateTimeViaInstant(story.getCreatedOn());

						if (storyCreatedOn.isAfter(twentyFourHoursAgo)) {

							// Transform user details only once
							StoriesWebModel storiesWebModel = userStoriesMap.getOrDefault(storyUserId, transformData(story));
							List<FileOutputWebModel> enrichedFiles = storiesWebModel.getFileOutputWebModel() != null
									? storiesWebModel.getFileOutputWebModel()
											: new ArrayList<>();

							// Fetch media files for this story
							List<FileOutputWebModel> mediaFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(
									MediaFileCategory.Stories, story.getId()
									);

							for (FileOutputWebModel model : mediaFiles) {
								Integer mediaFileId = model.getId();
								MediaFiles mediaEntity = mediaFilesRepository.findById(mediaFileId).orElse(null);
								if (mediaEntity == null) continue;

								// ✅ Set view count
								int count = storyViewRepository.countByMediaFile(mediaEntity);
								model.setViewCount(count);

								// ✅ Set viewers
								List<StoryView> views = storyViewRepository.findByMediaFile(mediaEntity);
								List<StoryViewerDTO> viewers = views.stream()
										.map(view -> {
											User viewer = view.getViewer();
											if (viewer == null) return null;
											return StoryViewerDTO.builder()
													.viewerId(viewer.getUserId())
													.viewerName(viewer.getName())
													.userProfilePic(userService.getProfilePicUrl(viewer.getUserId()))
													.viewedOn(view.getViewedOn())
													.build();
										})
										.filter(Objects::nonNull)
										.collect(Collectors.toList());

								model.setViewedBy(viewers);

								// ✅ Set the correct storyId in each media
								model.setStoryId(story.getStoryId());

								enrichedFiles.add(model);
							}

							storiesWebModel.setFileOutputWebModel(enrichedFiles);
							userStoriesMap.put(storyUserId, storiesWebModel);
						}
					}
				}

				storiesWebModelList = new ArrayList<>(userStoriesMap.values());
			}

		} catch (Exception e) {
			logger.error("Error in getUserStoriesByUserId() -> {}", e.getMessage(), e);
		}

		return storiesWebModelList;
	}




	//    public List<Map<String, Object>> getStoryByUserId(Integer userId) {
	//        List<Map<String, Object>> storiesWebModelList = new ArrayList<>();
	//
	//        try {
	//            List<Story> storyList = storyRepository.getAllActiveStories(); // Fetch all stories
	//            if (!Utility.isNullOrEmptyList(storyList)) {
	//                LocalDateTime now = LocalDateTime.now();
	//                LocalDateTime twentyFourHoursAgo = now.minusHours(24);
	//
	//                Map<Integer, Map<String, Object>> userStoriesMap = new LinkedHashMap<>();
	//
	//                for (Story story : storyList) {
	//                    Integer storyUserId = story.getUser().getUserId();
	//
	//                    // Check if the story was created within the last 24 hours
	//                    LocalDateTime storyCreatedOn = convertToLocalDateTimeViaInstant(story.getCreatedOn());
	//                    if (storyCreatedOn.isAfter(twentyFourHoursAgo)) {
	//                        Map<String, Object> storiesWebModel = userStoriesMap.getOrDefault(storyUserId, new HashMap<>());
	//
	//                        // Fetch user professions
	//                        Set<String> professionNames = new HashSet<>();
	//                        List<FilmProfessionPermanentDetail> professionPermanentDataList = professionPermanentDetailsRepository
	//                            .getProfessionDataByUserId(story.getUser().getUserId());
	//
	//                        if (!Utility.isNullOrEmptyList(professionPermanentDataList)) {
	//                            professionNames = professionPermanentDataList.stream()
	//                                .map(FilmProfessionPermanentDetail::getProfessionName)
	//                                .collect(Collectors.toSet());
	//                        } else {
	//                            professionNames.add("Public User");
	//                        }
	//
	//                        // Set user details if not already set
	//                        if (!userStoriesMap.containsKey(storyUserId)) {
	//                            storiesWebModel.put("id", storyUserId); // unique id
	//                            storiesWebModel.put("username", story.getUser().getName()); // username
	//                            storiesWebModel.put("title", professionNames); // title
	//                            storiesWebModel.put("profile", userService.getProfilePicUrl(storyUserId)); // profile picture URL
	//
	//                            userStoriesMap.put(storyUserId, storiesWebModel);
	//                        }
	//
	//                        // Fetch media files
	//                        List<FileOutputWebModel> mediaFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Stories, story.getId());
	//
	//                        // Initialize or get the existing stories list
	//                        List<Map<String, Object>> stories = (List<Map<String, Object>>) storiesWebModel.getOrDefault("stories", new ArrayList<>());
	//
	//                        if (!mediaFiles.isEmpty()) {
	//                            // Iterate through the media files to set story details
	//                            for (FileOutputWebModel mediaFile : mediaFiles) {
	//                                Map<String, Object> storyDetail = new HashMap<>();
	//                                storyDetail.put("id", mediaFile.getId()); // Unique id
	//                                storyDetail.put("url", mediaFile.getFilePath()); // Story URL
	//                                storyDetail.put("type", mediaFile.getType()); // Story type
	//                                storyDetail.put("duration", mediaFile.getDuration()); // Duration
	//                                storyDetail.put("storyId", story.getId()); // Story ID
	//                                storyDetail.put("isSeen", false); // Example for isSeen
	//                                storyDetail.put("showOverlay", true); // Example for showOverlay
	//                                storyDetail.put("link", "https://google.com"); // Example for link
	//
	//                                stories.add(storyDetail);
	//                            }
	//                        }
	//
	//                        storiesWebModel.put("stories", stories); // Add the story details to the map
	//                    }
	//                }
	//
	//                // Convert the map values to a list and sort to put the specified userId first
	//                storiesWebModelList = userStoriesMap.values().stream()
	//                    .sorted((s1, s2) -> {
	//                        if (s1.get("id").equals(userId)) return -1; // Place specified userId stories first
	//                        if (s2.get("id").equals(userId)) return 1;  // Place specified userId stories first
	//                        return 0; // No change in order for other users
	//                    })
	//                    .collect(Collectors.toList());
	//            }
	//        } catch (Exception e) {
	//            logger.error("Error at getStoryByUserId() -> {}", e.getMessage());
	//        }
	//        return storiesWebModelList;
	//    }

	//	@Override
	//	public StoriesWebModel getStoryWithViews(String storyId, Integer viewerId) {
	//	    Story story = storyRepository.findByStoryId(storyId);
	//	    if (story == null) return null;
	//
	//	    User viewer = userRepository.findById(viewerId).orElse(null);
	//	    if (viewer == null) return null;
	//
	//	    // Fetch all media files under this story
	//	    List<MediaFiles> mediaFilesList = mediaFilesService.getMediaFilesByCategoryAndRefId(
	//	            MediaFileCategory.Stories, story.getId());
	//
	//	    List<FileOutputWebModel> mediaModels = new ArrayList<>();
	//
	//	    for (MediaFiles mediaFile : mediaFilesList) {
	//	        // Check and save view only if not already viewed
	//	        if (!storyViewRepository.existsByMediaFileAndViewer(mediaFile, viewer)) {
	//	            StoryView view = StoryView.builder()
	//	                    .story(story)
	//	                    .mediaFile(mediaFile)
	//	                    .viewer(viewer)
	//	                    .build();
	//	            storyViewRepository.save(view);
	//
	//	            // Increment media view count manually if you're tracking it per media
	//	            mediaFile.setViews((mediaFile.getViews() == null ? 0 : mediaFile.getViews() + 1));
	//	            mediaFilesService.save(mediaFile); // Ensure your MediaFilesService supports this
	//	        }
	//
	//	        // Add to response model
	//	        FileOutputWebModel mediaModel = mediaFilesService.convertToWebModel(mediaFile);
	//
	//	        // Add viewers (if current user is owner)
	//	        if (story.getUser().getUserId().equals(viewerId)) {
	//	            List<StoryView> mediaViews = storyViewRepository.findByMediaFile(mediaFile);
	//	            List<StoryViewerDTO> viewerDTOs = mediaViews.stream().map(v -> {
	//	                User u = v.getViewer();
	//	                return StoryViewerDTO.builder()
	//	                        .viewerId(u.getUserId())
	//	                        .viewerName(u.getName())
	//	                        .userProfilePic(userService.getProfilePicUrl(u.getUserId()))
	//	                        .viewedOn(v.getViewedOn())
	//	                        .build();
	//	            }).collect(Collectors.toList());
	//
	//	            mediaModel.setViewers(viewerDTOs);
	//	            mediaModel.setViewCount(viewerDTOs.size());
	//	        }
	//
	//	        mediaModels.add(mediaModel);
	//	    }
	//
	//	    return StoriesWebModel.builder()
	//	            .storyId(story.getStoryId())
	//	            .userId(story.getUser().getUserId())
	//	            .userName(story.getUser().getName())
	//	            .profileUrl(userService.getProfilePicUrl(story.getUser().getUserId()))
	//	            .description(story.getDescription())
	//	            .type(story.getType())
	//	            .viewCount(null) // Optional: remove story-level count if you're switching to per-media
	//	            .fileOutputWebModel(mediaModels)
	//	            .createdOn(story.getCreatedOn())
	//	            .build();
	//	}

	@Override
	public void saveMediaView(String storyId, Integer mediaFileId, Integer viewerId, Boolean liked) {
		Story story = storyRepository.findByStoryId(storyId);
		MediaFiles mediaFile = mediaFilesRepository.findById(mediaFileId).orElse(null);
		User viewer = userRepository.findById(viewerId).orElse(null);

		if (story == null || mediaFile == null || viewer == null) return;

		// Don't save if viewer is the owner
		if (story.getUser().getUserId().equals(viewerId)) return;

		// Avoid duplicate views
		Optional<StoryView> optionalView = storyViewRepository.findByMediaFileAndViewer(mediaFile, viewer);

		if (optionalView.isEmpty()) {
			StoryView view = StoryView.builder()
					.story(story)
					.mediaFile(mediaFile)
					.viewer(viewer)
					.liked(Boolean.TRUE.equals(liked)) 
					.build();
			storyViewRepository.save(view);

			// Update story view count
			if (story.getViewCount() == null) {
				story.setViewCount(1);
			} else {
				story.setViewCount(story.getViewCount() + 1);
			}
			storyRepository.save(story);
			logger.info("✅ View saved for storyId: {}, by viewerId: {}", storyId, viewerId);
		}
		else {
			// Already viewed: only update like if liked = true (not for false/unliked)
			  StoryView existingView = optionalView.get();
		        if (existingView.getLiked() != Boolean.TRUE.equals(liked)) {
		            existingView.setLiked(Boolean.TRUE.equals(liked));
		            storyViewRepository.save(existingView);
		            logger.info("🔄 Like status updated for storyId: {}, viewerId: {}, liked: {}", storyId, viewerId, liked);
		        
			}
		}
		
	}


}


