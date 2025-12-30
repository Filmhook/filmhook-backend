package com.annular.filmhook.service.impl;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.PostWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.FilmProfessionPermanentDetail;
import com.annular.filmhook.model.FollowersRequest;
import com.annular.filmhook.model.Likes;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.Promote;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserMediaPin;
import com.annular.filmhook.model.UserProfilePin;
import com.annular.filmhook.model.VisitPage;
import com.annular.filmhook.repository.CommentRepository;
import com.annular.filmhook.repository.FilmProfessionPermanentDetailRepository;
import com.annular.filmhook.repository.FriendRequestRepository;
import com.annular.filmhook.repository.LikeRepository;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.PinMediaRepository;
import com.annular.filmhook.repository.PinProfileRepository;
import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.PromoteRepository;
import com.annular.filmhook.repository.ShareRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.repository.VisitPageRepository;
import com.annular.filmhook.security.UserDetailsImpl;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.PinProfileService;
import com.annular.filmhook.util.CalendarUtil;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.util.Utility;
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
    FriendRequestRepository friendRequestRepository;

    @Autowired
    FilmProfessionPermanentDetailRepository filmProfessionPermanentDetailRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    S3Util s3Util;

    @Autowired
    MediaFilesRepository mediaFilesRepository;
    @Autowired
    PromoteRepository promoteRepository;
    @Autowired
    VisitPageRepository visitPageRepository;

    private static final Logger logger = LoggerFactory.getLogger(PinProfileServiceImpl.class);

    @Override
    public ResponseEntity<?> addProfile(UserProfilePinWebModel userProfilePinWebModel) {
        try {
            Integer userId = userDetails.userInfo().getId();
            Integer pinProfileId = userProfilePinWebModel.getPinProfileId();

            Optional<UserProfilePin> existingPinOptional =
                    pinProfileRepository.findByUserIdAndPinProfileId(userId, pinProfileId);

            UserProfilePin userProfilePin;
            String message;

            if (existingPinOptional.isPresent()) {
                userProfilePin = existingPinOptional.get();
                userProfilePin.setUpdatedBy(userId);

                boolean newStatus = !userProfilePin.isStatus();
                userProfilePin.setStatus(newStatus);

                logger.info("Existing pin status before update: {}", !newStatus);
                logger.info("Updated pin status: {}", newStatus);

                message = newStatus ? "Profile pinned successfully." : "Profile unpinned successfully.";
            } else {
                userProfilePin = new UserProfilePin();
                userProfilePin.setUserId(userId);
                userProfilePin.setPinProfileId(pinProfileId);
                userProfilePin.setCreatedBy(userId);
                userProfilePin.setStatus(true);

                message = "Profile pinned successfully.";
            }

            pinProfileRepository.save(userProfilePin);
            return ResponseEntity.ok(new Response(1, "success", message));

        } catch (Exception e) {
            logger.error("Error setting addPin: {}", e.getMessage());
            return ResponseEntity.internalServerError()
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
                userMediaPin.setStatus(!userMediaPin.isStatus());
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
            logger.error("Error setting addMedia() -> {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new Response(-1, "Error setting addPin", e.getMessage()));
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
                    Optional<User> userOptional = userRepository.getUserByUserId(pinProfileId);

                    if (userOptional.isPresent()) {
                        User user = userOptional.get();

                        // Retrieve profile picture from mediaFiles table based on user ID
                        List<MediaFiles> profilePicOptional = mediaFilesRepository.getMediaFilesByUserIdAndCategory(pinProfileId, MediaFileCategory.ProfilePic);

                        LinkedHashMap<String, Object> pinData = new LinkedHashMap<>();
                        pinData.put("pinProfileId", userProfilePin.getPinProfileId());
                        pinData.put("userId", userProfilePin.getUserId());
                        pinData.put("userName", user.getName());
                        pinData.put("userGender", user.getGender());
                        pinData.put("review", user.getAdminReview());
                        pinData.put("userType", user.getUserType());

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
                        logger.warn("User not found for pinProfileId :- {}", pinProfileId);
                    }
                } else {
                    logger.warn("pinProfileId is null for userProfilePin :- {}", userProfilePin);
                }
            }
            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            logger.error("getAllProfilePin service Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", ""));
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
                Posts post = postsRepository.findById(userMediaPin.getPinMediaId()).orElse(null);
                if (post == null) continue;

                // Transform the post data to PostWebModel
                PostWebModel postWebModel = transformPostDataToPostWebModel(post);

                // Adding PostWebModel to the combined details list
                Map<String, Object> combinedDetails = new HashMap<>();
                combinedDetails.put("postWebModel", postWebModel);

                // Add the combined details to the list
                combinedDetailsList.add(combinedDetails);
            }
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("combinedDetailsList", combinedDetailsList);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            logger.error("Error in getAllMediaPin: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", ""));
        }
    }

