package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.MediaFileCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.CommentRepository;
import com.annular.filmhook.repository.LikeRepository;
import com.annular.filmhook.repository.ShareRepository;
import com.annular.filmhook.service.GalleryService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
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
    UserService userService;

    @Autowired
    AwsS3ServiceImpl awsService;
    
	@Autowired
	LikeRepository likeRepository;
    
    @Autowired
	ShareRepository shareRepository;

	@Autowired
	CommentRepository commentRepository;


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
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        List<HashMap<String, Object>> response = new ArrayList<>();
        try {
            outputWebModelList = mediaFilesService.getMediaFilesByCategory(MediaFileCategory.Gallery);
            if (outputWebModelList != null && !outputWebModelList.isEmpty()) {
                logger.info("[{}] gallery files found...", outputWebModelList.size());
                for(FileOutputWebModel outputWebModel : outputWebModelList) {
                	int likeCount = likeRepository.getLikeCount(outputWebModel.getId());
                	int commentCount = commentRepository.getCommentCount(outputWebModel.getId());
                	int shareCount =  shareRepository.getShareCount(outputWebModel.getId());
            		
                	HashMap<String, Object> withCounts = new HashMap<String, Object>();
 
            		withCounts.put("FileInfo", outputWebModel);
            		withCounts.put("LikeCount", likeCount);
            		withCounts.put("CommentCount", commentCount);
            		withCounts.put("ShareCount", shareCount);
            		
            		response.add(withCounts);
                }
                
            } else {
                return new Response(-1, "No file(s) available for this user...", null);
            }
        } catch (Exception e) {
            logger.error("Error at getGalleryFilesByUser()...", e);
            e.printStackTrace();
        }
//        return outputWebModelList;
        return new Response(1, "Gallery file(s) found successfully...", response);
    }
}
