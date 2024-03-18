package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.User;
import com.annular.filmhook.service.GalleryService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class GalleryServiceImpl implements GalleryService {

    public static final Logger logger = LoggerFactory.getLogger(GalleryServiceImpl.class);

    @Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    FileUtil fileUtil;

    @Autowired
    UserService userService;

    @Override
    public FileOutputWebModel saveGalleryFiles(FileInputWebModel fileInput) {
        FileOutputWebModel fileOutputWebModel = null;
        try {
            Optional<User> userFromDB = userService.getUser(fileInput.getUserId());
            if(userFromDB.isPresent()) {
                // 1. Save media files in MySQL
                fileOutputWebModel = mediaFilesService.saveMediaFiles(fileInput, userFromDB.get());

                // 2. Upload into S3
                File file = File.createTempFile(fileOutputWebModel.getFileId(), null);
                FileUtil.convertMultiPartFileToFile(fileInput.getFile(), file);
                String response = fileUtil.uploadFile(file, fileOutputWebModel.getFilePath());
                if (response != null && response.equalsIgnoreCase("File Uploaded")) {
                    file.delete(); // deleting temp file
                }
            }
        } catch (Exception e) {
            logger.error("Error at saveGalleryFiles()...", e);
        }
        return fileOutputWebModel;
    }

    @Override
    public Resource getGalleryFile(Integer userId, String category, String fileId) {
        try {
            Optional<User> userFromDB = userService.getUser(userId);
            if(userFromDB.isPresent()) {
                String filePath = FileUtil.generateFilePath(userFromDB.get(), category, fileId);
                return new ByteArrayResource(fileUtil.downloadFile(filePath));
            }
        } catch (Exception e) {
            logger.error("Error at getGalleryFile()...", e);
        }
        return null;
    }

    @Override
    public List<FileOutputWebModel> getGalleryFilesByUser(Integer userId) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            outputWebModelList = mediaFilesService.getMediaFilesByUserAndCategory(userId, "Gallery");
        } catch (Exception e) {
            logger.error("Error at getGalleryFilesByUser()...", e);
        }
        return outputWebModelList;
    }


}