private PostWebModel transformPostDataToPostWebModel(Posts post) {
    PostWebModel postWebModel = null;
    try {
        Integer loggedInUserTemp = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            loggedInUserTemp = ((UserDetailsImpl) principal).getId();
        }
        final Integer finalLoggedInUser = loggedInUserTemp;

        List<FileOutputWebModel> postFiles = mediaFilesService
                .getMediaFilesByCategoryAndRefId(MediaFileCategory.Post, post.getId());

        // Profession
        Set<String> professionNames = new HashSet<>();
        String userType = post.getUser().getUserType();
        if (userType != null && !userType.isEmpty()) {
            professionNames.add(userType);
        } else {
            professionNames.add("Public User");
        }

        // Followers
        List<FollowersRequest> followersList = friendRequestRepository
                .findByFollowersRequestReceiverIdAndFollowersRequestIsActive(post.getUser().getUserId(), true);

        // Likes & Unlikes for logged-in user
        Boolean likeStatus = false;
        Boolean unlikeStatus = false;
        Integer latestLikeId = null;

        if (finalLoggedInUser != null) {
            // Fetch all likes for this user once (you can also cache this outside the loop for efficiency)
            List<Likes> userLikes = likeRepository.findAllByUserIdForPosts(finalLoggedInUser);

            // Find the like entry for the current post
            Likes r = userLikes.stream()
                    .filter(like -> like.getPostId().equals(post.getId()))
                    .findFirst()
                    .orElse(null);

            if (r != null) {
                latestLikeId = r.getLikeId();

                if ("LIKE".equalsIgnoreCase(r.getReactionType())) {
                    likeStatus = true;
                    unlikeStatus = false;
                } else if ("UNLIKE".equalsIgnoreCase(r.getReactionType())) {
                    likeStatus = false;
                    unlikeStatus = true;
                }
            }
        }
     // Count total likes/unlikes with category filter
        Long totalLikesCount = likeRepository.countByPostIdAndReactionTypeAndCategory(
                post.getId(), "LIKE", "Post");

        Long totalUnlikesCount = likeRepository.countByPostIdAndReactionTypeAndCategory(
                post.getId(), "UNLIKE", "Post");

        // Pin Statusy
        Boolean pinStatus = false;
        if (finalLoggedInUser != null) {
            Optional<UserProfilePin> userData = pinProfileRepository
                    .findByPinProfileIdAndUserId(finalLoggedInUser, post.getUser().getUserId());
            pinStatus = userData.map(UserProfilePin::isStatus).orElse(false);
        }

        Boolean pinMediaStatus = false;
        if (finalLoggedInUser != null) {
            Optional<UserMediaPin> userData =
                    pinMediaRepository.findByUserIdAndPinMediaId(finalLoggedInUser, post.getId());
            pinMediaStatus = userData.isPresent();
        }

        // Promote
        boolean isPromoted = promoteRepository.existsByPostIdAndStatus(post.getId(), true);
        Optional<Promote> promoteDetailsOpt = promoteRepository.findByPostIds(post.getId());
        Promote promoteDetails = promoteDetailsOpt.orElse(null);

        // Tagged users
        List<Map<String, Object>> taggedUsers = post.getPostTagsCollection() != null
                ? post.getPostTagsCollection().stream()
                .filter(postTags -> Boolean.TRUE.equals(postTags.getStatus()))
                .map(postTags -> {
                    Map<String, Object> taggedUserDetails = new HashMap<>();
                    Integer taggedUserId = postTags.getTaggedUser().getUserId();
                    taggedUserDetails.put("userId", taggedUserId);
                    userService.getUser(taggedUserId).ifPresent(user -> {
                        taggedUserDetails.put("username", user.getName());
                        taggedUserDetails.put("userProfilePic", userService.getRecieverProfilePicUrl(taggedUserId));
                    });
                    return taggedUserDetails;
                })
                .collect(Collectors.toList())
                : null;

        LocalDateTime createdOn = LocalDateTime.ofInstant(post.getCreatedOn().toInstant(), ZoneId.systemDefault());
        String elapsedTime = CalendarUtil.calculateElapsedTime(createdOn);

        postWebModel = PostWebModel.builder()
                .id(post.getId())
                .userId(post.getUser().getUserId())
                .userName(post.getUser().getName())
                .postId(post.getPostId())
                .adminReview(post.getUser().getAdminReview())
                .userProfilePic(userService.getProfilePicUrl())
                .description(post.getDescription())
                .pinMediaStatus(pinMediaStatus)
                .pinProfileStatus(pinStatus)
                .userType(post.getUser().getUserType())
                .likeCount(totalLikesCount.intValue())
                .UnlikesCount(totalUnlikesCount.intValue())
                .UnlikeStatus(unlikeStatus)
                .shareCount(post.getSharesCount())
                .commentCount(post.getCommentsCount())
                .promoteFlag(post.getPromoteFlag())
                .postFiles(postFiles)
                .postLinkUrl(post.getPostLinkUrls())
                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .address(post.getAddress())
                .likeStatus(likeStatus)
                .likeId(latestLikeId)
                .elapsedTime(elapsedTime)
                .privateOrPublic(post.getPrivateOrPublic())
                .locationName(post.getLocationName())
                .professionNames(professionNames)
                .followersCount(followersList.size())
                .createdOn(post.getCreatedOn())
                .createdBy(post.getCreatedBy())
                .taggedUserss(taggedUsers)
                .promoteStatus(promoteDetails != null)
                .promoteId(promoteDetails != null ? promoteDetails.getPromoteId() : null)
                .numberOfDays(promoteDetails != null ? promoteDetails.getNumberOfDays() : null)
                .amount(promoteDetails != null ? promoteDetails.getAmount() : null)
                .contactNumber(promoteDetails != null ? promoteDetails.getContactNumber() : null)
                .webSiteLink(promoteDetails != null ? promoteDetails.getWebSiteLink() : null)
                .selectOption(promoteDetails != null ? promoteDetails.getSelectOption() : null)
                .visitPage(promoteDetails != null ? promoteDetails.getVisitPage() : null)
                .visitPageData(fetchVisitPageData(promoteDetails))
                .viewsCount(post.getViewsCount())
                .build();

    } catch (Exception e) {
        logger.error("Error at transformPostDataToPostWebModel() -> {}", e.getMessage(), e);
    }
    return postWebModel;
}

