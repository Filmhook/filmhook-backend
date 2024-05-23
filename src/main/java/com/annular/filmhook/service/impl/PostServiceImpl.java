package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.User;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.Likes;
import com.annular.filmhook.model.Comment;
import com.annular.filmhook.model.Share;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.FilmProfessionPermanentDetail;
import com.annular.filmhook.model.FollowersRequest;

import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.LikeWebModel;
import com.annular.filmhook.webmodel.CommentWebModel;
import com.annular.filmhook.webmodel.ShareWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Date;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import com.annular.filmhook.service.PostService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;

import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.FilmProfessionPermanentDetailRepository;
import com.annular.filmhook.repository.LikeRepository;
import com.annular.filmhook.repository.CommentRepository;
import com.annular.filmhook.repository.ShareRepository;
import com.annular.filmhook.repository.FriendRequestRepository;

import com.annular.filmhook.util.Utility;
import com.annular.filmhook.util.FileUtil;

import software.amazon.awssdk.services.s3.model.S3Object;

@Service
public class PostServiceImpl implements PostService {

    public static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    FileUtil fileUtil;

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
    CommentRepository commentRepository;

    @Autowired
    ShareRepository shareRepository;

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
                        .createdBy(postWebModel.getUserId())
                        .locationName(postWebModel.getLocationName())
                        .createdOn(new Date())
                        .build();
                Posts savedPost = postsRepository.saveAndFlush(posts);

                // Saving the Post files in the media_files table
                FileInputWebModel fileInputWebModel = FileInputWebModel.builder()
                        .userId(postWebModel.getUserId())
                        .category(MediaFileCategory.Post)
                        .categoryRefId(savedPost.getId())
                        .files(postWebModel.getFiles())
                        .build();
                mediaFilesService.saveMediaFiles(fileInputWebModel, userFromDB);

