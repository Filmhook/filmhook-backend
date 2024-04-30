package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.annular.filmhook.model.MediaFileCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.User;
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
    public List<FileOutputWebModel> getAllUsersGalleryFiles() {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            outputWebModelList = mediaFilesService.getMediaFilesByCategory(MediaFileCategory.Gallery);
        } catch (Exception e) {
            logger.error("Error at getGalleryFilesByUser()...", e);
            e.printStackTrace();
        }
        return outputWebModelList;
    }
}
