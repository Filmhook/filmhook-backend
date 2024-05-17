package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.PlatformPermanentDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.CommentRepository;
import com.annular.filmhook.repository.LikeRepository;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.PlatformPermanentDetailRepository;
import com.annular.filmhook.repository.ShareRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.GalleryService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

import software.amazon.awssdk.services.s3.model.S3Object;

@Service
public class GalleryServiceImpl implements GalleryService {

    public static final Logger logger = LoggerFactory.getLogger(GalleryServiceImpl.class);

    @Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    FileUtil fileUtil;
    
	@Autowired
	S3Util s3Util;

    @Autowired
    UserService userService;

    @Autowired
    AwsS3ServiceImpl awsService;
    
	@Autowired
	LikeRepository likeRepository;
	
	@Autowired
	PlatformPermanentDetailRepository platformPermanentDetailRepository;
    
	@Autowired
	MediaFilesRepository mediaFilesRepository;
	
    @Autowired
	ShareRepository shareRepository;

	@Autowired
	CommentRepository commentRepository;
	
	@Autowired
	UserRepository userRepository;


    @Override
    public List<FileOutputWebModel> saveGalleryFiles(FileInputWebModel fileInput) {
        try {
            Optional<User> userFromDB = userService.getUser(fileInput.getUserId());
            if (userFromDB.isPresent()) {
                logger.info("User found: {}", userFromDB.get().getName());
                // 1. Save media files in MySQL
                fileInput.setCategory(MediaFileCategory.Gallery);
                return mediaFilesService.saveMediaFiles(fileInput, userFromDB.get());
            }
        } catch (Exception e) {
            logger.error("Error at saveGalleryFiles()...", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Resource getGalleryFile(Integer userId, String category, String fileId) {
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
    public List<FileOutputWebModel> getGalleryFilesByUser(Integer userId) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            outputWebModelList = mediaFilesService.getMediaFilesByCategoryAndUserId(MediaFileCategory.Gallery, userId);
        } catch (Exception e) {
            logger.error("Error at getGalleryFilesByUser()...", e);
            e.printStackTrace();
        }
        return outputWebModelList;
    }

    @Override
    public Resource getAllGalleryFilesInCategory(Integer userId, String category) {
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
    public Resource getAllGalleryFilesInCategory(String category) {
        try {
            ///Optional<User> userFromDB = userService.getUser(userId);
            //if (userFromDB.isPresent()) {
            String destinationPath = FileUtil.generateDestinationPath(category);
            List<S3Object> s3data = awsService.getAllObjectsByBucketAndDestination("filmhook-dev-bucket", destinationPath);
            return new ByteArrayResource(fileUtil.downloadFile(s3data));
            //}
        } catch (Exception e) {
            logger.error("Error at getGalleryFile()...", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response getAllUsersGalleryFiles() {
        List<HashMap<String, Object>> response = new ArrayList<>();
        try {
            List<FileOutputWebModel> outputWebModelList = mediaFilesService.getMediaFilesByCategory(MediaFileCategory.Gallery);
            if (!outputWebModelList.isEmpty()) {
                logger.info("[{}] gallery files found...", outputWebModelList.size());
                for (FileOutputWebModel outputWebModel : outputWebModelList) {
                    int likeCount = likeRepository.getLikeCount(outputWebModel.getId());
                    int commentCount = commentRepository.getCommentCount(outputWebModel.getId());
                    int shareCount = shareRepository.getShareCount(outputWebModel.getId());
                    int userId = outputWebModel.getUserId();
                    MediaFileCategory profilePicCategory = MediaFileCategory.ProfilePic;
                    List<MediaFiles> mediaDataList = mediaFilesRepository.findByuserIdAndCategory(userId, profilePicCategory);
                    Optional<User> user = userRepository.findById(userId);
                    HashMap<String, Object> withCounts = new HashMap<>();
                    withCounts.put("FileInfo", outputWebModel);
                    withCounts.put("LikeCount", likeCount);
                    withCounts.put("CommentCount", commentCount);
                    withCounts.put("ShareCount", shareCount);
                    
                    if (!mediaDataList.isEmpty()) {
                        // Assuming you want to handle only the first result in the list
                        MediaFiles mediaFiles = mediaDataList.get(0);
                        withCounts.put("filePathProfile", mediaFiles.getFilePath());
                        withCounts.put("fileNameProfile", mediaFiles.getFileName());
                        withCounts.put("fileNameSize", mediaFiles.getFileSize());
                        withCounts.put("fileNameTypeProfile", mediaFiles.getFileType());
                        withCounts.put("profileUrl", s3Util.getS3BaseURL() + S3Util.S3_PATH_DELIMITER
                                + mediaFiles.getFilePath() + mediaFiles.getFileType());
                    } else {
                        // Handle the case where no profile picture is available
                        withCounts.put("filePathProfile", "No profile picture available");
                        withCounts.put("fileNameProfile", "");
                        withCounts.put("fileNameSize", "");
                        withCounts.put("fileNameTypeProfile", "");
                        withCounts.put("profileUrl", "");
                    }
                    withCounts.put("username", user != null ? user.get().getName() : "Unknown"); // Assuming getUsername() returns the username
                   
                    List<PlatformPermanentDetail> platformDetailList = platformPermanentDetailRepository.findByUserId(userId);
                    if (!platformDetailList.isEmpty()) {
                        Set<String> platformNames = new HashSet<>();
                        for (PlatformPermanentDetail platformDetail : platformDetailList) {
                            platformNames.add(platformDetail.getPlatformName());
                        }
                        withCounts.put("platformNames", platformNames);
                    } else {
                        withCounts.put("platformNames", "Unknown");
                    }


                    
                    response.add(withCounts);
                }
            } else {
                return new Response(-1, "No file(s) available for this user...", null);
            }
        } catch (Exception e) {
            logger.error("Error at getGalleryFilesByUser()...", e);
            e.printStackTrace();
        }
        return new Response(1, "Gallery file(s) found successfully...", response);
    }


}
