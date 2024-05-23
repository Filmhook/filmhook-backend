package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.*;
import com.annular.filmhook.webmodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.annular.filmhook.repository.*;

import com.annular.filmhook.service.PostService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;

import com.annular.filmhook.util.Utility;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.S3Util;

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
    
	@Autowired
	S3Util s3Util;

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
    public Resource getPostFile(Integer userId, String category, String fileId) {
        try {
            Optional<User> userFromDB = userService.getUser(userId);
            if (userFromDB.isPresent()) {
                String filePath = FileUtil.generateFilePath(userFromDB.get(), category, fileId);
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

                            int likeCount = likeRepository.countByMediaFileId(post.getId());
                            int commentCount = commentRepository.countByMediaFileId(post.getId());
                            int shareCount = shareRepository.countByMediaFileId(post.getId());
                            
                            List<FileOutputWebModel> userProfilePic = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.ProfilePic, post.getUser().getUserId());
                            String profilePicturePath = null;
                            if (!userProfilePic.isEmpty()) {
                                FileOutputWebModel profilePic = userProfilePic.get(0);
                                profilePicturePath = profilePic.getFilePath();
                            }



                            // Preparing outputList
                            PostWebModel postWebModel = PostWebModel.builder()
                                    .userId(post.getUser().getUserId())
                                    .userName(post.getUser().getName())
                                    .postId(post.getId())
                                    //.profileUrl(userProfilePic)
                                    .userProfilePic(profilePicturePath )
                                    .description(post.getDescription())
                                    .likeCount(likeCount)
                                    .shareCount(shareCount)
                                    .commentCount(commentCount)
                                    .promoteFlag(post.getPromoteFlag())
                                    .postFiles(postFiles)
                                    .privateOrPublic(post.getPrivateOrPublic())
                                    .locationName(post.getLocationName())
                                    .professionNames(professionNames)
                                    .build();
                            responseList.add(postWebModel);
                        });

                /*// Sort the response list based on promotedStatus first, then maintain original order
                Collections.sort(responseList, (a, b) -> {
                    Boolean promotedStatusA = (Boolean) a.get("promotedStatus");
                    Boolean promotedStatusB = (Boolean) b.get("promotedStatus");

                    // Handle null values
                    if (promotedStatusA == null && promotedStatusB == null) {
                        return 0; // Both values are null, maintain original order
                    } else if (promotedStatusA == null) {
                        return 1; // Null comes after non-null value
                    } else if (promotedStatusB == null) {
                        return -1; // Null comes before non-null value
                    } else {
                        // Sort by promotedStatus in descending order
                        return promotedStatusB.compareTo(promotedStatusA);
                    }
                });*/
                Collections.sort(responseList, Comparator.comparing(PostWebModel::getPromoteFlag).reversed());
            }
        } catch (Exception e) {
            logger.error("Error at transformPostsDataToPostWebModel() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return responseList;
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

}
