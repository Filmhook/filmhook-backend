package com.annular.filmhook.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.model.MediaFiles;
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
	public FileOutputWebModel saveGalleryFiles(FileInputWebModel fileInput) {
		 FileOutputWebModel fileOutputWebModel = null;
	        try {
	        	Optional<User> userFromDB = userService.getUser(fileInput.getUserId());
				System.out.println(userFromDB.get().getUserId());
				if (userFromDB.isPresent()) {
					logger.info("User found: " + userFromDB.get().getName());
					// 1. Save media files in MySQL
					fileOutputWebModel = mediaFilesService.saveMediaFiles(fileInput, userFromDB.get());

	            // 2. Upload images into S3
	            uploadToS3(fileInput.getGalleryImage(), fileOutputWebModel);
	            
	            // 3. Upload videos into S3
	            uploadToS3(fileInput.getGalleryVideos(), fileOutputWebModel);
	        } }catch (Exception e) {
	            logger.error("Error at saveGalleryFiles()...", e);
	            e.printStackTrace();
	        }
	        return fileOutputWebModel;
	    }

	    private void uploadToS3(MultipartFile[] files, FileOutputWebModel fileOutputWebModel) {
	        if (files != null && files.length > 0) {
	            for (MultipartFile file : files) {
	                try {
	                    if (fileOutputWebModel == null) {
	                        logger.error("Error: fileOutputWebModel is null during file upload to S3.");
	                        return;
	                    }

	                    File tempFile = File.createTempFile(fileOutputWebModel.getFileId(), null);
	                    FileUtil.convertMultiPartFileToFile(file, tempFile);
	                    String response = fileUtil.uploadFile(tempFile, fileOutputWebModel.getFilePath());
	                    logger.info("File saved in S3 response: " + response);
	                    if (response != null && response.equalsIgnoreCase("File Uploaded")) {
	                        tempFile.delete(); // deleting temp file
	                    }
	                } catch (Exception e) {
	                    logger.error("Error uploading file to S3: ", e);
	                    e.printStackTrace();
	                }
	            }
	        }
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
			outputWebModelList = mediaFilesService.getMediaFilesByUserAndCategory(userId);
			
		} catch (Exception e) {
			logger.error("Error at getGalleryFilesByUser()...", e);
			e.printStackTrace();
		}
		return outputWebModelList;
	}

	@Override
	public Resource getAllGalleryFilesInCategory(Integer userId,String category) {
		try {
			Optional<User> userFromDB = userService.getUser(userId);
			if (userFromDB.isPresent()) {
				String destinationPath = FileUtil.generateDestinationPath(userFromDB.get(),category);
				List<S3Object> s3data = awsService.getAllObjectsByBucketAndDestination("filmhook-dev-bucket",destinationPath);
				
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
				List<S3Object> s3data = awsService.getAllObjectsByBucketAndDestination("filmhook-dev-bucket",destinationPath);
				
				return new ByteArrayResource(fileUtil.downloadFile(s3data));
			//}
		} catch (Exception e) {
			logger.error("Error at getGalleryFile()...", e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<FileOutputWebModel> getGalleryFilesByAllUser() {
		List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
		try {
			outputWebModelList = mediaFilesService.getMediaFilesByUserAndCategory();
			
		} catch (Exception e) {
			logger.error("Error at getGalleryFilesByUser()...", e);
			e.printStackTrace();
		}
		return outputWebModelList;
	}
	}
