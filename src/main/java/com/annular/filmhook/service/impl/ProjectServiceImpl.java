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

import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.PlatformPermanentDetail;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.PlatformPermanentDetailRepository;
import com.annular.filmhook.service.ProjectMediaFileService;
import com.annular.filmhook.service.ProjectService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

@Service
public class ProjectServiceImpl implements ProjectService {

	public static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

	@Autowired
	private UserDetails userDetails;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	UserService userService;
	
	@Autowired
	PlatformPermanentDetailRepository platformPermanentDetailRepository;

	@Autowired
	ProjectMediaFileService projectMediaFileService;

	@Override
	public FileOutputWebModel saveProjectFiles(FileInputWebModel inputFileData) {
		FileOutputWebModel fileOutputWebModel = null;
		try {
			Optional<User> userFromDB = userService.getUser(inputFileData.getUserId());
			Optional<PlatformPermanentDetail> platformFromDB = platformPermanentDetailRepository.findById(inputFileData.getPermanentPlatformId());
			System.out.println(userFromDB.get().getUserId());
			if (userFromDB.isPresent()) {
				logger.info("User found: " + userFromDB.get().getName());
				if(platformFromDB.isPresent())
				{

				// 1. Save media files in MySQL
				fileOutputWebModel = projectMediaFileService.saveMediaFiles(inputFileData, userFromDB.get());

				// 2. Upload files to S3
				uploadToS3(inputFileData.getProjectImage(), fileOutputWebModel);
				}
			}
		} catch (Exception e) {
			logger.error("Error at saveIndustryUserFiles(): ", e);
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
	public List<FileOutputWebModel> getProjectFiles(Integer userId, Integer platformPermanentId) {
		List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
		try {
			outputWebModelList = projectMediaFileService.getMediaFilesByUserAndplatformPermanentId(userId,platformPermanentId);
		} catch (Exception e) {
			logger.error("Error at getProjectFilesByUser()...", e);
			e.printStackTrace();
		}
		return outputWebModelList;
	}

	@Override
	public Resource getProjectFiles(Integer userId, String category, String fileId) {
		try {
			Optional<User> userFromDB = userService.getUser(userId);
			if (userFromDB.isPresent()) {
				String filePath = FileUtil.generateFilePath(userFromDB.get(), category, fileId);
				return new ByteArrayResource(fileUtil.downloadFile(filePath));
			}
		} catch (Exception e) {
			logger.error("Error at getProjectFiles()...", e);
			e.printStackTrace();
		}
		return null;
	}
}