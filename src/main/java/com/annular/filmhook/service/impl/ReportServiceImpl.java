package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.annular.filmhook.model.ReportPost;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserProfilePin;
import com.annular.filmhook.repository.FilmProfessionPermanentDetailRepository;
import com.annular.filmhook.repository.FriendRequestRepository;
import com.annular.filmhook.repository.LikeRepository;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.PinProfileRepository;
import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.ReportRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.ReportService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.ReportPostWebModel;

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired
	UserDetails userDetails;
	
	@Autowired
	S3Util s3Util;
	
	@Autowired
	FileUtil fileUtil;
	
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
	
	@Autowired
	MediaFilesRepository mediaFilesRepository;

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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Error setting reportPost", e.getMessage()));
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
	            Optional<Posts> postOptional = postsRepository.findById(reportPost.getPostId());
	            if (!postOptional.isPresent()) {
	                continue;
	            }
	            Posts post = postOptional.get();

	            List<FileOutputWebModel> postFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Post, post.getId());

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

	            Map<String, Object> combinedDetails = new HashMap<>();
	            combinedDetails.put("postWebModel", postWebModel);
	            combinedDetailsList.add(combinedDetails);
	        }

	        response.put("pageDetails", pageDetails);
	        response.put("combinedDetailsList", combinedDetailsList);

	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        logger.error("Error in getAllPostReport: {}", e.getMessage(), e);
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("message", "Error retrieving post reports");
	        errorResponse.put("error", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}

	@Override
	public ResponseEntity<?> getByPostReportId(ReportPostWebModel reportPostWebModel) {
		  try {
	            Optional<ReportPost> optionalReportPost = reportRepository.findById(reportPostWebModel.getReportPostId());
	            if (optionalReportPost.isPresent()) {
	                ReportPost reportPost = optionalReportPost.get();
	                return new ResponseEntity<>(reportPost, HttpStatus.OK);
	            } else {
	                return new ResponseEntity<>("Report with ID " + reportPostWebModel.getReportPostId() + " not found", HttpStatus.NOT_FOUND);
	            }
	        } catch (Exception e) {
	            return new ResponseEntity<>("Error fetching post report: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    
	}

}