                return this.transformPostsDataToPostWebModel(List.of(savedPost)).get(0);
            }
        } catch (Exception e) {
            logger.error("Error at saveGalleryFiles()...", e);
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
            logger.error("Error at getGalleryFile()...", e);
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
        return transformPostsDataToPostWebModel(List.of(post)).get(0);
    }

    private List<PostWebModel> transformPostsDataToPostWebModel(List<Posts> postList) {
        List<PostWebModel> responseList = new ArrayList<>();
        try {
            if (!Utility.isNullOrEmptyList(postList)) {
                postList.stream()
                        .filter(Objects::nonNull)
                        .forEach(post -> {
                            // Fetching post-files
                            List<FileOutputWebModel> postFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Post, post.getId());

                            // Fetching the user Profession
                            Set<String> professionNames = null;
                            List<FilmProfessionPermanentDetail> professionPermanentDataList = filmProfessionPermanentDetailRepository.findByUserId(post.getUser().getUserId());
                            if (!Utility.isNullOrEmptyList(professionPermanentDataList)) {
                                professionNames = professionPermanentDataList.stream().map(FilmProfessionPermanentDetail::getProfessionName).collect(Collectors.toSet());
                            }

                            // Fetching the ProfilePic Path
                            List<FileOutputWebModel> userProfilePic = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.ProfilePic, post.getUser().getUserId());
                            String profilePicturePath = null;
                            if (!userProfilePic.isEmpty()) {
                                FileOutputWebModel profilePic = userProfilePic.get(0);
                                profilePicturePath = profilePic.getFilePath();
                            }

                            //Fetching the followers count for the user
                            List<FollowersRequest> followersList = friendRequestRepository.findByFollowersRequestReceiverIdAndFollowersRequestIsActive(post.getUser().getUserId(), true);

                            // Preparing outputList
                            PostWebModel postWebModel = PostWebModel.builder()
                                    .id(post.getId())
                                    .userId(post.getUser().getUserId())
                                    .userName(post.getUser().getName())
                                    .postId(post.getPostId())
                                    .postUrl(this.generatePostUrl(post.getPostId()))
                                    .userProfilePic(profilePicturePath)
                                    .description(post.getDescription())
                                    .likeCount(post.getLikesCollection() != null ? post.getLikesCollection().size() : 0)
                                    .shareCount(post.getShareCollection() != null ? post.getShareCollection().size() : 0)
                                    .commentCount(post.getCommentCollection() != null ? post.getCommentCollection().size() : 0)
                                    .promoteFlag(post.getPromoteFlag())
                                    .postFiles(postFiles)
                                    .privateOrPublic(post.getPrivateOrPublic())
                                    .locationName(post.getLocationName())
                                    .professionNames(professionNames)
                                    .followersCount(followersList.size())
                                    .build();
                            responseList.add(postWebModel);
                        });
                Collections.sort(responseList, Comparator.nullsLast(Comparator.comparing(PostWebModel::getPromoteFlag)).reversed());
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
            logger.error("Error at getGalleryFile()...", e);
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
            logger.error("Error at getGalleryFile()...", e);
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
            logger.error("Error at getGalleryFilesByUser()...", e);
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
                Likes existingLike = existingPost.getLikesCollection()
                        .stream()
                        .filter(obj -> Objects.nonNull(obj) && obj.getLikedBy().equals(likeWebModel.getUserId()))
                        .findFirst()
                        .orElse(null);

                if (existingLike != null) {
                    likeRowToSaveOrUpdate = existingLike;
                    likeRowToSaveOrUpdate.setStatus(!existingLike.getStatus());
                    likeRowToSaveOrUpdate.setUpdatedBy(likeWebModel.getUserId());
                    likeRowToSaveOrUpdate.setUpdatedOn(new Date());
                } else {
                    likeRowToSaveOrUpdate = Likes.builder()
                            .likedBy(likeWebModel.getUserId())
                            .postId(existingPost.getId())
                            .liveDate(null)
                            .status(true)
                            .createdBy(likeWebModel.getUserId())
                            .createdOn(new Date())
                            .build();
                }
                Likes savedLikes = likeRepository.saveAndFlush(likeRowToSaveOrUpdate);
                existingPost.getLikesCollection().add(savedLikes); // Adding saved/updated likes into postLikesCollection
                Integer currentPostTotalLikes = existingPost.getLikesCollection() != null ? (int) existingPost.getLikesCollection().stream().filter(Likes::getStatus).count() : 0;;
                logger.info("Like count for post id [{}] is :- [{}]",existingPost.getId(), currentPostTotalLikes);
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
                .userId(likes.getLikedBy())
                .postId(likes.getPostId())
                .likeId(likes.getLikeId())
                .totalLikesCount(totalCount)
                .status(likes.getStatus())
                .createdBy(likes.getCreatedBy())
                .createdOn(likes.getCreatedOn())
                .updatedBy(likes.getUpdatedBy())
                .updatedOn(likes.getUpdatedOn())
                .build();
    }

    @Override
    public CommentWebModel addComment(CommentWebModel commentWebModel) {
        try {
            Posts post = postsRepository.findById(commentWebModel.getPostId()).orElse(null);
            if (post != null) {
                Comment comment = Comment.builder()
                        .content(commentWebModel.getContent())
                        .commentedBy(commentWebModel.getUserId())
                        .postId(post.getId())
                        .status(true)
                        .build();
                Comment savedComment = commentRepository.save(comment);
                post.getCommentCollection().add(savedComment); // Adding saved/updated likes into postCommentsCollection
                Long currentTotalCommentCount = post.getCommentCollection() != null ? post.getCommentCollection().stream().filter(Comment::getStatus).count() : 0;
                logger.info("Comments count for post id [{}] is :- [{}]",post.getId(), currentTotalCommentCount);
                return this.transformCommentData(List.of(savedComment), currentTotalCommentCount).get(0);
            }
        } catch (Exception e) {
            logger.error("Error at addComment() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private List<CommentWebModel> transformCommentData(List<Comment> commentData, Long totalCommentCount) {
        List<CommentWebModel> commentWebModelList = new ArrayList<>();
        if (!Utility.isNullOrEmptyList(commentData)) {
            commentData.stream()
                    .filter(Objects::nonNull)
                    .forEach(comment -> {
                        CommentWebModel commentWebModel = CommentWebModel.builder()
                                .commentId(comment.getCommentId())
                                .postId(comment.getPostId())
                                .userId(comment.getCommentedBy())
                                .content(comment.getContent())
                                .totalCommentCount(totalCommentCount)
                                .status(comment.getStatus())
                                .createdBy(comment.getCreatedBy())
                                .createdOn(comment.getCreatedOn())
                                .updatedBy(comment.getUpdatedBy())
                                .updatedOn(comment.getUpdatedOn())
                                .build();
                        commentWebModelList.add(commentWebModel);
                    });
        }
        return commentWebModelList;
    }

    @Override
    public List<CommentWebModel> getComment(CommentWebModel commentWebModel) {
        try {
            Posts post = postsRepository.findById(commentWebModel.getPostId()).orElse(null);
            if (post != null) {
                List<Comment> commentData = post.getCommentCollection() != null ? (List<Comment>) post.getCommentCollection() : new ArrayList<>();
                Long currentTotalCommentCount = !commentData.isEmpty() ? commentData.stream().filter(Comment::getStatus).count() : 0;
                return this.transformCommentData(commentData, currentTotalCommentCount);
            }
        } catch (Exception e) {
            logger.error("Error at getComment() -> {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public CommentWebModel deleteComment(CommentWebModel commentWebModel) {
        try {
            Posts post = postsRepository.findById(commentWebModel.getPostId()).orElse(null);
            if (post != null) {
                Comment comment = commentRepository.findById(commentWebModel.getCommentId()).orElse(null);
                if (comment != null) {
                    comment.setStatus(false);
                    Comment deletedComment = commentRepository.saveAndFlush(comment);
                    post.getCommentCollection().removeIf(val -> val.getCommentId().equals(comment.getCommentId())); // removing saved/updated likes into postCommentsCollection
                    Long currentTotalCommentCount = post.getCommentCollection() != null ? post.getCommentCollection().stream().filter(Comment::getStatus).count() : 0;
                    logger.info("Comments count for post id [{}] is :- [{}]",post.getId(), currentTotalCommentCount);
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
                        .createdOn(new Date())
                        .build();
                Share savedShare = shareRepository.saveAndFlush(share); // Save the updated like
                existingPost.getShareCollection().add(savedShare); // Adding saved/updated likes into postShareCollection
                Integer currentTotalShareCount = existingPost.getShareCollection() != null ? (int) existingPost.getShareCollection().stream().filter(Share::getStatus).count() : 0;
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
}
