package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.UserWebModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.FilmProfessionPermanentDetail;
import com.annular.filmhook.model.FollowersRequest;
import com.annular.filmhook.model.Likes;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserMediaPin;
import com.annular.filmhook.model.UserProfilePin;
import com.annular.filmhook.repository.CommentRepository;
import com.annular.filmhook.repository.FilmProfessionPermanentDetailRepository;
import com.annular.filmhook.repository.FriendRequestRepository;
import com.annular.filmhook.repository.LikeRepository;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.PinMediaRepository;
import com.annular.filmhook.repository.PinProfileRepository;
import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.ShareRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.PinProfileService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.UserProfilePinWebModel;

@Service
public class PinProfileServiceImpl implements PinProfileService {

	@Autowired
	UserDetails userDetails;

	@Autowired
	PinProfileRepository pinProfileRepository;
	
	@Autowired
	PostsRepository postsRepository;
	
	@Autowired
	MediaFilesService mediaFilesService;

	@Autowired
	PinMediaRepository pinMediaRepository;

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ShareRepository shareRepository;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	FileUtil fileUtil;
	
	@Autowired
	FriendRequestRepository friendRequestRepository;
	
	@Autowired
	FilmProfessionPermanentDetailRepository filmProfessionPermanentDetailRepository;

	@Autowired
	LikeRepository likeRepository;

	@Autowired
	S3Util s3Util;

	@Autowired
	MediaFilesRepository mediaFilesRepository;

	private static final Logger logger = LoggerFactory.getLogger(PinProfileServiceImpl.class);

//	@Override
//	public ResponseEntity<?> addProfile(UserProfilePinWebModel userProfilePinWebModel) {
//	    try {
//	        Integer userId = userDetails.userInfo().getId();
//	        Integer pinProfileId = userProfilePinWebModel.getPinProfileId();
//
//	        // Check if the pin already exists for the user
//	        Optional<UserProfilePin> existingPinOptional = pinProfileRepository.findByUserIdAndPinProfileId(userId, pinProfileId);
//
//	        UserProfilePin userProfilePin;
//	        if (existingPinOptional.isPresent()) {
//	            // Update the existing pin
//	            userProfilePin = existingPinOptional.get();
//	            userProfilePin.setUpdatedBy(userId);
//	            //userProfilePin.setUpdatedOn(new Date()); // Assuming you have an updatedOn field in UserProfilePin
//	        } else {
//	            // Create a new pin
//	            userProfilePin = new UserProfilePin();
//	            userProfilePin.setUserId(userId);
//	            userProfilePin.setPinProfileId(pinProfileId);
//	            userProfilePin.setCreatedBy(userId);
//	            userProfilePin.setStatus(true);
//	        }
//
//	        // Save or update the pin
//	        pinProfileRepository.save(userProfilePin);
//
//	        return ResponseEntity.ok("Pin added or updated successfully");
//	    } catch (Exception e) {
//	        logger.error("Error setting addPin: {}", e.getMessage());
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body(new Response(-1, "Error setting addPin", e.getMessage()));
//	    }
//	}

