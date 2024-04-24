package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.MultiMediaFiles;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.MultiMediaFileRepository;
import com.annular.filmhook.service.AwsS3Service;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
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
    AwsS3Service awsS3Service;

    @Autowired
    UserService userService;

    @Autowired
    MultiMediaFileRepository multiMediaFilesRepository;

    @Autowired
    S3Util s3Util;

    @Override
    public FileOutputWebModel saveMediaFiles(FileInputWebModel fileInputWebModel, User user) {
        FileOutputWebModel fileOutputWebModel = null;
        try {
            // 1. Save first in MySQL
            MediaFiles mediaFiles = this.prepareMediaFileData(fileInputWebModel, user);
            mediaFilesRepository.save(mediaFiles);
            logger.info("File id saved in mysql :- {}", mediaFiles.getFileId());

            // 2. Upload into S3
            File file = File.createTempFile(mediaFiles.getFileId(), null);
            FileUtil.convertMultiPartFileToFile(fileInputWebModel.getFile(), file);
            String response = fileUtil.uploadFile(file, mediaFiles.getFilePath());
            logger.info("File Upload in S3 response :- {}", response);
            if (response != null && response.equalsIgnoreCase("File Uploaded")) {
                file.delete(); // deleting temp file
                fileOutputWebModel = this.transformData(mediaFiles); // Reading the saved file details
            }
        } catch (Exception e) {
            logger.error("Error at saveGalleryFiles()...", e);
            e.printStackTrace();
        }
        return fileOutputWebModel;
    }

    private MediaFiles prepareMediaFileData(FileInputWebModel fileInput, User user) {
        MultipartFile file = fileInput.getFile();

        MediaFiles mediaFiles = new MediaFiles();
        mediaFiles.setUser(user);
        mediaFiles.setCategory(fileInput.getCategory());
        mediaFiles.setCategoryRefId(fileInput.getCategoryRefId());
        mediaFiles.setFileId(UUID.randomUUID().toString());
        mediaFiles.setFileName(file.getOriginalFilename());
        mediaFiles.setFilePath(FileUtil.generateFilePath(mediaFiles.getUser(), fileInput.getCategory(), mediaFiles.getFileId()));
        mediaFiles.setFileType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
        mediaFiles.setFileSize(file.getSize());
        mediaFiles.setStatus(true);
        mediaFiles.setCreatedBy(user.getUserId());
        mediaFiles.setCreatedOn(new Date());
        // mediaFiles.setUpdatedBy(fileInput.getUserId());
        // mediaFiles.setUpdatedOn(new Date());
        logger.info("MediaFiles details to save in MySQL :- {}", mediaFiles);
        mediaFilesRepository.save(mediaFiles);

        // Save multiMediaTable

        try {
            MultiMediaFiles multiMediaFiles = new MultiMediaFiles();
            multiMediaFiles.setFileName(mediaFiles.getFileName());
            multiMediaFiles.setFileOriginalName(fileInput.getFile().getOriginalFilename());
            multiMediaFiles.setFileDomainId(FilmHookConstants.GALLERY);
            System.out.println(mediaFiles.getId());
            multiMediaFiles.setFileDomainReferenceId(mediaFiles.getId());
            multiMediaFiles.setFileIsActive(true);
            multiMediaFiles.setFileCreatedBy(user.getUserId());
            multiMediaFiles.setFileSize(mediaFiles.getFileSize());
            multiMediaFiles.setFileType(mediaFiles.getFileType());
            multiMediaFiles = multiMediaFilesRepository.save(multiMediaFiles);
            logger.info("MultiMediaFiles entity saved in the database with ID: {}", multiMediaFiles.getMultiMediaFileId());
        } catch (Exception e) {
            logger.error("Error saving MultiMediaFiles", e);
            // Handle the error accordingly
        }
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
            e.printStackTrace();
        }
        return outputWebModelList;
    }

    @Override
    public List<FileOutputWebModel> getMediaFilesByUserAndCategory(Integer userId) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByUserIdAndCategory(userId);
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
    public List<FileOutputWebModel> getMediaFilesByCategoryAndRefId(String category, Integer refId) {
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

    private FileOutputWebModel transformData(MediaFiles mediaFile) {
        FileOutputWebModel fileOutputWebModel = null;
        try {
            fileOutputWebModel = new FileOutputWebModel();

            fileOutputWebModel.setId(mediaFile.getId());

            fileOutputWebModel.setUserId(mediaFile.getUser().getUserId());
            fileOutputWebModel.setCategory(mediaFile.getCategory());
            fileOutputWebModel.setCategoryRefId(mediaFile.getCategoryRefId());

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
            e.printStackTrace();
        }
        return fileOutputWebModel;
    }

    @Override
    public void deleteMediaFilesByUserIdAndCategoryAndRefId(Integer userId, String category, List<Integer> idList) {
        Optional<User> user = userService.getUser(userId);
        if (user.isPresent()) {
            List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByUserIdAndCategoryAndRefId(userId, category, idList);
            this.deleteMediaFiles(mediaFiles);
        }
    }

    @Override
    public void deleteMediaFilesByCategoryAndRefId(String category, List<Integer> idList) {
        List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByCategoryAndRefId(category, idList);
        this.deleteMediaFiles(mediaFiles);
    }

    private void deleteMediaFiles(List<MediaFiles> mediaFiles) {
        try {
            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                mediaFiles.forEach(mediaFile -> {
                    mediaFile.setStatus(false); // 1. Deactivating the MediaFiles
                    mediaFilesRepository.saveAndFlush(mediaFile);

                    fileUtil.deleteFile(mediaFile.getFilePath()); // 2. Deleting the S3 Objects
                });
            }
        } catch (Exception e) {
            logger.error("Error at deleteMediaFiles()...", e);
            e.printStackTrace();
        }
    }

    @Override
    public FileOutputWebModel saveMediaFiles(MultipartFile file) {
        FileOutputWebModel fileOutputWebModel = null;
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be null or empty");
            }

            // Save in MySQL
            MediaFiles mediaFiles = prepareMediaFileData(file);
            mediaFilesRepository.save(mediaFiles);
            logger.info("File id saved in MySQL: {}", mediaFiles.getFileId());

            // Upload to S3
            File tempFile = File.createTempFile(mediaFiles.getFileId(), null);
            try {
                FileUtil.convertMultiPartFileToFile(file, tempFile);
                String response = fileUtil.uploadFile(tempFile, mediaFiles.getFilePath());
                if (response != null && response.equalsIgnoreCase("File Uploaded")) {
                    fileOutputWebModel = transformData(mediaFiles);
                } else {
                    // Handle upload failure
                    logger.error("Failed to upload file to S3: " + response);
                }
            } finally {
                // Ensure cleanup of temporary file
                tempFile.delete();
            }
        } catch (Exception e) {
            logger.error("Error at saveMediaFiles()", e);
            // You might want to handle or log the error here
        }
        return fileOutputWebModel;
    }

    private MediaFiles prepareMediaFileData(MultipartFile file) {
        MediaFiles mediaFiles = new MediaFiles();
        mediaFiles.setFileId(UUID.randomUUID().toString());
        mediaFiles.setFileName(file.getOriginalFilename());
        // Assuming you don't need user and category for file path
        mediaFiles.setFilePath(FileUtil.generateFilePath(null, null, mediaFiles.getFileId()));
        mediaFiles.setFileType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
        mediaFiles.setFileSize(file.getSize());
        mediaFiles.setStatus(true);
        // Assuming you don't need createdBy information
        // mediaFiles.setCreatedBy(user.getUserId());
        // Assuming you don't need createdOn information
        // mediaFiles.setCreatedOn(new Date());
        // Set other properties as needed

        return mediaFiles;
    }

    private MediaFiles createMediaFile(MultipartFile file, User user, String category, Integer createdBy) {
        MediaFiles mediaFiles = new MediaFiles();
        mediaFiles.setUser(user);
        mediaFiles.setCategory(category);
        mediaFiles.setFileId(UUID.randomUUID().toString());
        mediaFiles.setFileName(file.getOriginalFilename());
        mediaFiles.setFilePath(FileUtil.generateFilePath(user, category, mediaFiles.getFileId()));
        mediaFiles.setFileType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
        mediaFiles.setFileSize(file.getSize());
        mediaFiles.setStatus(true);
        mediaFiles.setCreatedBy(createdBy);
        mediaFiles.setCreatedOn(new Date());

        logger.info("MediaFiles details to save in MySQL: {}", mediaFiles);

        return mediaFiles;
    }

	@Override
	public List<FileOutputWebModel> getMediaFilesByUserAndCategory(String category) {
	       List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
	        try {
	            List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByUserIdAndCategory(category);
	            if (mediaFiles != null && !mediaFiles.isEmpty()) {
	                outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
	            }
	        } catch (Exception e) {
	            logger.error("Error at getMediaFilesByUserAndCategory()...", e);
	            e.printStackTrace();
	        }
	        return outputWebModelList;
	}
	 

}