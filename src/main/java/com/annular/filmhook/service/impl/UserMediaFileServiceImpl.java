package com.annular.filmhook.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.model.IndustryMediaFiles;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.MultiMediaFiles;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.IndustryMediaFileRepository;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.MultiMediaFileRepository;
import com.annular.filmhook.service.AwsS3Service;
import com.annular.filmhook.service.UserMediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.FilmHookConstants;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryFileInputWebModel;

@Service
public class UserMediaFileServiceImpl implements UserMediaFilesService {
	
	public static final Logger logger = LoggerFactory.getLogger(UserMediaFileServiceImpl.class);

	
	@Autowired
	MediaFilesRepository mediaFilesRepository;

	@Autowired
	FileUtil fileUtil;
	
	@Autowired
	IndustryMediaFileRepository industryMediaFileRepository;
	
	@Autowired
	MultiMediaFileRepository multiMediaFilesRepository;

	@Autowired
	AwsS3Service awsS3Service;

	@Autowired
	UserService userService;
	
    @Autowired
    S3Util s3Util;

	@Override
	public FileOutputWebModel saveMediaFiles(IndustryFileInputWebModel inputFileData, User user) {
		FileOutputWebModel fileOutputWebModel = null;
		try {
			// 1. Save files in MySQL
			List<IndustryMediaFiles> mediaFilesList = prepareMediaFileData(inputFileData, user);
			for (IndustryMediaFiles mediaFiles : mediaFilesList) {
				industryMediaFileRepository.save(mediaFiles);
				logger.info("File saved in MySQL. File ID: " + mediaFiles.getFileId());
			}

			// 2. Upload files to S3
			for (IndustryMediaFiles mediaFiles : mediaFilesList) {
				File file = File.createTempFile(mediaFiles.getFileId(), null);
				FileUtil.convertMultiPartFileToFile(inputFileData.getImages(), file);
				//String response = fileUtil.uploadFile(file, mediaFiles.getFilePath());
				String response = fileUtil.uploadFile(file, mediaFiles.getFilePath() + mediaFiles.getFileType());
				if (response != null && response.equalsIgnoreCase("File Uploaded")) {
					file.delete(); // deleting temp file
					fileOutputWebModel = this.transformData(mediaFiles); // Reading the saved file details
				}
			}
		} catch (Exception e) {
			logger.error("Error at saveMediaFiles(): ", e);
			e.printStackTrace();
		}
		return fileOutputWebModel;
	}

	
	private String uploadFileToS3(File file, String filePath) {
		// TODO Auto-generated method stub
		return awsS3Service.putObjectIntoS3(s3Util.getS3BucketName(), filePath, file);
	}
	private List<IndustryMediaFiles> prepareMediaFileData(IndustryFileInputWebModel inputFileData, User user) {
		List<IndustryMediaFiles> mediaFilesList = new ArrayList<>();

		// Process images
		if (inputFileData.getImages() != null) {
			for (MultipartFile image : inputFileData.getImages()) {
				IndustryMediaFiles mediaFiles = createMediaFiles(image, user, "image", inputFileData.getUserId());
				mediaFilesList.add(mediaFiles);
			}
		}

		// Process videos
		if (inputFileData.getVideos() != null) {
			for (MultipartFile video : inputFileData.getVideos()) {
				IndustryMediaFiles mediaFiles = createMediaFiles(video, user, "video", inputFileData.getUserId());
				mediaFilesList.add(mediaFiles);
			}
		}

		// Process PAN card
		if (inputFileData.getPanCard() != null) {
			IndustryMediaFiles mediaFiles = createMediaFiles(inputFileData.getPanCard(), user, "panCard",
					inputFileData.getUserId());
			mediaFilesList.add(mediaFiles);
		}

		// Process Aadhar card
		if (inputFileData.getAdharCard() != null) {
			IndustryMediaFiles mediaFiles = createMediaFiles(inputFileData.getAdharCard(), user, "aadharCard",
					inputFileData.getUserId());
			mediaFilesList.add(mediaFiles);
		}

		return mediaFilesList;
	}

	
	private IndustryMediaFiles createMediaFiles(MultipartFile file, User user, String category, Integer createdBy) {
		IndustryMediaFiles mediaFiles = new IndustryMediaFiles();
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

		logger.info("MediaFiles details to save in MySQL: " + mediaFiles);
		try {
			MultiMediaFiles multiMediaFiles = new MultiMediaFiles();
			multiMediaFiles.setFileName(mediaFiles.getFileName());
			multiMediaFiles.setFileOriginalName(file.getOriginalFilename());
			multiMediaFiles.setFileDomainId(FilmHookConstants.INDUSTRYFILES);
			//System.out.println(mediaFiles.getId());
			multiMediaFiles.setFileDomainReferenceId(mediaFiles.getIndustryMediaid());
			multiMediaFiles.setFileIsActive(true);
			multiMediaFiles.setFileCreatedBy(user.getUserId());
			multiMediaFiles.setFileSize(mediaFiles.getFileSize());
			multiMediaFiles.setFileType(mediaFiles.getFileType());
			multiMediaFiles = multiMediaFilesRepository.save(multiMediaFiles);
			logger.info(
					"MultiMediaFiles entity saved in the database with ID: " + multiMediaFiles.getMultiMediaFileId());
		} catch (Exception e) {
			logger.error("Error saving MultiMediaFiles", e);
			// Handle the error accordingly
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
			//fileOutputWebModel.setFilePath(mediaFile.getFilePath());
			fileOutputWebModel.setFilePath(s3Util.getS3BaseURL() + S3Util.S3_PATH_DELIMITER + mediaFile.getFilePath() + mediaFile.getFileType());

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
	public List<FileOutputWebModel> getMediaFilesByUserAndCategory(Integer userId) {
		List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
		try {
			List<IndustryMediaFiles> mediaFiles = industryMediaFileRepository.getMediaFilesByUserIdAndCategory(userId);
			if (mediaFiles != null && !mediaFiles.isEmpty()) {
				outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
			}
		} catch (Exception e) {
			logger.error("Error at getGalleryFilesByUser()...", e);
			e.printStackTrace();
		}
		return outputWebModelList;
	}


}
