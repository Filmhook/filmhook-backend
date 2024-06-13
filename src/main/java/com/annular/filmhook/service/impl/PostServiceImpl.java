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

import com.annular.filmhook.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Date;
import java.util.Optional;
import java.util.List;
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
import com.annular.filmhook.repository.UserRepository;
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
    UserRepository userRepository;

    @Autowired
    ShareRepository shareRepository;

    @Autowired
    PostTagsRepository postTagsRepository;

    @Value("${annular.app.url}")
    private String appUrl;

    @Autowired
    FriendRequestRepository friendRequestRepository;

    @Override
    public PostWebModel savePostsWithFiles(PostWebModel postWebModel) {
        try {
            User userFromDB = userService.getUser(postWebModel.getUserId()).orElse(null);
            if (userFromDB != null) {
                logger.info("User found: {}", userFromDB.getName());

                // Saving the Post details in the post-table
                Posts posts = Posts.builder()
                        .postId(UUID.randomUUID().toString())
                        .description(postWebModel.getDescription())
                        .user(userFromDB)
                        .status(true)
                        .privateOrPublic(postWebModel.getPrivateOrPublic())
                        .promoteFlag(false)
                        .promoteStatus(true)
                        .createdBy(postWebModel.getUserId())
                        .locationName(postWebModel.getLocationName())
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

                // Saving Tagged users (if anything with post)
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
            List<Posts> postList = postsRepository.findByUser(User.builder().userId(userId).build());
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
                        professionNames.add("CommonUser");
                    }

                    // Fetching the ProfilePic Path
                    List<FileOutputWebModel> userProfilePic = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.ProfilePic, post.getUser().getUserId());
                    String profilePicturePath = null;
                    if (!userProfilePic.isEmpty()) {
                        FileOutputWebModel profilePic = userProfilePic.get(0);
                        profilePicturePath = profilePic.getFilePath();
                    }

                    // Fetching the followers count for the user
                    List<FollowersRequest> followersList = friendRequestRepository.findByFollowersRequestReceiverIdAndFollowersRequestIsActive(post.getUser().getUserId(), true);

                    // Fetching the likes details
                    Integer userId = userDetails.userInfo().getId();
                    Optional<Likes> likesList = likeRepository.findByPostIdAndUserId(post.getId(), userId);
                    Boolean likeStatus = likesList.map(Likes::getStatus).orElse(false);

                    Optional<UserProfilePin> userData = pinProfileRepository.findByPinProfileIdAndUserId(userId, post.getUser().getUserId());
                    Boolean pinStatus = userData.map(UserProfilePin::isStatus).orElse(false);

                    Integer likeCount = post.getLikesCollection() != null ? (int) post.getLikesCollection().stream().filter(like -> like.getStatus().equals(true)).count() : 0;
                    Integer commentCount = post.getShareCollection() != null ? (int) post.getShareCollection().stream().filter(comment -> comment.getStatus().equals(true)).count() : 0;
                    Integer shareCount = post.getCommentCollection() != null ? (int) post.getCommentCollection().stream().filter(share -> share.getStatus().equals(true)).count() : 0;

                    List<Integer> taggedUsers = post.getPostTagsCollection() != null
                            ? post.getPostTagsCollection().stream()
                                    .filter(postTags -> postTags.getStatus().equals(true))
                                    .map(postTags -> postTags.getTaggedUser().getUserId())
                                    .collect(Collectors.toList())
                            : null;

                    // Preparing outputList
                    PostWebModel postWebModel = PostWebModel.builder()
                            .id(post.getId())
                            .userId(post.getUser().getUserId())
                            .userName(post.getUser().getName())
                            .postId(post.getPostId())
                            //.postUrl(this.generatePostUrl(post.getPostId()))
                            .userProfilePic(profilePicturePath)
                            .description(post.getDescription())
                            .pinStatus(pinStatus)
                            .likeCount(likeCount)
                            .shareCount(commentCount)
                            .commentCount(shareCount)
                            .promoteFlag(post.getPromoteFlag())
                            .postFiles(postFiles)
                            .likeStatus(likeStatus)
                            .privateOrPublic(post.getPrivateOrPublic())
                            .locationName(post.getLocationName())
                            .professionNames(professionNames)
                            .followersCount(followersList.size())
                            .createdOn(post.getCreatedOn())
                            .createdBy(post.getCreatedBy())
                            .taggedUsers(taggedUsers)
                            .build();
                    responseList.add(postWebModel);
                });
                responseList.sort(Comparator.comparing(PostWebModel::getCreatedOn).reversed());
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
    public List<PostWebModel> getAllUsersPosts() {
        try {
            List<Posts> postList = postsRepository.findAll();
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
            Posts existingPost = postsRepository.findById(likeWebModel.getPostId()).orElse(null);
            if (existingPost != null) {
                Likes likeRowToSaveOrUpdate = null;
                Likes existingLike = likeWebModel.getLikeId() != null ? likeRepository.findById(likeWebModel.getLikeId()).orElse(null) : null;

                if (existingLike != null) {
                    likeRowToSaveOrUpdate = existingLike;
                    likeRowToSaveOrUpdate.setStatus(!existingLike.getStatus());
                    likeRowToSaveOrUpdate.setUpdatedBy(likeWebModel.getUserId());
                    likeRowToSaveOrUpdate.setUpdatedOn(new Date());
                } else {
                    likeRowToSaveOrUpdate = Likes.builder()
                            .category(likeWebModel.getCategory())
                            .postId(existingPost.getId())
                            .commentId(likeWebModel.getCommentId())
                            .likedBy(likeWebModel.getUserId())
                            .liveDate(null)
                            .status(true)
                            .createdBy(likeWebModel.getUserId())
                            .createdOn(new Date())
                            .build();
                }
                Likes savedLikes = likeRepository.saveAndFlush(likeRowToSaveOrUpdate);
                existingPost.getLikesCollection().add(savedLikes); // Adding saved/updated likes into postLikesCollection

                Integer currentPostTotalLikes = existingPost.getLikesCollection() != null ? (int) existingPost.getLikesCollection().stream().filter(like -> like.getStatus().equals(true) && !Utility.isNullOrBlankWithTrim(like.getCategory()) && like.getCategory().equalsIgnoreCase("Post")).count() : 0;
                logger.info("Like count for post id [{}] is :- [{}]", existingPost.getId(), currentPostTotalLikes);
                return this.transformLikeData(likeRowToSaveOrUpdate, currentPostTotalLikes);
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
                        .createdBy(commentInputWebModel.getUserId())
                        .createdOn(new Date())
                        .build();
                Comment savedComment = commentRepository.save(comment);
                post.getCommentCollection().add(savedComment); // Adding saved/updated likes into postCommentsCollection
                Long currentTotalCommentCount = post.getCommentCollection() != null ? post.getCommentCollection().stream().filter(cmt -> cmt.getStatus().equals(true) && !Utility.isNullOrBlankWithTrim(cmt.getCategory()) && cmt.getCategory().equalsIgnoreCase("Post")).count() : 0;
                logger.info("Comments count for post id [{}] is :- [{}]", post.getId(), currentTotalCommentCount);
                return this.transformCommentData(List.of(savedComment), currentTotalCommentCount).get(0);
            }
        } catch (Exception e) {
            logger.error("Error at addComment() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private List<CommentOutputWebModel> transformCommentData(List<Comment> commentData, Long totalCommentCount) {
        List<CommentOutputWebModel> commentOutWebModelList = new ArrayList<>();
        if (!Utility.isNullOrEmptyList(commentData)) {
            commentData.stream().filter(Objects::nonNull).forEach(comment -> {
                // Fetch user information
                User user = userRepository.findById(comment.getCommentedBy()).orElse(null);
                String profilePicturePath = null;
                String username = null;
                if (user != null) {
                    username = user.getName();
                    List<FileOutputWebModel> userProfilePic = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.ProfilePic, user.getUserId());
                    if (!userProfilePic.isEmpty()) {
                        FileOutputWebModel profilePic = userProfilePic.get(0);
                        profilePicturePath = profilePic.getFilePath();
                    }
                }
                // Convert Date to LocalDateTime
                Date createdDate = comment.getCreatedOn();
                LocalDateTime createdOn = LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault());

                // Calculate elapsed time
                String elapsedTime = CalendarUtil.calculateElapsedTime(createdOn);

                List<CommentOutputWebModel> childComments = null;
                List<Comment> dbChildComments = commentRepository.getChildComments(comment.getPostId(), comment.getCommentId());
                if (!Utility.isNullOrEmptyList(dbChildComments)) childComments = this.transformCommentData(dbChildComments, 0L);

                Integer likeCount = likeRepository.getLikesForCommentByCommentId(comment.getPostId(), comment.getCommentId());

                CommentOutputWebModel commentOutputWebModel = CommentOutputWebModel.builder()
                        .commentId(comment.getCommentId())
                        .category(comment.getCategory())
                        .postId(comment.getPostId())
                        .userId(comment.getCommentedBy())
                        .parentCommentId(comment.getParentCommentId())
                        .content(comment.getContent())
                        .totalLikesCount(likeCount)
                        .totalCommentCount(totalCommentCount)
                        .status(comment.getStatus())
                        .createdBy(comment.getCreatedBy())
                        .createdOn(comment.getCreatedOn())
                        .userProfilePic(profilePicturePath)
                        .userName(username)
                        .time(elapsedTime)
                        .updatedBy(comment.getUpdatedBy())
                        .updatedOn(comment.getUpdatedOn())
                        .childComments(childComments)
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
                List<Comment> filteredComments = commentData.stream().filter(comment -> comment.getStatus() != null && comment.getStatus().equals(true) && !Utility.isNullOrBlankWithTrim(comment.getCategory()) && comment.getCategory().equalsIgnoreCase("Post")).collect(Collectors.toList());
                Long currentTotalCommentCount = (long) filteredComments.size();
                return this.transformCommentData(filteredComments, currentTotalCommentCount);
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
                    Comment deletedComment = commentRepository.saveAndFlush(comment);
                    post.getCommentCollection().removeIf(val -> val.getCommentId().equals(comment.getCommentId())); // removing saved/updated likes into postCommentsCollection
                    Long currentTotalCommentCount = post.getCommentCollection() != null ? post.getCommentCollection().stream().filter(cmt -> cmt.getStatus().equals(true) && !Utility.isNullOrBlankWithTrim(cmt.getCategory()) && cmt.getCategory().equalsIgnoreCase("Post")).count() : 0;
                    logger.info("Comments count :- [{}]", currentTotalCommentCount);
                    return this.transformCommentData(List.of(deletedComment), currentTotalCommentCount).get(0);
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
            Posts existingPost = postsRepository.findById(shareWebModel.getPostId()).orElse(null);
            if (existingPost != null) {
                Share share = Share.builder()
                        .sharedBy(shareWebModel.getUserId())
                        .postId(existingPost.getId())
                        .status(true)
                        .createdBy(shareWebModel.getUserId())
                        .createdOn(new Date()).build();
                Share savedShare = shareRepository.saveAndFlush(share); // Save the updated like
                existingPost.getShareCollection().add(savedShare); // Adding saved/updated likes into postShareCollection
                Integer currentTotalShareCount = existingPost.getShareCollection() != null ? (int) existingPost.getShareCollection().stream().filter(val -> val.getStatus().equals(true)).count() : 0;
                logger.info("Shares count for post id [{}] is :- [{}]", existingPost.getId(), currentTotalShareCount);
                return this.transformShareData(share, currentTotalShareCount);
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
            logger.error("Error at getPostsByUserId() -> {}", e.getMessage());
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

                    // Transform the updated comment to CommentWebModel and return it
                    //return this.transformToCommentWebModel(updatedComment);

                    Long currentTotalCommentCount = post.getCommentCollection() != null ? post.getCommentCollection().stream().filter(cmt -> cmt.getStatus().equals(true) && !Utility.isNullOrBlankWithTrim(cmt.getCategory()) && cmt.getCategory().equalsIgnoreCase("Post")).count() : 0;
                    return this.transformCommentData(List.of(updatedComment), currentTotalCommentCount).get(0);
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

}