	@Override
	public ResponseEntity<?> addProfile(UserProfilePinWebModel userProfilePinWebModel) {
	    try {
	        Integer userId = userDetails.userInfo().getId();
	        Integer pinProfileId = userProfilePinWebModel.getPinProfileId();

	        // Check if the pin already exists for the user
	        Optional<UserProfilePin> existingPinOptional = pinProfileRepository.findByUserIdAndPinProfileId(userId, pinProfileId);

	        UserProfilePin userProfilePin;
	        if (existingPinOptional.isPresent()) {
	            // Update the existing pin
	            userProfilePin = existingPinOptional.get();
	            userProfilePin.setUpdatedBy(userId);
	            
	            // Log the existing status before updating
	            logger.info("Existing pin status before update: {}", userProfilePin.isStatus());

	            // Toggle the status
	            userProfilePin.setStatus(!userProfilePin.isStatus());

	            // Log the updated status
	            logger.info("Updated pin status: {}", userProfilePin.isStatus());
	        } else {
	            // Create a new pin
	            userProfilePin = new UserProfilePin();
	            userProfilePin.setUserId(userId);
	            userProfilePin.setPinProfileId(pinProfileId);
	            userProfilePin.setCreatedBy(userId);
	            userProfilePin.setStatus(true);
	        }

	        // Save or update the pin
	        pinProfileRepository.save(userProfilePin);

	        return ResponseEntity.ok("Pin added or updated successfully");
	    } catch (Exception e) {
	        logger.error("Error setting addPin: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new Response(-1, "Error setting addPin", e.getMessage()));
	    }
	}


	@Override
	public ResponseEntity<?> addMedia(UserProfilePinWebModel userProfilePinWebModel) {
	    try {
	        Integer userId = userDetails.userInfo().getId();
	        Integer pinMediaId = userProfilePinWebModel.getPinMediaId();

	        // Check if the pin already exists for the user
	        Optional<UserMediaPin> existingPinOptional = pinMediaRepository.findByUserIdAndPinMediaId(userId, pinMediaId);

	        UserMediaPin userMediaPin;
	        if (existingPinOptional.isPresent()) {
	            // Update the existing pin
	            userMediaPin = existingPinOptional.get();
	            userMediaPin.setUpdatedBy(userId);
	            // Update other fields if necessary
	            // userMediaPin.setUpdatedOn(new Date()); // Uncomment if you have an updatedOn field
	            userMediaPin.setStatus(! userMediaPin.isStatus());
	        } else {
	            // Create a new pin
	            userMediaPin = new UserMediaPin();
	            userMediaPin.setUserId(userId);
	            userMediaPin.setPinMediaId(pinMediaId);
	            userMediaPin.setCreatedBy(userId);
	            userMediaPin.setStatus(true);
	        }

	        // Save or update the pin
	        pinMediaRepository.save(userMediaPin);

	        return ResponseEntity.ok("Pin added or updated successfully");
	    } catch (Exception e) {
	        logger.error("Error setting addPin: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new Response(-1, "Error setting addPin", e.getMessage()));
	    }
	}


	public ResponseEntity<?> getAllProfilePin() {
	    try {
	        logger.info("getAllProfilePin service start");

	        List<UserProfilePin> userProfilePins = pinProfileRepository.findByUserId(userDetails.userInfo().getId());
	        List<LinkedHashMap<String, Object>> responseList = new ArrayList<>();
	        for (UserProfilePin userProfilePin : userProfilePins) {
	            Integer pinProfileId = userProfilePin.getPinProfileId();
	            if (pinProfileId != null) {
	                Optional<User> userOptional = userRepository.findById(pinProfileId);

	                if (userOptional.isPresent()) {
	                    User user = userOptional.get();
	                    
	                    // Retrieve profile picture from mediaFiles table based on user ID
	                    List<MediaFiles> profilePicOptional = mediaFilesRepository.getMediaFilesByUserIdAndCategory(userProfilePin.getUserId(), MediaFileCategory.ProfilePic);
	                    
	                    LinkedHashMap<String, Object> pinData = new LinkedHashMap<>();
	                    pinData.put("pinProfileId", userProfilePin.getPinProfileId());
	                    pinData.put("userId", userProfilePin.getUserId());
	                    pinData.put("userName", user.getName());
	                    pinData.put("userGender", user.getGender());
	                    
	                    if (!profilePicOptional.isEmpty()) {
	                        MediaFiles mediaFiles = profilePicOptional.get(0);
	                        pinData.put("filePathProfile", mediaFiles.getFilePath());
	                        pinData.put("fileNameProfile", mediaFiles.getFileName());
	                        pinData.put("fileNameSize", mediaFiles.getFileSize());
	                        pinData.put("fileNameTypeProfile", mediaFiles.getFileType());
	                        pinData.put("profilePicUrl", s3Util.generateS3FilePath(mediaFiles.getFilePath() + mediaFiles.getFileType()));
	                    } else {
	                        // Handle case where profile picture is not found
	                        pinData.put("profilePicUrl", null);
	                    }
	                    
	                    responseList.add(pinData);
	                } else {
	                    logger.warn("User not found for pinProfileId: " + pinProfileId);
	                }
	            } else {
	                logger.warn("pinProfileId is null for userProfilePin: " + userProfilePin);
	            }
	        }
	        return ResponseEntity.ok(responseList);
	    } catch (Exception e) {
	        logger.error("getAllProfilePin service Method Exception {} ", e);
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Fail", ""));
	    }
	}

	@Override
	public ResponseEntity<?> getAllMediaPin() {
	    try {
	        logger.info("getAllMediaPin service start");

	        Integer userId = userDetails.userInfo().getId();
	        List<UserMediaPin> userMediaPins = pinMediaRepository.findByUserId(userId);

	        List<Map<String, Object>> combinedDetailsList = new ArrayList<>();

	        for (UserMediaPin userMediaPin : userMediaPins) {
	            Optional<Posts> postOptional = postsRepository.findById(userMediaPin.getPinMediaId());
	            if (!postOptional.isPresent()) {
	                continue;
	            }
	            Posts post = postOptional.get();

	            // Fetching post-files
	            List<FileOutputWebModel> postFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Post, post.getId());

	            // Fetching the user Profession
	            Set<String> professionNames = filmProfessionPermanentDetailRepository.findByUserId(post.getUser().getUserId())
	                    .stream()
	                    .map(FilmProfessionPermanentDetail::getProfessionName)
	                    .collect(Collectors.toSet());

                List<FileOutputWebModel> userProfilePic = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.ProfilePic, post.getUser().getUserId());
                String profilePicturePath = null;
                if (!userProfilePic.isEmpty()) {
                    FileOutputWebModel profilePic = userProfilePic.get(0);
                    profilePicturePath = profilePic.getFilePath();
                }

	            // Fetching the followers count for the user
	            //long followersCount = friendRequestRepository.countByFollowersRequestReceiverIdAndFollowersRequestIsActive(post.getUser().getUserId(), true);
	            List<FollowersRequest> followersList = friendRequestRepository.findByFollowersRequestReceiverIdAndFollowersRequestIsActive(post.getUser().getUserId(), true);

	            // Fetching the likes details
	            boolean likeStatus = likeRepository.findByPostIdAndUserId(post.getId(), userId)
	                    .map(Likes::getStatus)
	                    .orElse(false);

	            // Fetching the pin status
	            boolean pinStatus = pinProfileRepository.findByPinProfileIdAndUserId(post.getUser().getUserId(), userId)
	                    .map(UserProfilePin::isStatus)
	                    .orElse(false);

	            // Preparing PostWebModel
	            PostWebModel postWebModel = PostWebModel.builder()
	                    .id(post.getId())
	                    .userId(post.getUser().getUserId())
	                    .userName(post.getUser().getName())
	                    .postId(post.getPostId())
	                    .userProfilePic(profilePicturePath)
	                    .description(post.getDescription())
	                    .pinStatus(pinStatus)
	                    .likeCount(post.getLikesCollection() != null ? post.getLikesCollection().size() : 0)
	                    .shareCount(post.getShareCollection() != null ? post.getShareCollection().size() : 0)
	                    .commentCount(post.getCommentCollection() != null ? post.getCommentCollection().size() : 0)
	                    .promoteFlag(post.getPromoteFlag())
	                    .postFiles(postFiles)
	                    .likeStatus(likeStatus)
	                    .privateOrPublic(post.getPrivateOrPublic())
	                    .locationName(post.getLocationName())
	                    .professionNames(professionNames)
	                    .followersCount(followersList.size())
	                    .build();

	            // Adding PostWebModel to the combined details list
	            Map<String, Object> combinedDetails = new HashMap<>();
	            combinedDetails.put("postWebModel", postWebModel);

//	            FileOutputWebModel profilePic = userService.getProfilePic(UserWebModel.builder().userId(post.getUser().getUserId()).build());
//	            combinedDetails.put("profilePicUrl", profilePic != null ? s3Util.generateS3FilePath(profilePic.getFilePath() + profilePic.getFileType()) : null);

//	            List<String> postFileUrls = postFiles.stream()
//	                    .map(file -> s3Util.generateS3FilePath(file.getFilePath() + file.getFileType()))
//	                    .collect(Collectors.toList());
//	            combinedDetails.put("postFiles", postFileUrls);

	            combinedDetailsList.add(combinedDetails);
	        }

	        Map<String, Object> responseMap = new HashMap<>();
	        responseMap.put("combinedDetailsList", combinedDetailsList);

	        return ResponseEntity.ok(responseMap);
	    } catch (Exception e) {
	        logger.error("Error in getAllMediaPin: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Fail", ""));
	    }
	}

	

	@Override
	public ResponseEntity<?> getByProfileId(UserProfilePinWebModel userProfilePinWebModel) {
		try {
			logger.info("getByProfileId service start");

			Integer profileId = userProfilePinWebModel.getPinProfileId();
			if (profileId != null) {
				Optional<UserProfilePin> userProfilePinOptional = pinProfileRepository.findById(profileId);

				if (userProfilePinOptional.isPresent()) {
					UserProfilePin userProfilePin = userProfilePinOptional.get();
					Optional<User> userOptional = userRepository.findById(userProfilePin.getPinProfileId());

					if (userOptional.isPresent()) {
						User user = userOptional.get();
						LinkedHashMap<String, Object> responseData = new LinkedHashMap<>();
						responseData.put("pinProfileId", userProfilePin.getPinProfileId());
						// responseData.put("userId", userProfilePin.getUserId());
						responseData.put("userName", user.getName());
						responseData.put("userGender", user.getGender());
						// Add other details as needed
						return ResponseEntity.ok(responseData);
					} else {
						logger.warn("User not found for pinProfileId: " + userProfilePin.getPinProfileId());
					}
				} else {
					logger.warn("UserProfilePin not found for profileId: " + profileId);
				}
			} else {
				logger.warn("profileId is null in request");
			}

			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			logger.error("getByProfileId service Method Exception {} ", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Fail", ""));
		}

	}

	@Override
	public ResponseEntity<?> profilePinStatus(UserProfilePinWebModel userProfilePinWebModel) {
		try {
			logger.info("changePinStatus service start");

			Optional<UserProfilePin> pinOptional = pinProfileRepository
					.findById(userProfilePinWebModel.getUserProfilePinId());
			if (pinOptional.isPresent()) {
				UserProfilePin userProfilePin = pinOptional.get();
				userProfilePin.setStatus(userProfilePinWebModel.isStatus());

				pinProfileRepository.save(userProfilePin);

				return ResponseEntity.ok("Pin status changed successfully");
			} else {

				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error("changePinStatus service Method Exception {} ", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Fail", ""));
		}
	}

	@Override
	public ResponseEntity<?> mediaPinStatus(UserProfilePinWebModel userProfilePinWebModel) {
		try {
			logger.info("changePinStatus service start");

			Optional<UserMediaPin> pinOptional = pinMediaRepository
					.findById(userProfilePinWebModel.getUserMediaPinId());
			if (pinOptional.isPresent()) {
				UserMediaPin userProfilePin = pinOptional.get();
				userProfilePin.setStatus(userProfilePinWebModel.isStatus());

				pinMediaRepository.save(userProfilePin);

				return ResponseEntity.ok("Pin status changed successfully");
			} else {

				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error("changePinStatus service Method Exception {} ", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Fail", ""));
		}
	}
}
