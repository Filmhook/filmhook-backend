package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserMediaPin;
import com.annular.filmhook.model.UserProfilePin;
import com.annular.filmhook.model.VisitPage;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.Promote;
import com.annular.filmhook.model.Likes;
import com.annular.filmhook.model.Link;
import com.annular.filmhook.model.Audition;
import com.annular.filmhook.model.Comment;
import com.annular.filmhook.model.Share;
import com.annular.filmhook.model.PostTags;
import com.annular.filmhook.model.PostView;
import com.annular.filmhook.model.MediaFileCategory;

import com.annular.filmhook.model.FollowersRequest;
import com.annular.filmhook.model.InAppNotification;
import com.annular.filmhook.util.CalendarUtil;
import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.LikeWebModel;
import com.annular.filmhook.webmodel.LinkWebModel;
import com.annular.filmhook.webmodel.CommentInputWebModel;
import com.annular.filmhook.webmodel.CommentOutputWebModel;
import com.annular.filmhook.webmodel.ShareWebModel;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.stream.Collectors;
import com.annular.filmhook.service.PostService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.PromoteRepository;
import com.annular.filmhook.repository.FilmProfessionPermanentDetailRepository;
import com.annular.filmhook.repository.LikeRepository;
import com.annular.filmhook.repository.LinkRepository;
import com.annular.filmhook.repository.PinMediaRepository;
import com.annular.filmhook.repository.PinProfileRepository;
import com.annular.filmhook.repository.AuditionRepository;
import com.annular.filmhook.repository.CommentRepository;
import com.annular.filmhook.repository.ShareRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.repository.VisitPageRepository;
import com.annular.filmhook.security.UserDetailsImpl;
import com.annular.filmhook.repository.PostTagsRepository;
import com.annular.filmhook.repository.PostViewRepository;
import com.annular.filmhook.repository.FriendRequestRepository;
import com.annular.filmhook.repository.InAppNotificationRepository;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.util.FileUtil;
import software.amazon.awssdk.services.s3.model.S3Object;
import java.time.Duration;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	UserDetails userDetails;

	public static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

	@Autowired
	MediaFilesService mediaFilesService;


	@Autowired
	private UserRepository userRepository;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	PinProfileRepository pinProfileRepository;

	@Autowired
	PinMediaRepository pinMediaRepository;

	@Autowired
	UserService userService;

	@Autowired
	AwsS3ServiceImpl awsService;

	@Autowired
	PostsRepository postsRepository;

	@Autowired
	FilmProfessionPermanentDetailRepository filmProfessionPermanentDetailRepository;

	@Autowired
	LikeRepository likeRepository;

	@Autowired
	VisitPageRepository visitPageRepository;

	@Autowired
	LinkRepository linkRepository;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	ShareRepository shareRepository;

	@Autowired
	PromoteRepository promoteRepository;

	@Autowired
	PostTagsRepository postTagsRepository;

	@Value("${annular.app.url}")
	private String appUrl;

	@Autowired
	InAppNotificationRepository inAppNotificationRepository;

	@Autowired
	FriendRequestRepository friendRequestRepository;
	@Autowired
	PostViewRepository postViewRepository;
	@Autowired
	AuditionRepository auditionRepository;


	private static final String POST = "Post";
	private static final String COMMENT = "Comment";
	public static final String AUDITION = "Audition";

	@Override
	public PostWebModel savePostsWithFiles(PostWebModel postWebModel) {
		try {
			User userFromDB = userService.getUser(postWebModel.getUserId()).orElse(null);
			if (userFromDB != null) {
				logger.info("User found: {}", userFromDB.getName());
				// Saving Tagged Users' IDs as a comma-separated string
				String taggedUserIds = !Utility.isNullOrEmptyList(postWebModel.getTaggedUsers())
						? postWebModel.getTaggedUsers().stream().map(String::valueOf).collect(Collectors.joining(","))
								: null;

				Posts posts = Posts.builder()
						.postId(UUID.randomUUID().toString())
						.description(postWebModel.getDescription())
						.user(userFromDB)
						.postLinkUrls(postWebModel.getPostLinkUrl())
						.latitude(postWebModel.getLatitude())
						.longitude(postWebModel.getLongitude())
						.address(postWebModel.getAddress())
						.status(true)
						.privateOrPublic(postWebModel.getPrivateOrPublic())
						.promoteFlag(false)
						.promoteStatus(true)
						.locationName(postWebModel.getLocationName())
						.likesCount(0)
						.commentsCount(0)
						.sharesCount(0)
						.createdBy(postWebModel.getUserId())
						.createdOn(new Date())
						.tagUsers(taggedUserIds)
						.build();
				Posts savedPost = postsRepository.saveAndFlush(posts);

				if (!Utility.isNullOrEmptyList(postWebModel.getFiles())) {
					// Saving the Post files in the media_files table
					FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
							.userId(postWebModel.getUserId())
							.category(MediaFileCategory.Post)
							.categoryRefId(savedPost.getId())
							.files(postWebModel.getFiles())
							.build();
					mediaFilesService.saveMediaFiles(fileInputWebModel, userFromDB);
				}

				// Saving Tagged users
				if (!Utility.isNullOrEmptyList(postWebModel.getTaggedUsers())) {
					List<PostTags> tagsList = postWebModel.getTaggedUsers().stream()
							.map(taggedUserId -> PostTags.builder()
									.postId(savedPost.getId())
									.taggedUser(User.builder().userId(taggedUserId).build())
									.status(true)
									.createdBy(postWebModel.getUserId())
									.createdOn(new Date())
									.build())
							.collect(Collectors.toList());
					postTagsRepository.saveAllAndFlush(tagsList);

					for (PostTags tag : tagsList) {
						Integer taggedUserId = tag.getTaggedUser().getUserId();

						InAppNotification notification = InAppNotification.builder()
								.senderId(postWebModel.getUserId())
								.receiverId(taggedUserId)
								.title("You've been tagged in a post")
								.message( " tagged you in a post.")
								.createdOn(new Date())
								.isRead(false)
								.adminReview(userFromDB.getAdminReview())
								.Profession(userFromDB.getUserType())
								.isDeleted(false)
								.createdBy(postWebModel.getUserId())
								.id(tag.getId()) 
								.userType("Tagged")
								.postId(savedPost.getPostId())
								.build();

						inAppNotificationRepository.save(notification);

						// Send Firebase push notification
						User receiver = userService.getUser(taggedUserId).orElse(null);
						if (receiver != null && receiver.getFirebaseDeviceToken() != null && !receiver.getFirebaseDeviceToken().trim().isEmpty()) {
							try {
								String deviceToken = receiver.getFirebaseDeviceToken();
								String title = "You've been tagged!";
								String messageBody = userFromDB.getName() + " tagged you in a post.";

								Notification firebaseNotification = Notification.builder()
										//		                                .setTitle(title)
										.setTitle(messageBody)
										//		                                .setBody(messageBody)
										.build();

								AndroidNotification androidNotification = AndroidNotification.builder()
										.setIcon("ic_notification")
										.setColor("#00A2E8")
										.build();

								AndroidConfig androidConfig = AndroidConfig.builder()
										.setNotification(androidNotification)
										.build();

								Message firebaseMessage = Message.builder()
										.setNotification(firebaseNotification)
										.putData("type", "Tagged")
										.putData("refId", String.valueOf(savedPost.getId()))
										.putData("postId", savedPost.getPostId())
										.putData("senderId", String.valueOf(postWebModel.getUserId()))
										.putData("receiverId", String.valueOf(taggedUserId))
										.setAndroidConfig(androidConfig)
										.setToken(deviceToken)
										.build();

								String firebaseResponse = FirebaseMessaging.getInstance().send(firebaseMessage);
								logger.info("Push notification sent successfully: {}", firebaseResponse);

							} catch (FirebaseMessagingException e) {
								logger.error("Firebase push notification failed: {}", e.getMessage(), e);
							}
						}
					}
				}


				List<PostWebModel> responseList = this.transformPostsDataToPostWebModel(List.of(savedPost));
				return responseList.isEmpty() ? null : responseList.get(0);
			}
		} catch (Exception e) {
			logger.error("Error at savePostsWithFiles() -> {}", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public Resource getPostFile(Integer userId, String category, String fileId, String fileType) {
		try {
			Optional<User> userFromDB = userService.getUser(userId);
			if (userFromDB.isPresent()) {
				String filePath = FileUtil.generateFilePath(userFromDB.get(), category, fileId + fileType);
				return new ByteArrayResource(fileUtil.downloadFile(filePath));
			}
		} catch (Exception e) {
			logger.error("Error at getPostFile() -> {}", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	//    @Override
	//    public List<PostWebModel> getPostsByUserId(Integer userId) {
	//        try {
	//            List<Posts> postList = postsRepository.getUserPosts(User.builder().userId(userId).build());
	//            return this.transformPostsDataToPostWebModel(postList);
	//        } catch (Exception e) {
	//            logger.error("Error at getPostsByUserId() -> {}", e.getMessage());
	//            e.printStackTrace();
	//            return null;
	//        }
	//    }

	@Override
	public List<PostWebModel> getPostsByUserId(Integer userId, Integer pageNo, Integer pageSize, Integer highlightPostId) {
		try {
			// Fetch posts created by the user
			List<Posts> userPosts = postsRepository.getUserPosts(User.builder().userId(userId).build());

			// Fetch tagged posts
			String userIdString = userId.toString();
			List<Posts> taggedPosts = postsRepository.getPostsByTaggedUserId(userIdString);

			// Combine and remove duplicates
			Set<Posts> combinedPostsSet = new HashSet<>(userPosts);
			combinedPostsSet.addAll(taggedPosts);
			List<Posts> combinedPostsList = new ArrayList<>(combinedPostsSet);

			// Sorting logic
			combinedPostsList.sort(Comparator
					.comparing(Posts::getPromoteFlag, Comparator.nullsFirst(Comparator.naturalOrder()))
					.thenComparing(Posts::getCreatedOn, Comparator.nullsLast(Comparator.reverseOrder())));

			// If highlightPostId is passed → move it to the top
			if (highlightPostId != null) {
				Posts highlighted = combinedPostsList.stream()
						.filter(post -> post.getPostId().equals(highlightPostId))
						.findFirst()
						.orElseGet(() -> postsRepository.findById(highlightPostId).orElse(null));

				if (highlighted != null) {
					combinedPostsList.remove(highlighted);
					combinedPostsList.add(0, highlighted);
				}
			}


			// Pagination
			int totalPosts = combinedPostsList.size();
			int fromIndex = Math.min((pageNo - 1) * pageSize, totalPosts);
			int toIndex = Math.min(fromIndex + pageSize, totalPosts);

			List<Posts> paginatedPosts = combinedPostsList.subList(fromIndex, toIndex);

			return this.transformPostsDataToPostWebModel(paginatedPosts);

		} catch (Exception e) {
			logger.error("Error at getPostsByUserId() -> {}", e.getMessage(), e);
			return null;
		}
	}

	@Override
	public PostWebModel getPostByPostId(String postId) {
		Posts post = postsRepository.findByPostId(postId);
		List<PostWebModel> responseList = this.transformPostsDataToPostWebModel(List.of(post));
		return responseList.isEmpty() ? null : responseList.get(0);
	}

	@Override
	public PostWebModel getPostById(Integer id) {
		Posts post = postsRepository.findById(id).orElse(null);
		if (post == null) {
			return null;
		}
		List<PostWebModel> responseList = this.transformPostsDataToPostWebModel(List.of(post));
		return responseList.isEmpty() ? null : responseList.get(0);
	}

	private List<PostWebModel> transformPostsDataToPostWebModel(List<Posts> postList) {
		List<PostWebModel> responseList = new ArrayList<>();
		try {
			Integer loggedInUserTemp = null;
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal instanceof UserDetailsImpl) {
				loggedInUserTemp = ((UserDetailsImpl) principal).getId();
			}
			final Integer finalLoggedInUser = loggedInUserTemp;


			if (!Utility.isNullOrEmptyList(postList)) {
				postList.stream().filter(Objects::nonNull).forEach(post -> {

					List<FileOutputWebModel> postFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Post, post.getId());

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
						Optional<Likes> reactionOpt = likeRepository.findByPostIdAndUserId(post.getId(), finalLoggedInUser);
						if (reactionOpt.isPresent()) {
							Likes r = reactionOpt.get();
							latestLikeId = r.getLikeId();

							// Use reactionType instead of only status
							if ("LIKE".equalsIgnoreCase(r.getReactionType())) {
								likeStatus = true;
								unlikeStatus = false;
							} else if ("UNLIKE".equalsIgnoreCase(r.getReactionType())) {
								likeStatus = false;
								unlikeStatus = true;
							}
						}
					}

					// Count total likes/unlikes
					Long totalLikesCount = likeRepository.countByPostIdAndReactionType(post.getId(), "LIKE");
					Long totalUnlikesCount = likeRepository.countByPostIdAndReactionType(post.getId(), "UNLIKE");


					// Pin Status
					Boolean pinStatus = false;
					if (finalLoggedInUser != null) {
						Optional<UserProfilePin> userData = pinProfileRepository.findByPinProfileIdAndUserId(finalLoggedInUser, post.getUser().getUserId());
						pinStatus = userData.map(UserProfilePin::isStatus).orElse(false);
					}

					Boolean pinMediaStatus = false;
					if (finalLoggedInUser != null) {
						Optional<UserMediaPin> userData =
								pinMediaRepository.findByUserIdAndPinMediaId(finalLoggedInUser, post.getId());

						pinMediaStatus = userData.isPresent();   // only true if actively pinned
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
											taggedUserDetails.put("userProfilePic", userService.getProfilePicUrl(taggedUserId));
										});
										return taggedUserDetails;
									})
									.collect(Collectors.toList())
									: null;

					LocalDateTime createdOn = LocalDateTime.ofInstant(post.getCreatedOn().toInstant(), ZoneId.systemDefault());
					String elapsedTime = CalendarUtil.calculateElapsedTime(createdOn);

					PostWebModel postWebModel = PostWebModel.builder()
							.id(post.getId())
							.userId(post.getUser().getUserId())
							.userName(post.getUser().getName())
							.postId(post.getPostId())
							.adminReview(post.getUser().getAdminReview())
							.userProfilePic(userService.getProfilePicUrl(post.getUser().getUserId()))
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
							.whatsAppNumber(promoteDetails != null ? promoteDetails.getWhatsAppNumber() : null)
							.webSiteLink(promoteDetails != null ? promoteDetails.getWebSiteLink() : null)
							.selectOption(promoteDetails != null ? promoteDetails.getSelectOption() : null)
							.visitPage(promoteDetails != null ? promoteDetails.getVisitPage() : null)
							.visitPageData(fetchVisitPageData(promoteDetails))
							.viewsCount(post.getViewsCount())
							.build();

					responseList.add(postWebModel);
				});
			}
		} catch (Exception e) {
			logger.error("Error at transformPostsDataToPostWebModel() -> {}", e.getMessage(), e);
		}

		logger.info("Final post count to respond :- [{}]", responseList.size());
		return responseList;
	}


	private String fetchVisitPageData(Promote promoteDetails) {
		if (promoteDetails != null && promoteDetails.getSelectOption() != null) {
			// Assuming selectedOption is a foreign key that refers to VisitPage
			Optional<VisitPage> visitPageOpt = visitPageRepository.findById(promoteDetails.getSelectOption());
			return visitPageOpt.map(VisitPage::getData).orElse(null); // Fetching the data field
		}
		return null; // Return null if no data is available
	}


	private String generatePostUrl(String postId) {
		return !Utility.isNullOrBlankWithTrim(appUrl) && !Utility.isNullOrBlankWithTrim(postId) ? appUrl + "/user/post/view/" + postId : "";
	}

	@Override
	public Resource getAllPostByUserIdAndCategory(Integer userId, String category) {
		try {
			Optional<User> userFromDB = userService.getUser(userId);
			if (userFromDB.isPresent()) {
				String destinationPath = FileUtil.generateDestinationPath(userFromDB.get(), category);
				List<S3Object> s3data = awsService.getAllObjectsByBucketAndDestination("filmhook-dev-bucket", destinationPath);
				return new ByteArrayResource(fileUtil.downloadFile(s3data));
			}
		} catch (Exception e) {
			logger.error("Error at getAllPostByUserIdAndCategory() -> {}", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Resource getAllPostFilesByCategory(String category) {
		try {
			String destinationPath = FileUtil.generateDestinationPath(category);
			List<S3Object> s3data = awsService.getAllObjectsByBucketAndDestination("filmhook-dev-bucket", destinationPath);
			return new ByteArrayResource(fileUtil.downloadFile(s3data));
		} catch (Exception e) {
			logger.error("Error at getAllPostFilesByCategory() -> {}", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	//    @Override
	//    public List<PostWebModel> getAllUsersPosts(Integer pageNo, Integer pageSize) {
	//        try {
	//            Pageable paging = PageRequest.of(pageNo - 1, pageSize);
	//
	//            // Fetch all active posts with pagination
	//            List<Posts> postList = postsRepository.getAllActivePosts(paging);
	//
	//            // Sort the posts: false (or null) promoteFlag first, true last, then by creation date (newest first)
	//            postList.sort(Comparator
	//                    .comparing(Posts::getPromoteFlag, Comparator.nullsFirst(Comparator.naturalOrder())) // false/null first, true last
	//                    .thenComparing(Posts::getCreatedOn, Comparator.nullsLast(Comparator.reverseOrder()))); // Sort by creation date, newest first
	//
	//            // Transform the sorted posts into PostWebModel and return the result
	//            return this.transformPostsDataToPostWebModel(postList);
	//        } catch (Exception e) {
	//            logger.error("Error at getAllUsersPosts() -> {}", e.getMessage());
	//            e.printStackTrace();
	//            return null;
	//        }
	//    }

	@Override
	public List<PostWebModel> getAllUsersPosts(Integer pageNo, Integer pageSize) {
		try {
			List<Posts> allPosts = postsRepository.getAllActivePosts();

			if (allPosts == null || allPosts.isEmpty()) {
				return Collections.emptyList();
			}

			// Sort by createdOn (newest first)
			allPosts.sort(Comparator.comparing(Posts::getCreatedOn).reversed());

			List<Posts> promotedPosts = new ArrayList<>();
			List<Posts> normalPosts = new ArrayList<>();

			for (Posts post : allPosts) {
				if (Boolean.TRUE.equals(post.getPromoteFlag())) {
					promotedPosts.add(post);
				} else {
					normalPosts.add(post);
				}
			}

			// Interleave: 1 promoted + 5 normal pattern
			List<Posts> orderedPosts = new ArrayList<>();
			int promoIdx = 0, normalIdx = 0;

			while (promoIdx < promotedPosts.size() || normalIdx < normalPosts.size()) {
				if (promoIdx < promotedPosts.size()) {
					orderedPosts.add(promotedPosts.get(promoIdx++));
				}
				for (int i = 0; i < 5 && normalIdx < normalPosts.size(); i++) {
					orderedPosts.add(normalPosts.get(normalIdx++));
				}
			}

			// Pagination handling
			int start = (pageNo - 1) * pageSize;
			int end = Math.min(start + pageSize, orderedPosts.size());

			// If requested page is out of bounds, return empty
			if (start >= orderedPosts.size()) {
				return Collections.emptyList();
			}

			List<Posts> paginatedPosts = orderedPosts.subList(start, end);

			return transformPostsDataToPostWebModel(paginatedPosts);

		} catch (Exception e) {
			logger.error("Error in getAllUsersPosts(): {}", e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public LikeWebModel addOrUpdateLike(LikeWebModel likeWebModel) {
		try {
			Likes likeRowToSaveOrUpdate;
			Posts post = null;
			Audition audition = null;
			Comment existingComment = null;

			// --- Validate category targets ---
			if (POST.equalsIgnoreCase(likeWebModel.getCategory())) {
				post = postsRepository.findById(likeWebModel.getPostId()).orElse(null);
				if (post == null) return null;
			} else if (AUDITION.equalsIgnoreCase(likeWebModel.getCategory())) {
				audition = auditionRepository.findById(likeWebModel.getAuditionId()).orElse(null);
				if (audition == null) return null;
			} else if (COMMENT.equalsIgnoreCase(likeWebModel.getCategory())) {
				existingComment = likeWebModel.getCommentId() != null
						? commentRepository.findById(likeWebModel.getCommentId()).orElse(null)
								: null;
				if (existingComment == null) return null;
			}

			// --- Find existing like/unlike record ---
			Likes existingLike;
			if (!Utility.isNullObject(likeWebModel.getLikeId())) {
				existingLike = likeRepository.findById(likeWebModel.getLikeId()).orElse(null);
			} else {
				existingLike = likeRepository.findByCategoryAndLikedByAndPostIdAndCommentIdAndAuditionId(
						likeWebModel.getCategory(),
						likeWebModel.getUserId(),
						likeWebModel.getPostId(),
						likeWebModel.getCommentId(),
						likeWebModel.getAuditionId()
						).orElse(null);
			}

			if (existingLike != null) {
				// Update reaction type directly (LIKE or UNLIKE)
				existingLike.setReactionType(likeWebModel.getReactionType());
				existingLike.setStatus("LIKE".equalsIgnoreCase(likeWebModel.getReactionType())); // status = true for like, false for unlike
				existingLike.setUpdatedBy(likeWebModel.getUserId());
				existingLike.setUpdatedOn(new Date());
				likeRowToSaveOrUpdate = existingLike;
			} else {
				// Insert new record
				likeRowToSaveOrUpdate = Likes.builder()
						.category(likeWebModel.getCategory())
						.postId(likeWebModel.getPostId())
						.commentId(likeWebModel.getCommentId())
						.auditionId(likeWebModel.getAuditionId())
						.likedBy(likeWebModel.getUserId())
						.reactionType(likeWebModel.getReactionType())
						.status("LIKE".equalsIgnoreCase(likeWebModel.getReactionType()))
						.notified(false)
						.createdBy(likeWebModel.getUserId())
						.createdOn(new Date())
						.build();
			}

			Likes savedLike = likeRepository.saveAndFlush(likeRowToSaveOrUpdate);

			// --- Count likes & unlikes ---
			Integer totalLikes = likeRepository.countByReactionType(
					likeWebModel.getCategory(),
					likeWebModel.getPostId(),
					likeWebModel.getCommentId(),
					likeWebModel.getAuditionId(),
					"LIKE"
					);

			Integer totalUnlikes = likeRepository.countByReactionType(
					likeWebModel.getCategory(),
					likeWebModel.getPostId(),
					likeWebModel.getCommentId(),
					likeWebModel.getAuditionId(),
					"UNLIKE"
					);

			logger.info("Like count [{}], Unlike count [{}] for category [{}]",
					totalLikes, totalUnlikes, likeWebModel.getCategory());

			return this.transformLikeData(savedLike, totalLikes, totalUnlikes, likeWebModel.getUserId());

		} catch (Exception e) {
			logger.error("Error at addOrUpdateLike() -> {}", e.getMessage(), e);
			return null;
		}
	}



	private LikeWebModel transformLikeData(Likes likes, Integer totalLikes, Integer totalUnlikes, Integer loggedInUserId) {
		return LikeWebModel.builder()
				.likeId(likes.getLikeId())
				.category(likes.getCategory())
				.postId(likes.getPostId())
				.commentId(likes.getCommentId())
				.userId(likes.getLikedBy())
				.totalLikesCount(totalLikes)
				.totalUnlikesCount(totalUnlikes)
				.status(likes.getStatus())
				.isLiked(likes.getStatus() != null && likes.getStatus() && likes.getLikedBy().equals(loggedInUserId))
				.isUnliked(likes.getStatus() != null && !likes.getStatus() && likes.getLikedBy().equals(loggedInUserId))
				.createdBy(likes.getCreatedBy())
				.createdOn(likes.getCreatedOn())
				.updatedBy(likes.getUpdatedBy())
				.updatedOn(likes.getUpdatedOn())
				.build();
	}


	@Scheduled(fixedRate = 1 * 60 * 1000) // every 1 minute
	public void sendBatchLikeNotifications() {
		List<Likes> unnotifiedLikes = likeRepository.findByStatusTrueAndNotifiedFalse();

		Map<Integer, List<Likes>> likesByPost = unnotifiedLikes.stream()
				.filter(like -> like.getPostId() != null && like.getCommentId() == null) // Only post likes
				.collect(Collectors.groupingBy(Likes::getPostId));

		Map<Integer, List<Likes>> likesByComment = unnotifiedLikes.stream()
				.filter(like -> like.getCommentId() != null) // All comment likes
				.collect(Collectors.groupingBy(Likes::getCommentId));

		processPostLikeNotifications(likesByPost);
		processCommentLikeNotifications(likesByComment);
	}

	private void processPostLikeNotifications(Map<Integer, List<Likes>> likesByPost) {
		for (Map.Entry<Integer, List<Likes>> entry : likesByPost.entrySet()) {
			Integer postId = entry.getKey();
			List<Likes> likes = entry.getValue();

			Posts post = postsRepository.findById(postId).orElse(null);
			if (post == null) continue;

			Integer postOwnerId = post.getUser().getUserId();
			List<Likes> validLikes = likes.stream()
					.filter(like -> !like.getLikedBy().equals(postOwnerId))
					.collect(Collectors.toList());

			if (validLikes.isEmpty()) continue;

			sendBatchNotification(validLikes, postOwnerId, "Someone Liked Your Post", "Like", postId, post.getPostId());
		}
	}

	private void processCommentLikeNotifications(Map<Integer, List<Likes>> likesByComment) {
		for (Map.Entry<Integer, List<Likes>> entry : likesByComment.entrySet()) {
			Integer commentId = entry.getKey();
			List<Likes> likes = entry.getValue();

			Comment comment = commentRepository.findById(commentId).orElse(null);
			if (comment == null) continue;

			Integer commentOwnerId = comment.getCommentedBy();
			List<Likes> validLikes = likes.stream()
					.filter(like -> !like.getLikedBy().equals(commentOwnerId))
					.collect(Collectors.toList());

			if (validLikes.isEmpty()) continue;

			sendBatchNotification(validLikes, commentOwnerId, "Someone Liked Your Comment", "CommentLike", commentId, comment.getPost().getPostId());
		}
	}

	private void sendBatchNotification(List<Likes> validLikes, Integer receiverId, String title, String type, Integer refId, String postId) {
		List<User> likers = userRepository.findAllById(
				validLikes.stream().map(Likes::getLikedBy).distinct().collect(Collectors.toList())
				);

		if (likers.isEmpty()) return;

		String message;
		Integer senderId = likers.get(0).getUserId();
		Integer senderId2 = null;

		if (likers.size() == 1) {
			message =  " liked your " + (type.equals("Like") ? "post" : "comment");
		} else if (likers.size() == 2) {
			senderId2 = likers.get(1).getUserId();
			message = " liked your " + (type.equals("Like") ? "post" : "comment");
		} else {
			message = " and " + (likers.size() - 2) + " others liked your " + (type.equals("Like") ? "post" : "comment");
			senderId2 = likers.get(1).getUserId();
		}

		sendLikeNotificationWithOptionalSecondSender(receiverId, senderId, senderId2, title, message, type, refId, postId);


		validLikes.forEach(like -> {
			like.setNotified(true);
			like.setUpdatedOn(new Date());
		});
		logger.info("SenderId1: {}, SenderId2: {}", senderId, senderId2);

		likeRepository.saveAll(validLikes);
	}

	private void sendLikeNotificationWithOptionalSecondSender(
			Integer receiverId,
			Integer senderId,
			Integer senderId2,
			String title,
			String messageBody,
			String userType,
			Integer refId, String postId
			) {
		try {
			Optional<User> senderOpt = userRepository.findById(senderId);
			Optional<User> receiverOpt = userRepository.findById(receiverId);

			if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
				logger.warn("Sender or Receiver not found. Notification not sent.");
				return;
			}

			User sender = senderOpt.get();
			User receiver = receiverOpt.get();

			InAppNotification inAppNotification = InAppNotification.builder()
					.senderId(senderId)
					.senderId2(senderId2)
					.receiverId(receiverId)
					.title(title)
					.message(messageBody)
					.userType(userType)
					.id(refId)
					.postId(postId)
					.adminReview(sender.getAdminReview())
					.Profession(sender.getUserType())
					.isRead(false)
					.isDeleted(false)
					.createdBy(senderId)
					.createdOn(new Date())
					.build();

			inAppNotificationRepository.save(inAppNotification);
			logger.info(" In-app notification saved for receiver ID: {}", receiverId);

			String deviceToken = receiver.getFirebaseDeviceToken();

			if (deviceToken != null && !deviceToken.trim().isEmpty()) {
				try {


					String bodyText;
					if (senderId2 != null) {
						Optional<User> sender2Opt = userRepository.findById(senderId2);
						String sender2Name = sender2Opt.map(User::getName).orElse("Someone");
						bodyText = sender.getName() + " & " + sender2Name + " " + messageBody;
					} else {
						bodyText = sender.getName() + " " + messageBody;
					}

					// FCM Notification
					Notification notificationData = Notification.builder()
						//	.setTitle(title)
							.setBody(bodyText)
							.build();

					// Android Config
					AndroidNotification androidNotification = AndroidNotification.builder()
							.setIcon("ic_notification")
							.setColor("#00A2E8")
							.build();

					AndroidConfig androidConfig = AndroidConfig.builder()
							.setNotification(androidNotification)
							.build();

					Message firebaseMessage = Message.builder()
							.setNotification(notificationData)
							.setAndroidConfig(androidConfig)
							.putData("type", userType)
							.putData("refId", String.valueOf(refId))
							.putData("senderId", String.valueOf(senderId))
							.putData("postId", postId)        
							.putData("receiverId", String.valueOf(receiverId))
							.setToken(deviceToken)
							.build();

					String firebaseResponse = FirebaseMessaging.getInstance().send(firebaseMessage);
					logger.info("Push notification sent successfully: {}", firebaseResponse);

				} catch (FirebaseMessagingException e) {
					logger.error(" Firebase push notification failed: {}", e.getMessage(), e);
				}
			} else {
				logger.warn("No Firebase token available for receiver ID: {}", receiverId);
			}

		} catch (Exception e) {
			logger.error("Exception in sendNotification(): {}", e.getMessage(), e);
		}
	}


	@Override
	public CommentOutputWebModel addComment(CommentInputWebModel commentInputWebModel) {
		try {
			Posts post = postsRepository.findById(commentInputWebModel.getPostId()).orElse(null);
			if (post != null) {
				// Create and save new comment or reply
				Comment comment = Comment.builder()
						.category(commentInputWebModel.getCategory())
						.postId(post.getId())
						.parentCommentId(commentInputWebModel.getParentCommentId())
						.content(commentInputWebModel.getContent())
						.commentedBy(commentInputWebModel.getUserId())
						.status(true)
						.likesCount(0)
						.createdBy(commentInputWebModel.getUserId())
						.createdOn(new Date())
						.build();

				Comment savedComment = commentRepository.save(comment);

				// Always update post comment count (for both direct comments and replies)
				int newPostCommentCount = !Utility.isNullOrZero(post.getCommentsCount())
						? post.getCommentsCount() + 1
								: 1;
				post.setCommentsCount(newPostCommentCount);
				postsRepository.saveAndFlush(post);

				boolean isReply = COMMENT.equalsIgnoreCase(commentInputWebModel.getCategory());
				Integer parentCommentId = commentInputWebModel.getParentCommentId();

				if (isReply && parentCommentId != null) {
					Comment parent = commentRepository.findById(parentCommentId).orElse(null);
					if (parent != null) {
						parent.setReplyCount((parent.getReplyCount() == null ? 0 : parent.getReplyCount()) + 1);
						commentRepository.saveAndFlush(parent);

						// Notify parent comment owner (if not replying to self!)
						if (!parent.getCommentedBy().equals(commentInputWebModel.getUserId())) {
							User sender = userRepository.findById(commentInputWebModel.getUserId()).orElse(null);
							String senderName = sender != null ? sender.getName() : "Someone";
							sendNotification(
									parent.getCommentedBy(),            
									commentInputWebModel.getUserId(),                
									"Reply to Your Comment",
									" replied to your comment.",
									"COMMENT_REPLY",
									comment.getCommentId(), 
									post.getPostId()// Use commentId for context
									);
						}
					}
				}

				// 4. Always notify post owner if commenter is not the post owner
				if (!post.getCreatedBy().equals(commentInputWebModel.getUserId())) {
					User commenter = userRepository.findById(commentInputWebModel.getUserId()).orElse(null);
					String commenterName = commenter != null ? commenter.getName() : "Someone";
					sendNotification(
							post.getCreatedBy(),                        
							commentInputWebModel.getUserId(),                        
							"New Comment on Your Post",
							" commented on your post.",
							"POST_COMMENT",
							comment.getCommentId(),
							post.getPostId()
							);
				}

				logger.info("Post owner: {}, Commented by: {}", post.getCreatedBy(), commentInputWebModel.getUserId());
				logger.info("Comment added to post [{}]", post.getId());
				return this.transformCommentData(List.of(savedComment), post.getCommentsCount()).get(0);

			}} catch (Exception e) {
				logger.error("Error at addComment() -> {}", e.getMessage(), e);
			}

		return null;
	}




	public void sendNotification(Integer receiverId, Integer senderId, String title, String messageBody, String userType, Integer refId, String postId) {
		try {
			// Step 1: Validate users
			Optional<User> senderOpt = userRepository.findById(senderId);
			Optional<User> receiverOpt = userRepository.findById(receiverId);


			if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
				logger.warn("❗ Sender or Receiver not found. Notification not sent.");
				return;
			}

			User sender = senderOpt.get();
			User receiver = receiverOpt.get();

			// Step 2: Save In-App Notification
			InAppNotification inAppNotification = InAppNotification.builder()
					.senderId(senderId)
					.receiverId(receiverId)
					.title(title)
					.message(messageBody)
					.userType(userType)               
					.id(refId)   
					.postId(postId)
					.adminReview(sender.getAdminReview())
					.Profession(sender.getUserType()) 
					.isRead(false)
					.isDeleted(false)
					.createdBy(senderId)
					.createdOn(new Date())
					.build();

			inAppNotificationRepository.save(inAppNotification);
			logger.info("In-app notification saved for receiver ID: {}", receiverId);

			// Step 3: Send Push Notification via Firebase
			String deviceToken = receiver.getFirebaseDeviceToken();



			if (deviceToken != null && !deviceToken.trim().isEmpty()) {
				try {
					String bodyText = sender.getName() + " " + messageBody;
					// Create the notification payload
					Notification notification = Notification.builder()
							.setTitle(bodyText)
							//	                        .setBody(bodyText)
							.build();

					// Android-specific settings
					AndroidNotification androidNotification = AndroidNotification.builder()
							.setIcon("ic_notification") // matches Android app drawable
							.setColor("#00A2E8") // optional tint
							.build();

					AndroidConfig androidConfig = AndroidConfig.builder()
							.setNotification(androidNotification)
							.build();
					Message firebaseMessage = Message.builder()
							.setNotification(notification)
							.putData("type", userType)
							.putData("postId", postId)
							.putData("senderId", String.valueOf(senderId))
							.putData("receiverId", String.valueOf(receiverId))
							.putData("postId", postId)
							.setAndroidConfig(androidConfig)
							.setToken(deviceToken)
							.build();

					String firebaseResponse = FirebaseMessaging.getInstance().send(firebaseMessage);
					logger.info("Push notification sent successfully: {}", firebaseResponse);

				} catch (FirebaseMessagingException e) {
					logger.error("Firebase push notification failed: {}", e.getMessage(), e);
				}
			} else {
				logger.warn("No Firebase token available for receiver ID: {}", receiverId);
			}

		} catch (Exception e) {
			logger.error("Exception in sendNotification(): {}", e.getMessage(), e);
		}
	}

	@Override
	public ResponseEntity<Response> getCommentById(Integer commentId) {
		try {

			Comment comment = commentRepository.findByCommentId(commentId)
					.orElseThrow(() -> new RuntimeException("Comment not found"));

			Posts post = postsRepository.findById(comment.getPostId()).orElse(null);

			int commentCount = (post != null && post.getCommentsCount() != null)
					? post.getCommentsCount()
							: 0;

			CommentOutputWebModel output = this.transformCommentData(List.of(comment), commentCount).get(0);

			return ResponseEntity.ok(new Response(1, "success", output));

		} catch (RuntimeException e) {

			logger.warn("Comment not found for ID: {}", commentId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new Response(-1, "fail", "Comment not found for ID: " + commentId));

		} catch (Exception e) {

			logger.error("Unexpected error in getCommentById(): {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "error", "Internal server error occurred"));
		}
	}

	@Override
	public CommentOutputWebModel deleteComment(CommentInputWebModel commentInputWebModel) {
		try {
			Posts post = postsRepository.findById(commentInputWebModel.getPostId()).orElse(null);
			if (post != null) {
				Comment comment = commentRepository.findById(commentInputWebModel.getCommentId()).orElse(null);

				if (comment != null && Boolean.TRUE.equals(comment.getStatus())) {
					// Soft delete the parent comment
					comment.setStatus(false);
					comment.setUpdatedBy(commentInputWebModel.getUserId());
					comment.setUpdatedOn(new Date());
					commentRepository.save(comment);

					// Handle child comments (soft delete them too)
					List<Comment> childComments = commentRepository.getChildComments(
							comment.getPost().getId(), comment.getCommentId());

					int childDeletedCount = 0;

					if (!Utility.isNullOrEmptyList(childComments)) {
						for (Comment child : childComments) {
							if (Boolean.TRUE.equals(child.getStatus())) {
								child.setStatus(false);
								child.setUpdatedBy(commentInputWebModel.getUserId());
								child.setUpdatedOn(new Date());
								commentRepository.save(child);
								childDeletedCount++;
							}
						}
					}

					// Optional: You can update the stored count (not required if using live count)
					int totalReduced = 1 + childDeletedCount;
					int currentStoredCount = post.getCommentsCount() != null ? post.getCommentsCount() : 0;
					post.setCommentsCount(Math.max(0, currentStoredCount - totalReduced));
					postsRepository.save(post);

					// ✅ Always recalculate current live count of active comments
					int activeCommentCount = commentRepository.countActiveCommentsByPostId(post.getId());

					logger.info("Updated comments count for post [{}] is [{}]", post.getId(), activeCommentCount);
					return this.transformCommentData(List.of(comment), activeCommentCount).get(0);
				} else {
					logger.warn("Comment not found or already deleted.");
				}
			}
		} catch (Exception e) {
			logger.error("Error at deleteComment() -> {}", e.getMessage(), e);
		}
		return null;
	}

	private List<CommentOutputWebModel> transformCommentData(List<Comment> commentData, Integer totalCommentCount) {
		List<CommentOutputWebModel> commentOutWebModelList = new ArrayList<>();
		if (!Utility.isNullOrEmptyList(commentData)) {
			commentData.stream().filter(Objects::nonNull).forEach(comment -> {
				User user = userService.getUser(comment.getCommentedBy()).orElse(null); // Fetch user information

				Date createdDate = comment.getCreatedOn(); // Convert Date to LocalDateTime
				LocalDateTime createdOn = LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault());
				String elapsedTime = CalendarUtil.calculateElapsedTime(createdOn); // Calculate elapsed time

				Posts post = postsRepository.findByPostId(comment.getPostId()).orElse(null);

				List<CommentOutputWebModel> childComments = null;
				List<Comment> dbChildComments = commentRepository.getChildComments(comment.getPostId(), comment.getCommentId());
				if (!Utility.isNullOrEmptyList(dbChildComments))
					childComments = this.transformCommentData(dbChildComments, 0);

				CommentOutputWebModel commentOutputWebModel = CommentOutputWebModel.builder()
						.commentId(comment.getCommentId())
						.category(comment.getCategory())
						.postId(comment.getPostId())// I want that postId userId want in post table
						.userId(comment.getCommentedBy())
						.parentCommentId(comment.getParentCommentId())
						.content(comment.getContent())
						.totalLikesCount(comment.getLikesCount())
						.totalCommentCount(totalCommentCount)
						.status(comment.getStatus())
						.userProfilePic(userService.getProfilePicUrl(comment.getCommentedBy()))
						.userName(user != null ? user.getName() : "")
						.time(elapsedTime)
						.postUserId(post.getUser().getUserId())
						.childComments(childComments)
						.createdBy(comment.getCreatedBy())
						.createdOn(comment.getCreatedOn())
						.updatedBy(comment.getUpdatedBy())
						.updatedOn(comment.getUpdatedOn())
						.build();
				commentOutWebModelList.add(commentOutputWebModel);
			});
		}
		return commentOutWebModelList;
	}

	@Override
	public List<CommentOutputWebModel> getComment(CommentInputWebModel commentInputWebModel) {
		try {
			Posts post = postsRepository.findById(commentInputWebModel.getPostId()).orElse(null);
			if (post != null) {	
				List<Comment> commentData = (List<Comment>) post.getCommentCollection();
				// Filter comments with status true
				List<Comment> filteredComments = commentData.stream().filter(comment -> comment.getStatus() != null && comment.getStatus().equals(true) && !Utility.isNullOrBlankWithTrim(comment.getCategory()) && comment.getCategory().equalsIgnoreCase(POST)).collect(Collectors.toList());
				return this.transformCommentData(filteredComments, post.getCommentsCount());
			}
		} catch (Exception e) {
			logger.error("Error at getComment() -> {}", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ShareWebModel addShare(ShareWebModel shareWebModel) {
		try {
			Posts post = postsRepository.findById(shareWebModel.getPostId()).orElse(null);
			if (post != null) {
				Share share = Share.builder()
						.sharedBy(shareWebModel.getUserId())
						.postId(post.getId())
						.status(true)
						.createdBy(shareWebModel.getUserId())
						.createdOn(new Date()).build();
				Share savedShare = shareRepository.saveAndFlush(share); // Save the updated like

				post.setSharesCount(!Utility.isNullOrZero(post.getSharesCount()) ? post.getSharesCount() + 1 : 1); // Increasing the share count in post's table
				postsRepository.saveAndFlush(post);

				logger.info("Shares count for post id [{}] is :- [{}]", post.getId(), post.getSharesCount());
				return this.transformShareData(savedShare, post.getSharesCount());
			}
		} catch (Exception e) {
			logger.error("Error at addShare() -> {}", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private ShareWebModel transformShareData(Share share, Integer currentTotalShareCount) {
		return ShareWebModel.builder()
				.shareId(share.getShareId())
				.userId(share.getSharedBy())
				.postId(share.getPostId())
				.totalSharesCount(currentTotalShareCount)
				.status(share.getStatus())
				.createdBy(share.getCreatedBy())
				.createdOn(share.getCreatedOn())
				.updatedBy(share.getUpdatedBy())
				.updatedOn(share.getUpdatedOn())
				.build();
	}

	@Override
	public LinkWebModel addLink(LinkWebModel linkWebModel) {
		Link link = Link.builder()
				.links(linkWebModel.getLinks())
				.status(true)
				.createdBy(linkWebModel.getUserId())
				.createdOn(new Date())
				.userId(linkWebModel.getUserId())
				.build();

		Link savedLink = linkRepository.save(link);

		linkWebModel.setLinkId(savedLink.getLinkId());
		linkWebModel.setCreatedOn(new Date());
		linkWebModel.setUpdatedOn(new Date());

		return linkWebModel;
	}

	@Override
	public List<PostWebModel> getPostsByUserIds(Integer userId) {
		try {
			List<Posts> postList = postsRepository.findByUsers(User.builder().userId(userId).build());
			return this.transformPostsDataToPostWebModel(postList);
		} catch (Exception e) {
			logger.error("Error at getPostsByUserIds() -> {}", e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public CommentOutputWebModel updateComment(CommentInputWebModel commentInputWebModel) {
		try {
			Posts post = postsRepository.findById(commentInputWebModel.getPostId()).orElse(null);
			if (post != null) {
				// Fetch the existing comment by ID
				Optional<Comment> existingCommentOptional = commentRepository.findById(commentInputWebModel.getCommentId());

				if (existingCommentOptional.isPresent()) {
					Comment existingComment = existingCommentOptional.get();

					// Update the content of the comment
					existingComment.setContent(commentInputWebModel.getContent());
					existingComment.setUpdatedOn(new Date());
					existingComment.setUpdatedBy(commentInputWebModel.getUserId());

					// Save the updated comment back to the repository
					Comment updatedComment = commentRepository.saveAndFlush(existingComment);
					return this.transformCommentData(List.of(updatedComment), post.getCommentsCount()).get(0);
				} else {
					// If the comment with the given ID is not found, log an error and return null or throw an exception
					logger.error("Comment with ID [{}] not found", commentInputWebModel.getCommentId());
					return null;
				}
			}
		} catch (Exception e) {
			logger.error("Error at updateComment() -> {}", e.getMessage());
			e.printStackTrace();
			return null;
		}
		return null;
	}

	// Helper method to transform Comment to CommentWebModel
	private CommentOutputWebModel transformToCommentWebModel(Comment comment) {
		return CommentOutputWebModel.builder()
				.commentId(comment.getCommentId())
				.category(comment.getCategory())
				.postId(comment.getPostId())
				.parentCommentId(comment.getParentCommentId())
				.userId(comment.getCommentedBy())
				.content(comment.getContent())
				.createdOn(comment.getCreatedOn())
				.updatedOn(comment.getUpdatedOn())
				.status(comment.getStatus())
				.build();
	}

	@Override
	public boolean deletePostByUserId(PostWebModel postWebModel) {
		try {
			// Find the post by its ID and user ID
			Optional<Posts> postData = postsRepository.findByIdAndUserId(postWebModel.getMediaFilesIds(), postWebModel.getUserId());
			if (postData.isPresent()) {
				Posts post = postData.get();

				// Delete associated media files
				mediaFilesService.deleteMediaFilesByUserIdAndCategoryAndRefIds(post.getUser().getUserId(), MediaFileCategory.Post, postWebModel.getMediaFilesIds());

				// Update post status to false
				post.setStatus(false);

				// Save the updated post
				postsRepository.save(post);
				return true;
			} else {
				return false; // Post not found
			}
		} catch (Exception e) {
			// Log the exception
			e.printStackTrace();
			return false;
		}

	}

	public PostView trackPostView(Integer postId, Integer userId) {
		Posts post = postsRepository.findById(postId)
				.orElseThrow(() -> new RuntimeException("Post not found"));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// ✅ Skip counting if the user is the owner of the post
		if (post.getUser().getUserId().equals(userId)) {
			// Optional: still save view timestamp for analytics
			Optional<PostView> selfView = postViewRepository.findByPostAndUser(post, user);
			PostView view = selfView.orElseGet(() ->
			PostView.builder()
			.post(post)
			.user(user)
			.build()
					);
			view.setLastViewedOn(LocalDateTime.now());
			return postViewRepository.save(view);
		}

		LocalDateTime now = LocalDateTime.now();

		Optional<PostView> existing = postViewRepository.findByPostAndUser(post, user);

		boolean shouldIncrement = existing
				.map(view -> Duration.between(view.getLastViewedOn(), now).toHours() >= 24)
				.orElse(true);

		if (shouldIncrement) {
			PostView view = existing.orElseGet(() ->
			PostView.builder()
			.post(post)
			.user(user)
			.build()
					);
			view.setLastViewedOn(now);
			postViewRepository.save(view);

			post.setViewsCount(post.getViewsCount() + 1);
			postsRepository.save(post);

			return view;
		}

		return existing.orElseThrow();
	}

	@Override
	public Posts getTaggedPostById(Integer taggedId) {
		Optional<PostTags> postTagOptional = postTagsRepository.findById(taggedId);

		if (postTagOptional.isPresent()) {
			Integer postId = postTagOptional.get().getPostId();
			return postsRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("Post not found"));
		} else {
			throw new NoSuchElementException("Tag not found");
		}
	}



}