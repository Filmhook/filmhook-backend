package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.FilmProfessionPermanentDetail;
import com.annular.filmhook.model.FollowersRequest;
import com.annular.filmhook.model.Likes;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.ReportPost;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserProfilePin;

import com.annular.filmhook.repository.FilmProfessionPermanentDetailRepository;
import com.annular.filmhook.repository.FriendRequestRepository;
import com.annular.filmhook.repository.LikeRepository;
import com.annular.filmhook.repository.PinProfileRepository;
import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.ReportRepository;
import com.annular.filmhook.repository.UserRepository;

import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.ReportService;
import com.annular.filmhook.service.UserService;

import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.ReportPostWebModel;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    UserDetails userDetails;

    @Autowired
    UserService userService;

    @Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    PostsRepository postsRepository;

    @Autowired
    FilmProfessionPermanentDetailRepository filmProfessionPermanentDetailRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PinProfileRepository pinProfileRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    FriendRequestRepository friendRequestRepository;

    @Autowired
    ReportRepository reportRepository;

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Override
    public ResponseEntity<?> addPostReport(ReportPostWebModel reportPostWebModel) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            ReportPost reportPost = new ReportPost();
            reportPost.setUserId(userDetails.userInfo().getId());
            reportPost.setPostId(reportPostWebModel.getPostId());
            reportPost.setReason(reportPostWebModel.getReason());
            reportPost.setStatus(false); // Assuming initially report status is false
            reportPost.setCreatedBy(userDetails.userInfo().getId()); // Assuming user who reports is the creator
            reportRepository.save(reportPost);
            response.put("reportInfo", reportPost);
            logger.info("addMethod method end");
            return ResponseEntity.ok(new Response(1, "Add ReportPost successfully", response));
        } catch (Exception e) {
            logger.error("Error setting reportPost {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new Response(-1, "Error setting reportPost", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> getAllPostReport(ReportPostWebModel reportPostWebModel) {
        try {
            Map<String, Object> response = new HashMap<>();

            Pageable paging = PageRequest.of(reportPostWebModel.getPageNo() - 1, reportPostWebModel.getPageSize());
            Page<ReportPost> reportPosts = reportRepository.findAll(paging);
            List<Map<String, Object>> combinedDetailsList = new ArrayList<>();

            Map<String, Object> pageDetails = new HashMap<>();
            pageDetails.put("totalPages", reportPosts.getTotalPages());
            pageDetails.put("totalRecords", reportPosts.getTotalElements());

            for (ReportPost reportPost : reportPosts) {
                Posts post = postsRepository.findById(reportPost.getPostId()).orElse(null);
                if (post == null) continue;

                List<FileOutputWebModel> postFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Post, post.getId());

                Set<String> professionNames = filmProfessionPermanentDetailRepository.getProfessionDataByUserId(post.getUser().getUserId()).stream()
                        .map(FilmProfessionPermanentDetail::getProfessionName)
                        .collect(Collectors.toSet());

                List<FollowersRequest> followersList = friendRequestRepository.findByFollowersRequestReceiverIdAndFollowersRequestIsActive(post.getUser().getUserId(), true);

                Integer userId = userDetails.userInfo().getId();
                boolean likeStatus = likeRepository.findByPostIdAndUserId(post.getId(), userId)
                        .map(Likes::getStatus)
                        .orElse(false);

                boolean pinStatus = pinProfileRepository.findByPinProfileIdAndUserId(post.getUser().getUserId(), userId)
                        .map(UserProfilePin::isStatus)
                        .orElse(false);

                PostWebModel postWebModel = PostWebModel.builder()
                        .id(post.getId())
                        .userId(post.getUser().getUserId())
                        .userName(post.getUser().getName())
                        .postId(post.getPostId())
                        .userProfilePic(userService.getProfilePicUrl(post.getUser().getUserId()))
                        .description(post.getDescription())
                        .pinStatus(pinStatus)
                        .likeCount(post.getLikesCount())
                        .shareCount(post.getSharesCount())
                        .commentCount(post.getCommentsCount())
                        .promoteFlag(post.getPromoteFlag())
                        .postFiles(postFiles).likeStatus(likeStatus)
                        .privateOrPublic(post.getPrivateOrPublic())
                        .locationName(post.getLocationName())
                        .professionNames(professionNames)
                        .followersCount(followersList.size())
                        .build();

                Map<String, Object> combinedDetails = new HashMap<>();
                combinedDetails.put("postWebModel", postWebModel);

                // Fetch ReportTable details
                Optional<ReportPost> reportTableOptional = reportRepository.findById(reportPost.getReportPostId());
                if (reportTableOptional.isPresent()) {
                    ReportPost reportTable = reportTableOptional.get();
                    combinedDetails.put("reportDetails", reportTable);
                }
                combinedDetailsList.add(combinedDetails);
            }
            response.put("pageDetails", pageDetails);
            response.put("combinedDetailsList", combinedDetailsList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in getAllPostReport: {}", e.getMessage(), e);
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error retrieving post reports", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> getByPostReportId(ReportPostWebModel reportPostWebModel) {
        try {
            Optional<ReportPost> optionalReportPost = reportRepository.findById(reportPostWebModel.getReportPostId());
            if (optionalReportPost.isPresent()) {
                ReportPost reportPost = optionalReportPost.get();
                return ResponseEntity.ok(reportPost);
            } else {
                return ResponseEntity.ok().body(new Response(-1, "Report with ID " + reportPostWebModel.getReportPostId() + " not found", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error retrieving post reports", e.getMessage()));
        }

    }

    @Override
    public ResponseEntity<?> getAllReportsByPostId(ReportPostWebModel reportPostWebModel) {
        Map<String, Object> response = new HashMap<>();
        try {
            Pageable paging = PageRequest.of(reportPostWebModel.getPageNo() - 1, reportPostWebModel.getPageSize());
            Page<ReportPost> reportPosts = reportRepository.findAll(paging);

            List<Map<String, Object>> combinedDetailsList = new ArrayList<>();

            Map<String, Object> pageDetails = new HashMap<>();
            pageDetails.put("totalPages", reportPosts.getTotalPages());
            pageDetails.put("totalRecords", reportPosts.getTotalElements());

            Set<Integer> processedPostIds = new HashSet<>();

            for (ReportPost reportPost : reportPosts) {
                if (processedPostIds.contains(reportPost.getPostId())) continue;

                processedPostIds.add(reportPost.getPostId());

                Posts post = postsRepository.findById(reportPost.getPostId()).orElse(null);
                if (post == null) continue;

                List<FileOutputWebModel> postFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Post, post.getId());

                Set<String> professionNames = filmProfessionPermanentDetailRepository.getProfessionDataByUserId(post.getUser().getUserId()).stream()
                        .map(FilmProfessionPermanentDetail::getProfessionName)
                        .collect(Collectors.toSet());

                List<FollowersRequest> followersList = friendRequestRepository.findByFollowersRequestReceiverIdAndFollowersRequestIsActive(post.getUser().getUserId(), true);

                Integer userId = userDetails.userInfo().getId();
                boolean likeStatus = likeRepository.findByPostIdAndUserId(post.getId(), userId)
                        .map(Likes::getStatus)
                        .orElse(false);

                boolean pinStatus = pinProfileRepository.findByPinProfileIdAndUserId(post.getUser().getUserId(), userId)
                        .map(UserProfilePin::isStatus)
                        .orElse(false);

                PostWebModel postWebModel = PostWebModel.builder()
                        .id(post.getId())
                        .userId(post.getUser().getUserId())
                        .userName(post.getUser().getName())
                        .postId(post.getPostId())
                        .userProfilePic(userService.getProfilePicUrl(post.getUser().getUserId()))
                        .description(post.getDescription())
                        .pinStatus(pinStatus)
                        .likeCount(post.getLikesCount())
                        .shareCount(post.getSharesCount())
                        .commentCount(post.getCommentsCount())
                        .promoteFlag(post.getPromoteFlag())
                        .postFiles(postFiles)
                        .likeStatus(likeStatus)
                        .privateOrPublic(post.getPrivateOrPublic())
                        .locationName(post.getLocationName())
                        .professionNames(professionNames)
                        .followersCount(followersList.size())
                        .build();

                Map<String, Object> combinedDetails = new HashMap<>();
                combinedDetails.put("postWebModel", postWebModel);

//				// Fetch ReportTable details and collect user IDs who reported this post
//				List<ReportPost> reportDetailsList = reportRepository.findByPostId(reportPost.getPostId());
//				List<Integer> reportUserIds = reportDetailsList.stream()
//						.map(ReportPost::getUserId)
//						.collect(Collectors.toList());
//				long uniqueReportUserIdCount = reportDetailsList.stream().map(ReportPost::getUserId).distinct().count();
//
//				combinedDetails.put("reportDetails", reportDetailsList);
//				combinedDetails.put("reportUserIds", reportUserIds);
//				combinedDetails.put("reportUserIdCount", uniqueReportUserIdCount);
                // Fetch ReportTable details and collect user IDs who reported this post
                List<ReportPost> reportDetailsList = reportRepository.findByPostId(reportPost.getPostId());
                List<Integer> reportUserIdss = reportDetailsList.stream()
                        .map(ReportPost::getUserId)
                        .collect(Collectors.toList());

                // Fetch user details for the reportUserIds
                List<User> users = userRepository.findAllById(reportUserIdss);
                // Count the number of reports per user
                List<ReportPost> postData = reportRepository.findAll();
                Map<Integer, Long> reportCountsPerUser = postData.stream()
                        .collect(Collectors.groupingBy(ReportPost::getUserId, Collectors.counting()));

                // Create a list of maps, each containing a userId and userName
                List<Map<String, Object>> reportUserIds = users.stream()
                        .map(user -> {
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("userId", user.getUserId());
                            userMap.put("username", user.getName());
                            userMap.put("reportCount", reportCountsPerUser.getOrDefault(user.getUserId(), 0L));
                            return userMap;
                        })
                        .collect(Collectors.toList());

                // Count unique report user IDs
                long reportUserIdCount = reportDetailsList.stream()
                        .map(ReportPost::getUserId)
                        .distinct()
                        .count();

                // Add the details to combinedDetails
                combinedDetails.put("reportDetails", reportDetailsList);
                combinedDetails.put("reportUserIds", reportUserIds);
                combinedDetails.put("reportUserIdCount", reportUserIdCount);

                combinedDetailsList.add(combinedDetails);
            }

            response.put("pageDetails", pageDetails);
            response.put("combinedDetailsList", combinedDetailsList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in getAllPostReport: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error retrieving post reports", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> getReportsByUserId(ReportPostWebModel postWebModel) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> combinedDetailsList = new ArrayList<>();
        try {
            List<ReportPost> reportPosts = reportRepository.findByUserId(postWebModel.getUserId());
            for (ReportPost reportPost : reportPosts) {
                Posts post = postsRepository.findById(reportPost.getPostId()).orElse(null);
                if (post == null) continue;

                List<FileOutputWebModel> postFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Post, post.getId());

                Set<String> professionNames = filmProfessionPermanentDetailRepository.getProfessionDataByUserId(post.getUser().getUserId()).stream()
                        .map(FilmProfessionPermanentDetail::getProfessionName)
                        .collect(Collectors.toSet());

                List<FollowersRequest> followersList = friendRequestRepository.findByFollowersRequestReceiverIdAndFollowersRequestIsActive(post.getUser().getUserId(), true);

                Integer userId = userDetails.userInfo().getId();
                boolean likeStatus = likeRepository.findByPostIdAndUserId(post.getId(), userId)
                        .map(Likes::getStatus)
                        .orElse(false);

                boolean pinStatus = pinProfileRepository.findByPinProfileIdAndUserId(post.getUser().getUserId(), userId)
                        .map(UserProfilePin::isStatus)
                        .orElse(false);

                PostWebModel postWebModels = PostWebModel.builder()
                        .id(post.getId())
                        .userId(post.getUser().getUserId())
                        .userName(post.getUser().getName())
                        .postId(post.getPostId())
                        .userProfilePic(userService.getProfilePicUrl(post.getUser().getUserId()))
                        .description(post.getDescription())
                        .pinStatus(pinStatus)
                        .likeCount(post.getLikesCount())
                        .shareCount(post.getSharesCount())
                        .commentCount(post.getCommentsCount())
                        .promoteFlag(post.getPromoteFlag())
                        .postFiles(postFiles)
                        .likeStatus(likeStatus)
                        .privateOrPublic(post.getPrivateOrPublic())
                        .locationName(post.getLocationName())
                        .professionNames(professionNames)
                        .followersCount(followersList.size())
                        .build();

                Map<String, Object> combinedDetails = new HashMap<>();
                combinedDetails.put("postWebModel", postWebModels);
                combinedDetails.put("reportDetails", reportPost);
                combinedDetailsList.add(combinedDetails);
            }
            response.put("combinedDetailsList", combinedDetailsList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in getReportsByUserId: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error retrieving post reports", e.getMessage()));
        }
    }
}

