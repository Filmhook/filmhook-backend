package com.annular.filmhook.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.model.IndustryMediaFiles;
import com.annular.filmhook.model.MultiMediaFiles;
import com.annular.filmhook.model.ProjectMediaFiles;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.MultiMediaFileRepository;
import com.annular.filmhook.repository.ProjectMediaFilesRepository;
import com.annular.filmhook.service.ProjectMediaFileService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.FilmHookConstants;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

@Service
public class ProjectMediaFileServiceImpl implements ProjectMediaFileService {

	public static final Logger logger = LoggerFactory.getLogger(ProjectMediaFileServiceImpl.class);

	@Autowired
	ProjectMediaFilesRepository projectMediaFilesRepository;

	@Autowired
	MultiMediaFileRepository multiMediaFilesRepository;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	UserService userService;

	@Override
	public FileOutputWebModel saveMediaFiles(FileInputWebModel inputFileData, User user) {
		FileOutputWebModel fileOutputWebModel = null;
		try {
			// 1. Save files in MySQL
			List<ProjectMediaFiles> mediaFilesList = prepareMediaFileData(inputFileData, user);
			for (ProjectMediaFiles mediaFiles : mediaFilesList) {
				projectMediaFilesRepository.save(mediaFiles);
				logger.info("File saved in MySQL. File ID: " + mediaFiles.getFileId());
			}

			// 2. Upload files to S3
			for (ProjectMediaFiles mediaFiles : mediaFilesList) {
				File file = File.createTempFile(mediaFiles.getFileId(), null);
				FileUtil.convertMultiPartFileToFile(inputFileData.getProjectImage(), file);
				String response = fileUtil.uploadFile(file, mediaFiles.getFilePath());
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

	private FileOutputWebModel transformData(ProjectMediaFiles mediaFile) {
		FileOutputWebModel fileOutputWebModel = null;
		try {
			fileOutputWebModel = new FileOutputWebModel();

			fileOutputWebModel.setUserId(mediaFile.getUser().getUserId());
			fileOutputWebModel.setCategory(mediaFile.getCategory());
			fileOutputWebModel.setId(mediaFile.getProjectMediaFilesId());
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

	private List<ProjectMediaFiles> prepareMediaFileData(FileInputWebModel inputFileData, User user) {
		List<ProjectMediaFiles> mediaFilesList = new ArrayList<>();

		// Process images
		if (inputFileData.getProjectImage() != null) {
			for (MultipartFile image : inputFileData.getProjectImage()) {
				ProjectMediaFiles mediaFiles = createMediaFiles(image, user, "project image", inputFileData.getUserId(),inputFileData.getPermanentPlatformId());
				mediaFilesList.add(mediaFiles);
			}
		}

		return mediaFilesList;
	}

	private ProjectMediaFiles createMediaFiles(MultipartFile file, User user, String category, Integer userId,Integer permanentPlatformId) {
		ProjectMediaFiles mediaFiles = new ProjectMediaFiles();
		mediaFiles.setUser(user);
		mediaFiles.setPermanentprofessionid(permanentPlatformId);
		mediaFiles.setCategory(category);
		mediaFiles.setFileId(UUID.randomUUID().toString());
		mediaFiles.setFileName(file.getOriginalFilename());
		mediaFiles.setFilePath(FileUtil.generateFilePath(user, category, mediaFiles.getFileId()));
		mediaFiles.setFileType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
		mediaFiles.setFileSize(file.getSize());
		mediaFiles.setStatus(true);
		mediaFiles.setCreatedBy(userId);

		projectMediaFilesRepository.save(mediaFiles);

		logger.info("MediaFiles details to save in MySQL: " + mediaFiles);
		try {
			MultiMediaFiles multiMediaFiles = new MultiMediaFiles();
			multiMediaFiles.setFileName(mediaFiles.getFileName());
			multiMediaFiles.setFileOriginalName(file.getOriginalFilename());
			multiMediaFiles.setFileDomainId(FilmHookConstants.PROJECTS);
			// System.out.println(mediaFiles.getId());
			multiMediaFiles.setFileDomainReferenceId(mediaFiles.getProjectMediaFilesId());
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

	@Override
	public List<FileOutputWebModel> getMediaFilesByUserAndplatformPermanentId(Integer userId,
			Integer platformPermanentId) {
		List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
		try {
			List<ProjectMediaFiles> mediaFiles = projectMediaFilesRepository.getMediaFilesByUserIdAndPlatformPermanentId(userId,platformPermanentId);
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
