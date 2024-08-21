package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserProfilePin;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.Likes;
import com.annular.filmhook.model.Link;
import com.annular.filmhook.model.Comment;
import com.annular.filmhook.model.Share;
import com.annular.filmhook.model.PostTags;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.FilmProfessionPermanentDetail;
import com.annular.filmhook.model.FollowersRequest;
import com.annular.filmhook.util.CalendarUtil;
import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.LikeWebModel;
import com.annular.filmhook.webmodel.LinkWebModel;
import com.annular.filmhook.webmodel.CommentInputWebModel;
import com.annular.filmhook.webmodel.CommentOutputWebModel;
import com.annular.filmhook.webmodel.ShareWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.annular.filmhook.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.stream.Collectors;

import com.annular.filmhook.service.PostService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;

import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.FilmProfessionPermanentDetailRepository;
import com.annular.filmhook.repository.LikeRepository;
import com.annular.filmhook.repository.LinkRepository;
import com.annular.filmhook.repository.PinProfileRepository;
import com.annular.filmhook.repository.CommentRepository;
import com.annular.filmhook.repository.ShareRepository;
import com.annular.filmhook.repository.PostTagsRepository;
import com.annular.filmhook.repository.FriendRequestRepository;

import com.annular.filmhook.util.Utility;
import com.annular.filmhook.util.FileUtil;

