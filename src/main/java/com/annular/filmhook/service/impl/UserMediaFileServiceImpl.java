package com.annular.filmhook.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.IndustryMediaFiles;
import com.annular.filmhook.model.MultiMediaFiles;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.IndustryMediaFileRepository;
import com.annular.filmhook.repository.MultiMediaFileRepository;
import com.annular.filmhook.service.AwsS3Service;
import com.annular.filmhook.service.UserMediaFilesService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.FilmHookConstants;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryFileInputWebModel;

@Service
public class UserMediaFileServiceImpl implements UserMediaFilesService {

    public static final Logger logger = LoggerFactory.getLogger(UserMediaFileServiceImpl.class);

    @Autowired
    FileUtil fileUtil;

    @Autowired
    IndustryMediaFileRepository industryMediaFileRepository;

    @Autowired
    MultiMediaFileRepository multiMediaFilesRepository;

    @Autowired
    AwsS3Service awsS3Service;

    @Autowired
    S3Util s3Util;

    @Override
    public List<FileOutputWebModel> saveMediaFiles(IndustryFileInputWebModel inputFileData, User user) {
        List<FileOutputWebModel> fileOutputWebModelList = new ArrayList<>();
        try {
            Map<IndustryMediaFiles, MultipartFile> mediaFilesMap = this.prepareMediaFileData(inputFileData, user);
            mediaFilesMap.forEach((mediaFile, file) -> {
                industryMediaFileRepository.save(mediaFile); // Save files in MySQL
                logger.info("File saved in MySQL. File ID: {}", mediaFile.getFileId());
                FileOutputWebModel fileOutputWebModel = this.uploadToS3(file, mediaFile);// Upload files to S3
                if (fileOutputWebModel != null)
                    fileOutputWebModelList.add(fileOutputWebModel); // Reading the saved file details
            });
        } catch (Exception e) {
            logger.error("Error at saveMediaFiles() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return fileOutputWebModelList;
    }

    private String uploadFileToS3(File file, String filePath) {
        return awsS3Service.putObjectIntoS3(s3Util.getS3BucketName(), filePath, file);
    }

    private Map<IndustryMediaFiles, MultipartFile> prepareMediaFileData(IndustryFileInputWebModel inputFileData, User user) {
        Map<IndustryMediaFiles, MultipartFile> mediaFilesMap = new HashMap<>();

        // Process images
        if (inputFileData.getImages() != null) {
            for (MultipartFile image : inputFileData.getImages()) {
                IndustryMediaFiles mediaFiles = this.createMediaFiles(image, user, MediaFileCategory.Image.toString(), inputFileData.getUserId());
                if (mediaFiles != null) mediaFilesMap.put(mediaFiles, image);
            }
        }

        // Process videos
        if (inputFileData.getVideos() != null) {
            for (MultipartFile video : inputFileData.getVideos()) {
                IndustryMediaFiles mediaFiles = this.createMediaFiles(video, user, MediaFileCategory.Video.toString(), inputFileData.getUserId());
                if (mediaFiles != null) mediaFilesMap.put(mediaFiles, video);
            }
        }

        // Process PAN card
        if (inputFileData.getPanCard() != null) {
            IndustryMediaFiles mediaFiles = this.createMediaFiles(inputFileData.getPanCard(), user, MediaFileCategory.PanCard.toString(), inputFileData.getUserId());
            if (mediaFiles != null) mediaFilesMap.put(mediaFiles, inputFileData.getPanCard());
        }

        // Process Aadhaar card
        if (inputFileData.getAdharCard() != null) {
            IndustryMediaFiles mediaFiles = this.createMediaFiles(inputFileData.getAdharCard(), user, MediaFileCategory.AadhaarCard.toString(), inputFileData.getUserId());
            if (mediaFiles != null) mediaFilesMap.put(mediaFiles, inputFileData.getAdharCard());
        }

        return mediaFilesMap;
    }

    private IndustryMediaFiles createMediaFiles(MultipartFile file, User user, String category, Integer createdBy) {
        IndustryMediaFiles mediaFiles = null;
        try {
            mediaFiles = new IndustryMediaFiles();
            mediaFiles.setUser(user);
            mediaFiles.setCategory(category);
            mediaFiles.setFileId(UUID.randomUUID().toString());
            mediaFiles.setFileName(file.getOriginalFilename());
            mediaFiles.setFilePath(FileUtil.generateFilePath(user, category, mediaFiles.getFileId()));
            mediaFiles.setFileType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
            mediaFiles.setFileSize(file.getSize());
            mediaFiles.setStatus(true);
            mediaFiles.setCreatedBy(createdBy);
            industryMediaFileRepository.save(mediaFiles);
            logger.info("Industry MediaFiles details to save in MySQL: {}", mediaFiles);

            MultiMediaFiles multiMediaFiles = new MultiMediaFiles();
            multiMediaFiles.setFileName(mediaFiles.getFileName());
            multiMediaFiles.setFileOriginalName(file.getOriginalFilename());
            multiMediaFiles.setFileDomainId(FilmHookConstants.INDUSTRYFILES);
            multiMediaFiles.setFileDomainReferenceId(mediaFiles.getIndustryMediaid());
            multiMediaFiles.setFileIsActive(true);
            multiMediaFiles.setFileCreatedBy(user.getUserId());
            multiMediaFiles.setFileSize(mediaFiles.getFileSize());
            multiMediaFiles.setFileType(mediaFiles.getFileType());
            multiMediaFiles = multiMediaFilesRepository.save(multiMediaFiles);
            logger.info("MultiMediaFiles entity saved in the database with ID: {}", multiMediaFiles.getMultiMediaFileId());
        } catch (Exception e) {
            logger.error("Error saving MultiMediaFiles", e);
        }

        return mediaFiles;
    }

    private FileOutputWebModel transformData(IndustryMediaFiles mediaFile) {
        FileOutputWebModel fileOutputWebModel = null;
        try {
            fileOutputWebModel = new FileOutputWebModel();

            fileOutputWebModel.setUserId(mediaFile.getUser().getUserId());
            fileOutputWebModel.setCategory(mediaFile.getCategory());
            fileOutputWebModel.setId(mediaFile.getIndustryMediaid());
            fileOutputWebModel.setFileId(mediaFile.getFileId());
            fileOutputWebModel.setFileName(mediaFile.getFileName());
            fileOutputWebModel.setFileType(mediaFile.getFileType());
            fileOutputWebModel.setFileSize(mediaFile.getFileSize());
            fileOutputWebModel.setFilePath(s3Util.getS3BaseURL() + S3Util.S3_PATH_DELIMITER + mediaFile.getFilePath() + mediaFile.getFileType());

            fileOutputWebModel.setCreatedBy(mediaFile.getCreatedBy());
            fileOutputWebModel.setCreatedOn(mediaFile.getCreatedOn());
            fileOutputWebModel.setUpdatedBy(mediaFile.getUpdatedBy());
            fileOutputWebModel.setUpdatedOn(mediaFile.getUpdatedOn());

            return fileOutputWebModel;
        } catch (Exception e) {
            logger.error("Error at transformData() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return fileOutputWebModel;
    }

    public FileOutputWebModel uploadToS3(MultipartFile file, IndustryMediaFiles mediaFile) {
        try {
            File orginalFile = File.createTempFile(mediaFile.getFileId(), null);
            File compressedFile = File.createTempFile(mediaFile.getFileId(), null);

            FileUtil.convertMultiPartFileToFile(file, orginalFile);

            // Compressing the files before save
            if (FileUtil.isImageFile(mediaFile.getFileType())) {
                FileUtil.compressImageFile(orginalFile, mediaFile.getFileType().substring(1), compressedFile);
            } else if (FileUtil.isVideoFile(mediaFile.getFileType())) {
                FileUtil.compressVideoFile(orginalFile, mediaFile.getFileType().substring(1), compressedFile);
            } else {
                compressedFile = orginalFile;
            }

            String response = fileUtil.uploadFile(compressedFile, mediaFile.getFilePath() + mediaFile.getFileType());
            if (response != null && response.equalsIgnoreCase("File Uploaded")) {
                orginalFile.delete();
                compressedFile.delete();// deleting temp file
                return this.transformData(mediaFile);
            }
        } catch (Exception e) {
            logger.error("Error at uploadToS3 -> {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public List<FileOutputWebModel> getMediaFilesByUserAndCategory(Integer userId) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            List<IndustryMediaFiles> mediaFiles = industryMediaFileRepository.getMediaFilesByUserIdAndCategory(userId);
            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error at getGalleryFilesByUser() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return outputWebModelList;
    }

}
