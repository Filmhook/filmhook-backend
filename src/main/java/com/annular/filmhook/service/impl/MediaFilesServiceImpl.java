package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MediaFilesServiceImpl implements MediaFilesService {

    public static final Logger logger = LoggerFactory.getLogger(MediaFilesServiceImpl.class);

    @Autowired
    MediaFilesRepository mediaFilesRepository;

    @Autowired
    FileUtil fileUtil;

    @Autowired
    UserService userService;

    @Override
    public FileOutputWebModel saveMediaFiles(FileInputWebModel fileInputWebModel, User user) {
        FileOutputWebModel fileOutputWebModel = null;
        try {
            // 1. Save first in MySQL
            MediaFiles mediaFiles = this.prepareMediaFileData(fileInputWebModel, user);
            mediaFilesRepository.save(mediaFiles);
            logger.info("File id saved in mysql :- " + mediaFiles.getFileId());

            // 2. Upload into S3
            File file = File.createTempFile(mediaFiles.getFileId(), null);
            FileUtil.convertMultiPartFileToFile(fileInputWebModel.getFile(), file);
            String response = fileUtil.uploadFile(file, mediaFiles.getFilePath());
            if (response != null && response.equalsIgnoreCase("File Uploaded")) {
                file.delete(); // deleting temp file
                fileOutputWebModel = this.transformData(mediaFiles); // Reading the saved file details
            }
        } catch (Exception e) {
            logger.error("Error at saveGalleryFiles()...", e);
        }
        return fileOutputWebModel;
    }

    private MediaFiles prepareMediaFileData(FileInputWebModel fileInput, User user) {
        MultipartFile file = fileInput.getFile();

        MediaFiles mediaFiles = new MediaFiles();
        mediaFiles.setUser(user);
        mediaFiles.setCategory(fileInput.getCategory());
        mediaFiles.setFileId(UUID.randomUUID().toString());
        mediaFiles.setFileName(file.getOriginalFilename());
        mediaFiles.setFilePath(FileUtil.generateFilePath(mediaFiles.getUser(), fileInput.getCategory(), mediaFiles.getFileId()));
        mediaFiles.setFileType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
        mediaFiles.setFileSize(file.getSize());
        mediaFiles.setStatus(true);
        mediaFiles.setCreatedBy(user.getUserId());
        mediaFiles.setCreatedOn(new Date());
        //mediaFiles.setUpdatedBy(fileInput.getUserId());
        //mediaFiles.setUpdatedOn(new Date());

        return mediaFiles;
    }

    @Override
    public List<FileOutputWebModel> getMediaFilesByUser(Integer userId) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByUserId(userId);
            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error at getGalleryFilesByUser()...", e);
        }
        return outputWebModelList;
    }

    @Override
    public List<FileOutputWebModel> getMediaFilesByUserAndCategory(Integer userId, String category) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByUserIdAndCategory(userId, category);
            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error at getGalleryFilesByUser()...", e);
        }
        return outputWebModelList;
    }

    private FileOutputWebModel transformData(MediaFiles mediaFile) {
        FileOutputWebModel fileOutputWebModel = null;
        try {
            fileOutputWebModel = new FileOutputWebModel();

            fileOutputWebModel.setUserId(mediaFile.getUser().getUserId());
            fileOutputWebModel.setCategory(mediaFile.getCategory());

            fileOutputWebModel.setFileId(mediaFile.getFileId());
            fileOutputWebModel.setFileName(mediaFile.getFileName());
            fileOutputWebModel.setFileType(mediaFile.getFileType());
            fileOutputWebModel.setFileSize(mediaFile.getFileSize());
            fileOutputWebModel.setFilePath(mediaFile.getFilePath());

            fileOutputWebModel.setCreatedBy(mediaFile.getCreatedBy());
            fileOutputWebModel.setCreatedOn(mediaFile.getCreatedOn());
            fileOutputWebModel.setUpdatedBy(mediaFile.getUpdatedBy());
            fileOutputWebModel.setUpdatedOn(mediaFile.getUpdatedOn());

            return fileOutputWebModel;
        } catch (Exception e) {
            logger.error("Error at transformData()...", e);
        }
        return fileOutputWebModel;
    }

    @Override
    public void deleteMediaFilesByUserIdAndCategory(Integer userId, String category) {
        Optional<User> user = userService.getUser(userId);
        if (user.isPresent()) {
            // Deleting th S3 Objects
            List<FileOutputWebModel> fileOutputWebModelList = this.getMediaFilesByUserAndCategory(userId, category);
            if (fileOutputWebModelList != null && !fileOutputWebModelList.isEmpty()) {
                String destinationPath = FileUtil.generateDestinationPath(user.get(), category);
                fileOutputWebModelList.forEach(item -> fileUtil.deleteFile(destinationPath, item.getFileId()));
            }
        }
    }
}