import software.amazon.awssdk.services.s3.model.S3Object;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    UserDetails userDetails;

    public static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    FileUtil fileUtil;

    @Autowired
    PinProfileRepository pinProfileRepository;

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
    LinkRepository linkRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ShareRepository shareRepository;

    @Autowired
    PostTagsRepository postTagsRepository;

    @Value("${annular.app.url}")
    private String appUrl;

    @Autowired
    FriendRequestRepository friendRequestRepository;

    private static final String POST = "Post";
    private static final String COMMENT = "Comment";

    @Override
    public PostWebModel savePostsWithFiles(PostWebModel postWebModel) {
        try {
            User userFromDB = userService.getUser(postWebModel.getUserId()).orElse(null);
            if (userFromDB != null) {
                logger.info("User found: {}", userFromDB.getName());

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

    @Override
    public List<PostWebModel> getPostsByUserId(Integer userId) {
        try {
            List<Posts> postList = postsRepository.getUserPosts(User.builder().userId(userId).build());
            return this.transformPostsDataToPostWebModel(postList);
        } catch (Exception e) {
            logger.error("Error at getPostsByUserId() -> {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PostWebModel getPostByPostId(String postId) {
        Posts post = postsRepository.findByPostId(postId);
        List<PostWebModel> responseList = this.transformPostsDataToPostWebModel(List.of(post));
        return responseList.isEmpty() ? null : responseList.get(0);
    }

    private List<PostWebModel> transformPostsDataToPostWebModel(List<Posts> postList) {
        List<PostWebModel> responseList = new ArrayList<>();
        try {
            if (!Utility.isNullOrEmptyList(postList)) {

                postList.stream().filter(Objects::nonNull).forEach(post -> {
                    // Fetching post-files
                    List<FileOutputWebModel> postFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Post, post.getId());

                    // Fetching the user Profession
                    Set<String> professionNames = new HashSet<>();
                    List<FilmProfessionPermanentDetail> professionPermanentDataList = filmProfessionPermanentDetailRepository.getProfessionDataByUserId(post.getUser().getUserId());
                    if (!Utility.isNullOrEmptyList(professionPermanentDataList)) {
                        professionNames = professionPermanentDataList.stream().map(FilmProfessionPermanentDetail::getProfessionName).collect(Collectors.toSet());
                    } else {
                        professionNames.add("Public User");
                    }

                    // Fetching the followers count for the user
                    List<FollowersRequest> followersList = friendRequestRepository.findByFollowersRequestReceiverIdAndFollowersRequestIsActive(post.getUser().getUserId(), true);

                    // Fetching the like actions from current logged-in user
                    Integer loggedInUser = userDetails.userInfo().getId();
                    Optional<Likes> likesList = likeRepository.findByPostIdAndUserId(post.getId(), loggedInUser);
                    Boolean likeStatus = likesList.map(Likes::getStatus).orElse(false);
                    Integer latestLikeId = likesList.map(Likes::getLikeId).orElse(null);

                    Optional<UserProfilePin> userData = pinProfileRepository.findByPinProfileIdAndUserId(loggedInUser, post.getUser().getUserId());
                    Boolean pinStatus = userData.map(UserProfilePin::isStatus).orElse(false);

                    List<Map<String, Object>> taggedUsers = post.getPostTagsCollection() != null
                            ? post.getPostTagsCollection().stream()
                                    .filter(postTags -> postTags.getStatus().equals(true))
                                    .map(postTags -> {
                                        Map<String, Object> taggedUserDetails = new HashMap<>();
                                        Integer taggedUserId = postTags.getTaggedUser().getUserId();
                                        taggedUserDetails.put("userId", taggedUserId);

                                        // Fetch username and profile pic
                                        userService.getUser(taggedUserId).ifPresent(user -> {
                                            taggedUserDetails.put("username", user.getName());
                                            taggedUserDetails.put("userProfilePic", userService.getProfilePicUrl(taggedUserId));
                                        });

                                        return taggedUserDetails;
                                    })
                                    .collect(Collectors.toList())
                            : null;


                    Date createdDate = post.getCreatedOn(); // Convert Date to LocalDateTime
                    LocalDateTime createdOn = LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault());
                    String elapsedTime = CalendarUtil.calculateElapsedTime(createdOn); // Calculate elapsed time

                    // Preparing outputList
                    PostWebModel postWebModel = PostWebModel.builder()
                            .id(post.getId())
                            .userId(post.getUser().getUserId())
                            .userName(post.getUser().getName())
                            .postId(post.getPostId()) // Unique id of each post
                            //.postUrl(this.generatePostUrl(post.getPostId()))
                            .adminReview(post.getUser().getAdminReview())
                            .userProfilePic(userService.getProfilePicUrl(post.getUser().getUserId()))
                            .description(post.getDescription())
                            .pinStatus(pinStatus)
                            .userType(post.getUser().getUserType())
                            .likeCount(post.getLikesCount())
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
                            .build();
                    responseList.add(postWebModel);
                });
                //responseList.sort(Comparator.comparing(PostWebModel::getCreatedOn).reversed());
                responseList.sort(Comparator.nullsLast(Comparator.comparing(PostWebModel::getPromoteFlag).reversed()));
            }
        } catch (Exception e) {
            logger.error("Error at transformPostsDataToPostWebModel() -> {}", e.getMessage());
            e.printStackTrace();
        }
        logger.info("Final post count to respond :- [{}]", responseList.size());
        return responseList;
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

    @Override
    public List<PostWebModel> getAllUsersPosts(Integer pageNo, Integer pageSize) {
        try {
            Pageable paging = PageRequest.of(pageNo - 1, pageSize);
            //List<Posts> postList = postsRepository.findAll(paging).stream().filter(post -> post.getStatus().equals(true)).collect(Collectors.toList());
            List<Posts> postList = postsRepository.getAllActivePosts(paging);
            return this.transformPostsDataToPostWebModel(postList);
        } catch (Exception e) {
            logger.error("Error at getAllUsersPosts() -> {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public LikeWebModel addOrUpdateLike(LikeWebModel likeWebModel) {
        try {
            Likes likeRowToSaveOrUpdate;
            Posts post = postsRepository.findById(likeWebModel.getPostId()).orElse(null);
            if (post != null) {

                Likes existingLike;
                if (!Utility.isNullObject(likeWebModel.getLikeId())) {
                    existingLike = likeRepository.findById(likeWebModel.getLikeId()).orElse(null);
                } else {
                    existingLike = likeRepository.findByPostIdAndUserId(likeWebModel.getPostId(), likeWebModel.getUserId()).orElse(null);
                }
                Comment existingComment = likeWebModel.getCommentId() != null ? commentRepository.findById(likeWebModel.getCommentId()).orElse(null) : null;

                if (existingLike != null) {
                    likeRowToSaveOrUpdate = existingLike;
                    likeRowToSaveOrUpdate.setStatus(!existingLike.getStatus());
                    likeRowToSaveOrUpdate.setUpdatedBy(likeWebModel.getUserId());
                    likeRowToSaveOrUpdate.setUpdatedOn(new Date());
                } else {
                    likeRowToSaveOrUpdate = Likes.builder()
                            .category(likeWebModel.getCategory())
                            .postId(post.getId())
                            .commentId(likeWebModel.getCommentId())
                            .likedBy(likeWebModel.getUserId())
                            .liveDate(null)
                            .status(true)
                            .createdBy(likeWebModel.getUserId())
                            .createdOn(new Date())
                            .build();
                }
                Likes savedLike = likeRepository.saveAndFlush(likeRowToSaveOrUpdate);

                Integer totalLikes = 0;
                if (!Utility.isNullOrBlankWithTrim(likeWebModel.getCategory())) {
                    if (likeWebModel.getCategory().equalsIgnoreCase(POST)) {
                        if (likeRowToSaveOrUpdate.getStatus()) {
                            post.setLikesCount(!Utility.isNullOrZero(post.getLikesCount()) ? post.getLikesCount() + 1 : 1); // Increasing Post's like count
                        } else {
                            post.setLikesCount(!Utility.isNullOrZero(post.getLikesCount()) ? post.getLikesCount() - 1 : 0); // Decreasing Post's like count
                        }
                        postsRepository.saveAndFlush(post);
                        totalLikes = post.getLikesCount();
                    } else if (likeWebModel.getCategory().equalsIgnoreCase(COMMENT) && existingComment != null) {
                        if (likeRowToSaveOrUpdate.getStatus()) {
                            existingComment.setLikesCount(!Utility.isNullOrZero(existingComment.getLikesCount()) ? existingComment.getLikesCount() + 1 : 1); // Increasing Comment's like count
                        } else {
                            existingComment.setLikesCount(!Utility.isNullOrZero(existingComment.getLikesCount()) ? existingComment.getLikesCount() - 1 : 0); // Decreasing Comment's like count
                        }
                        commentRepository.saveAndFlush(existingComment);
                        totalLikes = existingComment.getLikesCount();
                    }
                }
                logger.info("Like count for post id [{}] is :- [{}]", post.getId(), totalLikes);
                return this.transformLikeData(savedLike, totalLikes);
            }
        } catch (Exception e) {
            logger.error("Error at addOrUpdateLike() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private LikeWebModel transformLikeData(Likes likes, Integer totalCount) {
        return LikeWebModel.builder()
                .likeId(likes.getLikeId())
                .category(likes.getCategory())
                .postId(likes.getPostId())
                .commentId(likes.getCommentId())
                .userId(likes.getLikedBy())
                .totalLikesCount(totalCount)
                .status(likes.getStatus())
                .createdBy(likes.getCreatedBy())
                .createdOn(likes.getCreatedOn())
                .updatedBy(likes.getUpdatedBy())
                .updatedOn(likes.getUpdatedOn())
                .build();
    }

    @Override
    public CommentOutputWebModel addComment(CommentInputWebModel commentInputWebModel) {
        try {
            Posts post = postsRepository.findById(commentInputWebModel.getPostId()).orElse(null);
            if (post != null) {
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

                if (!Utility.isNullOrBlankWithTrim(commentInputWebModel.getCategory()) && commentInputWebModel.getCategory().equalsIgnoreCase(POST)) {
                    post.setCommentsCount(!Utility.isNullOrZero(post.getCommentsCount()) ? post.getCommentsCount() + 1 : 1); // Increasing the comments count in post's table
                    postsRepository.saveAndFlush(post);
                }

                logger.info("Comments count for post id [{}] is :- [{}]", post.getId(), post.getCommentsCount());
                return this.transformCommentData(List.of(savedComment), post.getCommentsCount()).get(0);
            }
        } catch (Exception e) {
            logger.error("Error at addComment() -> {}", e.getMessage());
            e.printStackTrace();
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
    public CommentOutputWebModel deleteComment(CommentInputWebModel commentInputWebModel) {
        try {
            Posts post = postsRepository.findById(commentInputWebModel.getPostId()).orElse(null);
            if (post != null) {
                Comment comment = commentRepository.findById(commentInputWebModel.getCommentId()).orElse(null);
                if (comment != null) {
                    comment.setStatus(false);
                    comment.setUpdatedBy(commentInputWebModel.getUserId());
                    comment.setUpdatedOn(new Date());
                    Comment deletedComment = commentRepository.saveAndFlush(comment);

                    if (!Utility.isNullOrBlankWithTrim(commentInputWebModel.getCategory()) && commentInputWebModel.getCategory().equalsIgnoreCase(POST)) {
                        post.setCommentsCount(!Utility.isNullOrZero(post.getCommentsCount()) ? post.getCommentsCount() - 1 : 0); // Decreasing the comments count from post's table
                        postsRepository.saveAndFlush(post);
                    }

                    logger.info("Comments count :- [{}]", post.getCommentsCount());
                    return this.transformCommentData(List.of(deletedComment), post.getCommentsCount()).get(0);
                }
            }
        } catch (Exception e) {
            logger.error("Error at deleteComment() -> {}", e.getMessage());
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
			Optional<Posts> postData = postsRepository.findByIdAndUserId(postWebModel.getMediaFilesIds(),
					postWebModel.getUserId());
			if (postData.isPresent()) {
				Posts post = postData.get();

				// Delete associated media files
				mediaFilesService.deleteMediaFilesByUserIdAndCategoryAndRefIds(post.getUser().getUserId(),
						MediaFileCategory.Post, postWebModel.getMediaFilesIds());

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


}
