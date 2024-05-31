package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.MultiMediaFiles;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.MultiMediaFileRepository;
import com.annular.filmhook.repository.StoryRepository;
import com.annular.filmhook.service.AwsS3Service;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.CalendarUtil;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.FilmHookConstants;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MediaFilesServiceImpl implements MediaFilesService {

    public static final Logger logger = LoggerFactory.getLogger(MediaFilesServiceImpl.class);

    @Autowired
    MediaFilesRepository mediaFilesRepository;

    @Autowired
    StoryRepository storyRepository;

    @Autowired
    FileUtil fileUtil;

    @Autowired
    AwsS3Service awsS3Service;

    @Autowired
    UserService userService;

    @Autowired
    MultiMediaFileRepository multiMediaFilesRepository;

    @Autowired
    S3Util s3Util;

    @Override
    public List<FileOutputWebModel> saveMediaFiles(FileInputWebModel fileInputWebModel, User user) {
        List<FileOutputWebModel> fileOutputWebModelList = new ArrayList<>();
        try {
            // 1. Save first in MySQL
            Map<MediaFiles, MultipartFile> mediaFilesMap = this.prepareMultipleMediaFilesData(fileInputWebModel, user);
            logger.info("Saved MediaFiles rows list size :- [{}]", mediaFilesMap.size());

            // 2. Upload into S3
            mediaFilesMap.forEach((mediaFile, inputFile) -> {
                mediaFilesRepository.saveAndFlush(mediaFile);
                try {
                    File file = File.createTempFile(mediaFile.getFileId(), null);
                    FileUtil.convertMultiPartFileToFile(inputFile, file);
                    String response = fileUtil.uploadFile(file, mediaFile.getFilePath() + mediaFile.getFileType());
                    if (response != null && response.equalsIgnoreCase("File Uploaded")) {
                        file.delete(); // deleting temp file
                        fileOutputWebModelList.add(this.transformData(mediaFile)); // Reading the saved file details
                    }
                } catch (IOException e) {
                    logger.error("Error at saveMediaFiles()...", e);
                }
            });
            fileOutputWebModelList.sort(Comparator.comparing(FileOutputWebModel::getId));
        } catch (Exception e) {
            logger.error("Error at saveMediaFiles()...", e);
            e.printStackTrace();
        }
        return fileOutputWebModelList;
    }

    private Map<MediaFiles, MultipartFile> prepareMultipleMediaFilesData(FileInputWebModel fileInput, User user) {
        Map<MediaFiles, MultipartFile> mediaFilesMap = new HashMap<>();
        try {
            fileInput.getFiles().stream()
                    .filter(Objects::nonNull)
                    .forEach(file -> {
                        MediaFiles mediaFiles = new MediaFiles();
                        mediaFiles.setUser(user);
                        mediaFiles.setCategory(fileInput.getCategory());
                        mediaFiles.setCategoryRefId(fileInput.getCategoryRefId());
                        mediaFiles.setDescription(fileInput.getDescription());
                        mediaFiles.setFileId(UUID.randomUUID().toString());
                        mediaFiles.setFileName(file.getOriginalFilename());
                        mediaFiles.setFilePath(FileUtil.generateFilePath(mediaFiles.getUser(), fileInput.getCategory().toString(), mediaFiles.getFileId()));
                        mediaFiles.setFileType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
                        mediaFiles.setFileSize(file.getSize());
                        mediaFiles.setStatus(true);
                        mediaFiles.setCreatedBy(user.getUserId());
                        mediaFiles.setCreatedOn(new Date());
                        mediaFilesRepository.save(mediaFiles);

                        // Save multiMediaTable
                        try {
                            MultiMediaFiles multiMediaFiles = new MultiMediaFiles();
                            multiMediaFiles.setFileName(mediaFiles.getFileName());
                            multiMediaFiles.setFileOriginalName(file.getOriginalFilename());
                            multiMediaFiles.setFileDomainId(FilmHookConstants.GALLERY);
                            multiMediaFiles.setFileDomainReferenceId(mediaFiles.getId());
                            multiMediaFiles.setFileIsActive(true);
                            multiMediaFiles.setFileCreatedBy(user.getUserId());
                            multiMediaFiles.setFileSize(mediaFiles.getFileSize());
                            multiMediaFiles.setFileType(mediaFiles.getFileType());
                            multiMediaFiles = multiMediaFilesRepository.save(multiMediaFiles);
                            logger.info("MultiMediaFiles entity saved in the database with ID: {}", multiMediaFiles.getMultiMediaFileId());
                        } catch (Exception e) {
                            logger.error("Error saving MultiMediaFiles", e);
                        }
                        mediaFilesMap.put(mediaFiles, file);
                    });
        } catch (Exception e) {
            logger.error("Error occurred at prepareMultipleMediaFilesData() -> ", e);
            e.printStackTrace();
        }
        return mediaFilesMap;
    }

    @Override
    public List<FileOutputWebModel> getMediaFilesByUserId(Integer userId) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByUserId(userId);
            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error at getMediaFilesByUser()...", e);
            e.printStackTrace();
        }
        return outputWebModelList;
    }

    @Override
    public List<FileOutputWebModel> getMediaFilesByCategory(MediaFileCategory category) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByCategory(category);
            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error at getMediaFilesByUser()...", e);
            e.printStackTrace();
        }
        return outputWebModelList;
    }

    @Override
    public List<FileOutputWebModel> getMediaFilesByCategoryAndUserId(MediaFileCategory category, Integer userId) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByUserIdAndCategory(userId, category);
            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error at getMediaFilesByUserAndCategory()...", e);
            e.printStackTrace();
        }
        return outputWebModelList;
    }

    @Override
    public List<FileOutputWebModel> getMediaFilesByCategoryAndRefId(MediaFileCategory category, Integer refId) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByCategoryAndRefId(category, refId);
            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error at getMediaFilesByCategoryAndRefId()...", e);
            e.printStackTrace();
        }
        return outputWebModelList;
    }

    @Override
    public List<FileOutputWebModel> getMediaFilesByUserIdAndCategoryAndRefId(Integer userId, MediaFileCategory category, Integer refId) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByUserIdAndCategoryAndRefId(userId, category, refId);
            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error at getMediaFilesByUserIdAndCategoryAndRefId()...", e);
            e.printStackTrace();
        }
        return outputWebModelList;
    }


    private FileOutputWebModel transformData(MediaFiles mediaFile) {
        FileOutputWebModel fileOutputWebModel = null;
        try {
            fileOutputWebModel = new FileOutputWebModel();

            fileOutputWebModel.setId(mediaFile.getId());

            fileOutputWebModel.setUserId(mediaFile.getUser().getUserId());
            fileOutputWebModel.setCategory(mediaFile.getCategory().toString());
            fileOutputWebModel.setCategoryRefId(mediaFile.getCategoryRefId());

            fileOutputWebModel.setFileId(mediaFile.getFileId());
            fileOutputWebModel.setFileName(mediaFile.getFileName());
            fileOutputWebModel.setFileType(mediaFile.getFileType());
            fileOutputWebModel.setFileSize(mediaFile.getFileSize());
            fileOutputWebModel.setFilePath(s3Util.generateS3FilePath(mediaFile.getFilePath() + mediaFile.getFileType()));
            fileOutputWebModel.setDescription(mediaFile.getDescription());

            fileOutputWebModel.setCreatedBy(mediaFile.getCreatedBy());
            fileOutputWebModel.setCreatedOn(mediaFile.getCreatedOn());
            fileOutputWebModel.setUpdatedBy(mediaFile.getUpdatedBy());
            fileOutputWebModel.setUpdatedOn(mediaFile.getUpdatedOn());
            fileOutputWebModel.setFilmHookCode(mediaFile.getUser().getFilmHookCode());

            // Convert Date to LocalDateTime
            Date createdDate = mediaFile.getCreatedOn();
            LocalDateTime createdOn = LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault());

            // Calculate elapsed time
            String elapsedTime = CalendarUtil.calculateElapsedTime(createdOn);
            fileOutputWebModel.setElapsedTime(elapsedTime);

            return fileOutputWebModel;
        } catch (Exception e) {
            logger.error("Error at transformData()...", e);
            e.printStackTrace();
        }
        return fileOutputWebModel;
    }

    @Override
    public void deleteMediaFilesByUserIdAndCategoryAndRefIds(Integer userId, MediaFileCategory category, List<Integer> idList) {
        Optional<User> user = userService.getUser(userId);
        if (user.isPresent()) {
            List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByUserIdAndCategoryAndRefIds(userId, category, idList);
            this.deleteMediaFiles(mediaFiles);
        }
    }

    @Override
    public void deleteMediaFilesByCategoryAndRefIds(MediaFileCategory category, List<Integer> idList) {
        List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByCategoryAndRefIds(category, idList);
        this.deleteMediaFiles(mediaFiles);
    }

    private void deleteMediaFiles(List<MediaFiles> mediaFiles) {
        try {
            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                mediaFiles.forEach(mediaFile -> {
                    mediaFile.setStatus(false); // 1. Deactivating the MediaFiles
                    mediaFilesRepository.saveAndFlush(mediaFile);
                    fileUtil.deleteFile(mediaFile.getFilePath() + mediaFile.getFileType()); // 2. Deleting the S3 Objects
                });
            }
        } catch (Exception e) {
            logger.error("Error at deleteMediaFiles() -> [{}]", e.getMessage());
            e.printStackTrace();
        }
    }

}