private String fetchVisitPageData(Promote promoteDetails) {
	if (promoteDetails != null && promoteDetails.getSelectOption() != null) {
		// Assuming selectedOption is a foreign key that refers to VisitPage
		Optional<VisitPage> visitPageOpt = visitPageRepository.findById(promoteDetails.getSelectOption());
		return visitPageOpt.map(VisitPage::getData).orElse(null); // Fetching the data field
	}
	return null; // Return null if no data is available
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
                        logger.warn("User not found for pinProfileId :- {}", userProfilePin.getPinProfileId());
                    }
                } else {
                    logger.warn("UserProfilePin not found for profileId :- {}", profileId);
                }
            } else {
                logger.warn("profileId is null in request");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("getByProfileId service Method Exception {} ", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", ""));
        }

    }

    @Override
    public ResponseEntity<?> profilePinStatus(UserProfilePinWebModel userProfilePinWebModel) {
        try {
            logger.info("profilePinStatus service start");
            Optional<UserProfilePin> pinOptional = pinProfileRepository.findById(userProfilePinWebModel.getUserProfilePinId());
            if (pinOptional.isPresent()) {
                UserProfilePin userProfilePin = pinOptional.get();
                userProfilePin.setStatus(userProfilePinWebModel.isStatus());
                pinProfileRepository.save(userProfilePin);
                return ResponseEntity.ok("Pin status changed successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("profilePinStatus Method Exception -> {} ", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", ""));
        }
    }

    @Override
    public ResponseEntity<?> mediaPinStatus(UserProfilePinWebModel userProfilePinWebModel) {
        try {
            logger.info("mediaPinStatus service start");
            Optional<UserMediaPin> pinOptional = pinMediaRepository.findById(userProfilePinWebModel.getUserMediaPinId());
            if (pinOptional.isPresent()) {
                UserMediaPin userProfilePin = pinOptional.get();
                userProfilePin.setStatus(userProfilePinWebModel.isStatus());
                pinMediaRepository.save(userProfilePin);
                return ResponseEntity.ok("Pin status changed successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("changePinStatus service Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", ""));
        }
    }
}
