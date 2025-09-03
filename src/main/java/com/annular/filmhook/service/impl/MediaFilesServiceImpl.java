package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.FileStatus;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.MultiMediaFiles;
import com.annular.filmhook.model.Story;
import com.annular.filmhook.model.User;

import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.MultiMediaFileRepository;
import com.annular.filmhook.repository.StoryRepository;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;

import com.annular.filmhook.util.CalendarUtil;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.FilmHookConstants;
import com.annular.filmhook.util.MediaConversionUtil;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.util.Utility;

import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Date;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

@Service
public class MediaFilesServiceImpl implements MediaFilesService {

	public static final Logger logger = LoggerFactory.getLogger(MediaFilesServiceImpl.class);

	@Autowired
	MediaFilesRepository mediaFilesRepository;

	@Autowired
	StoryRepository storiesrRepository;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	UserService userService;

	@Autowired
	MultiMediaFileRepository multiMediaFilesRepository;

	@Autowired
	S3Util s3Util;

//    @Override
//    public List<FileOutputWebModel> saveMediaFiles(FileInputWebModel fileInputWebModel, User user) {
//        List<FileOutputWebModel> fileOutputWebModelList = new ArrayList<>();
//        try {
//            // 1. Save first in MySQL
//            Map<MediaFiles, MultipartFile> mediaFilesMap = this.prepareMultipleMediaFilesData(fileInputWebModel, user);
//            logger.info("Saved MediaFiles rows list size :- [{}]", mediaFilesMap.size());
//
//            // 2. Upload into S3
//            mediaFilesMap.forEach((mediaFile, inputFile) -> {
//                mediaFilesRepository.saveAndFlush(mediaFile);
//                try {
//                    File orginalFile = File.createTempFile(mediaFile.getFileId(), null);
//                    File compressedFile = File.createTempFile(mediaFile.getFileId(), null);
//
//                    FileUtil.convertMultiPartFileToFile(inputFile, orginalFile); // Converting from Multipart to file
//
//                    // Compressing the files before save
//                    if (FileUtil.isImageFile(mediaFile.getFileType())) {
//                        FileUtil.compressImageFile(orginalFile, mediaFile.getFileType().substring(1), compressedFile);
//                    } else if (FileUtil.isVideoFile(mediaFile.getFileType())) {
//                        FileUtil.compressVideoFile(orginalFile, mediaFile.getFileType().substring(1), compressedFile);
//                    } else {
//                        compressedFile = orginalFile;
//                    }
//
//                    String response = fileUtil.uploadFile(compressedFile, mediaFile.getFilePath() + mediaFile.getFileType());
//                    if (response != null && response.equalsIgnoreCase("File Uploaded")) {
//                        orginalFile.delete();
//                        compressedFile.delete(); // deleting temp file
//                        fileOutputWebModelList.add(this.transformData(mediaFile)); // Reading the saved file details
//                    }
//                } catch (IOException e) {
//                    logger.error("Error at media file save() -> {}", e.getMessage());
//                }
//            });
//            fileOutputWebModelList.sort(Comparator.comparing(FileOutputWebModel::getId));
//        } catch (Exception e) {
//            logger.error("Error at saveMediaFiles() -> {}", e.getMessage());
//            e.printStackTrace();
//        }
//        return fileOutputWebModelList;
//    }
	public List<FileOutputWebModel> saveMediaFiles(FileInputWebModel fileInputWebModel, User user) {
		List<FileOutputWebModel> fileOutputWebModelList = new ArrayList<>();
		try {
			Map<MediaFiles, MultipartFile> mediaFilesMap = this.prepareMultipleMediaFilesData(fileInputWebModel, user);
			logger.info("Saved MediaFiles rows list size :- [{}]", mediaFilesMap.size());

			mediaFilesMap.forEach((mediaFile, inputFile) -> {
				mediaFilesRepository.saveAndFlush(mediaFile);

				File originalFile = null;
				File convertedFile = null;
				File thumbnailFile = null;

				try {
					String originalExtension = mediaFile.getFileType();
					originalFile = File.createTempFile(mediaFile.getFileId(), originalExtension);
					FileUtil.convertMultiPartFileToFile(inputFile, originalFile);

					String contentType = inputFile.getContentType();
					String s3FileExtension;

					if (contentType != null && contentType.startsWith("image/")) {
						convertedFile = File.createTempFile("converted_", ".webp");
						MediaConversionUtil.convertToWebP(originalFile.getAbsolutePath(),
								convertedFile.getAbsolutePath());
						s3FileExtension = ".webp";
						
					} else if (contentType != null && contentType.startsWith("video/")) {
						convertedFile = File.createTempFile("converted_", ".webm");
						MediaConversionUtil.convertToWebM(originalFile.getAbsolutePath(),
								convertedFile.getAbsolutePath());
						s3FileExtension = ".webm";

						// ✅ Generate thumbnail from video

		//String ffmpegPath = "C:\\Program Files\\webmUtil\\ffmpeg-7.1.1-essentials_build\\bin\\ffmpeg.exe";
						String playIconPath = "https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/play-icon.png";
				String ffmpegPath = "/usr/bin/ffmpeg";
						String inputPath = convertedFile.getAbsolutePath();
						thumbnailFile = File.createTempFile("thumb_", ".webp");
						String thumbPath = thumbnailFile.getAbsolutePath();

						List<String> command = Arrays.asList(
							    ffmpegPath, "-y",
							    "-i", inputPath,
							    "-i", playIconPath, // PNG play icon
							    "-ss", "00:00:01.000",
							    "-vframes", "1",
							    "-filter_complex", "[1:v]scale=200:200[icon];[0:v][icon]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2",

							    thumbPath
							);

						ProcessBuilder pb = new ProcessBuilder(command);
						pb.redirectErrorStream(true); // Combine stdout and stderr
						Process process = pb.start();

						BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
						String line;
						while ((line = reader.readLine()) != null) {
							logger.info("FFmpeg output: {}", line);
						}

						int exitCode = process.waitFor();
						logger.info("FFmpeg exited with code: {}", exitCode);

						if (exitCode == 0 && thumbnailFile.exists()) {
							String thumbS3Path = mediaFile.getFilePath() + "_thumb.webp";
							String thumbUploadResult = fileUtil.uploadFile(thumbnailFile, thumbS3Path);

							if ("File Uploaded".equalsIgnoreCase(thumbUploadResult)) {
								String thumbFullUrl = "https://d3cb2xboyh9a9l.cloudfront.net/" + thumbS3Path;
								mediaFile.setThumbnailPath(thumbFullUrl);
								logger.info("Thumbnail with play icon uploaded: {}", thumbFullUrl);
							} else {
								logger.warn("Thumbnail upload failed for: {}", mediaFile.getFileId());
							}
						} else {
							logger.warn("Thumbnail generation failed for: {}", mediaFile.getFileId());
						}
					} else {
						convertedFile = originalFile;
						s3FileExtension = originalExtension;
					}

					// ✅ Upload main file
					String s3Path = mediaFile.getFilePath() + s3FileExtension;
					String response = fileUtil.uploadFile(convertedFile, s3Path);

					if ("File Uploaded".equalsIgnoreCase(response)) {
						mediaFile.setFileType(s3FileExtension);
						mediaFilesRepository.saveAndFlush(mediaFile);
						fileOutputWebModelList.add(this.transformData(mediaFile));
					} else {
						logger.error("S3 upload failed for media file: {}", mediaFile.getFileId());
					}

				} catch (Exception e) {
					logger.error("Error during media conversion or upload -> {}", e.getMessage(), e);
				} finally {
					if (originalFile != null && originalFile.exists())
						originalFile.delete();
					if (convertedFile != null && convertedFile.exists() && !convertedFile.equals(originalFile))
						convertedFile.delete();
					if (thumbnailFile != null && thumbnailFile.exists())
						thumbnailFile.delete();
				}
			});

			fileOutputWebModelList.sort(Comparator.comparing(FileOutputWebModel::getId));
		} catch (Exception e) {
			logger.error("Error at saveMediaFiles() -> {}", e.getMessage(), e);
		}

		return fileOutputWebModelList;
	}

	private Map<MediaFiles, MultipartFile> prepareMultipleMediaFilesData(FileInputWebModel fileInput, User user) {
		Map<MediaFiles, MultipartFile> mediaFilesMap = new HashMap<>();
		try {
			if (!Utility.isNullOrEmptyList(fileInput.getFiles())) {
				fileInput.getFiles().stream().filter(Objects::nonNull).forEach(file -> {
					MediaFiles mediaFiles = new MediaFiles();
					mediaFiles.setUser(user);
					mediaFiles.setCategory(fileInput.getCategory());
					mediaFiles.setCategoryRefId(fileInput.getCategoryRefId());
					  mediaFiles.setFileStatus(fileInput.getFileStatus());
					mediaFiles.setDescription(fileInput.getDescription());
					mediaFiles.setFileId(UUID.randomUUID().toString());
					mediaFiles.setFileName(file.getOriginalFilename());
					mediaFiles.setFilePath(FileUtil.generateFilePath(mediaFiles.getUser(),
							fileInput.getCategory().toString(), mediaFiles.getFileId()));
					mediaFiles.setFileType(!Utility.isNullOrBlankWithTrim(file.getOriginalFilename())
							? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))
							: "");
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
						logger.info("MultiMediaFiles entity saved in the database with ID: {}",
								multiMediaFiles.getMultiMediaFileId());
					} catch (Exception e) {
						logger.error("Error saving MultiMediaFiles -> {}", e.getMessage());
					}
					mediaFilesMap.put(mediaFiles, file);
				});
			}
		} catch (Exception e) {
			logger.error("Error occurred at prepareMultipleMediaFilesData() -> {}", e.getMessage());
			e.printStackTrace();
		}
		return mediaFilesMap;
	}

	@Override
	public List<FileOutputWebModel> getMediaFilesByUserId(Integer userId) {
		List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
		try {
			List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByUserId(userId);
			if (!Utility.isNullOrEmptyList(mediaFiles)) {
				outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
			}
		} catch (Exception e) {
			logger.error("Error at getMediaFilesByUser() -> {}", e.getMessage());
			e.printStackTrace();
		}
		return outputWebModelList;
	}

	@Override
	public List<FileOutputWebModel> getMediaFilesByCategory(MediaFileCategory category) {
		List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
		try {
			List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByCategory(category);
			if (!Utility.isNullOrEmptyList(mediaFiles)) {
				outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
			}
		} catch (Exception e) {
			logger.error("Error at getMediaFilesByCategory() -> {}", e.getMessage());
			e.printStackTrace();
		}
		return outputWebModelList;
	}

	@Override
	public List<FileOutputWebModel> getMediaFilesByCategoryAndUserId(MediaFileCategory category, Integer userId) {
		List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
		try {
			List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByUserIdAndCategory(userId, category);
			if (!Utility.isNullOrEmptyList(mediaFiles)) {
				outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
			}
		} catch (Exception e) {
			logger.error("Error at getMediaFilesByUserAndCategory() -> {}", e.getMessage());
			e.printStackTrace();
		}
		return outputWebModelList;
	}

	@Override
	public List<FileOutputWebModel> getMediaFilesByCategoryAndRefId(MediaFileCategory category, Integer refId) {
		List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
		try {
			List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByCategoryAndRefId(category, refId);
			if (!Utility.isNullOrEmptyList(mediaFiles)) {
				outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
			}
		} catch (Exception e) {
			logger.error("Error at getMediaFilesByCategoryAndRefId() -> {}", e.getMessage());
			e.printStackTrace();
		}
		return outputWebModelList;
	}

	@Override
	public List<FileOutputWebModel> getMediaFilesByUserIdAndCategoryAndRefId(Integer userId, MediaFileCategory category,
			Integer refId) {
		List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
		try {
			List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByUserIdAndCategoryAndRefId(userId,
					category, refId);
			if (!Utility.isNullOrEmptyList(mediaFiles)) {
				outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
			}
		} catch (Exception e) {
			logger.error("Error at getMediaFilesByUserIdAndCategoryAndRefId() -> {}", e.getMessage());
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
			fileOutputWebModel
					.setFilePath(s3Util.generateS3FilePath(mediaFile.getFilePath() + mediaFile.getFileType()));
			fileOutputWebModel.setDescription(mediaFile.getDescription());

			fileOutputWebModel.setCreatedBy(mediaFile.getCreatedBy());
			fileOutputWebModel.setCreatedOn(mediaFile.getCreatedOn());
			fileOutputWebModel.setUpdatedBy(mediaFile.getUpdatedBy());
			fileOutputWebModel.setUpdatedOn(mediaFile.getUpdatedOn());
			fileOutputWebModel.setFilmHookCode(mediaFile.getUser().getFilmHookCode());
			fileOutputWebModel.setThumbnailPath(mediaFile.getThumbnailPath());
			fileOutputWebModel.setFileStatus(mediaFile.getFileStatus());
			// Handle category type STORY
			if (mediaFile.getCategory() == MediaFileCategory.Stories) {
				Story story = storiesrRepository.findById(mediaFile.getCategoryRefId()).orElseThrow(
						() -> new RuntimeException("Story not found for id: " + mediaFile.getCategoryRefId()));
				fileOutputWebModel.setType(story.getType());
			}

			// Convert Date to LocalDateTime
			Date createdDate = mediaFile.getCreatedOn();
			LocalDateTime createdOn = LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault());

			// Calculate elapsed time
			String elapsedTime = CalendarUtil.calculateElapsedTime(createdOn);
			fileOutputWebModel.setElapsedTime(elapsedTime);

			return fileOutputWebModel;
		} catch (Exception e) {
			logger.error("Error at transformData() -> {}", e.getMessage());
			e.printStackTrace();
		}
		return fileOutputWebModel;
	}

	@Override
	public void deleteMediaFilesByUserIdAndCategoryAndRefIds(Integer userId, MediaFileCategory category,
			List<Integer> idList) {
		Optional<User> user = userService.getUser(userId);
		if (user.isPresent()) {
			List<MediaFiles> mediaFiles = mediaFilesRepository.getMediaFilesByUserIdAndCategoryAndRefIds(userId,
					category, idList);
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
			if (!Utility.isNullOrEmptyList(mediaFiles)) {
				mediaFiles.forEach(mediaFile -> {
					mediaFile.setStatus(false); // 1. Deactivating the MediaFiles
					mediaFilesRepository.saveAndFlush(mediaFile);
					fileUtil.deleteFile(mediaFile.getFilePath() + mediaFile.getFileType()); // 2. Deleting the S3
																							// Objects
				});
			}
		} catch (Exception e) {
			logger.error("Error at deleteMediaFiles() -> [{}]", e.getMessage());
			e.printStackTrace();
		}
	}

	// File: MediaFilesServiceImpl.java

	@Override
	public FileOutputWebModel getMediaFileById(Integer id) {
		FileOutputWebModel output = null;
		try {
			Optional<MediaFiles> mediaFileOpt = mediaFilesRepository.findById(id);
			if (mediaFileOpt.isPresent() && mediaFileOpt.get().getStatus()) {
				output = transformData(mediaFileOpt.get());
			} else {
				logger.warn("Media file not found or inactive for id: {}", id);
			}
		} catch (Exception e) {
			logger.error("Error in getMediaFileById() -> {}", e.getMessage());
			e.printStackTrace();
		}
		return output;
	}
	@Override
	public List<FileOutputWebModel> getMediaFilesByUserIdAndCategoryAndRefIdAndStatus(Integer userId, MediaFileCategory category,
	                                                                                  Integer refId, FileStatus status) {
	    List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
	    try {
	        List<MediaFiles> mediaFiles = mediaFilesRepository
	            .getMediaFilesByUserIdAndCategoryAndRefIdAndStatus(userId, category, refId, status);
	        if (!Utility.isNullOrEmptyList(mediaFiles)) {
	            outputWebModelList = mediaFiles.stream().map(this::transformData).collect(Collectors.toList());
	        }
	    } catch (Exception e) {
	        logger.error("Error at getMediaFilesByUserIdAndCategoryAndRefIdAndStatus() -> {}", e.getMessage());
	        e.printStackTrace();
	    }
	    return outputWebModelList;
	}
	
	@Override
	public List<FileOutputWebModel> getMediaFilesByUserIdAndCategoryAndStatus(
	    Integer userId, MediaFileCategory category, FileStatus status
	) {
	    List<FileOutputWebModel> outputList = new ArrayList<>();
	    try {
	        List<MediaFiles> mediaFiles = mediaFilesRepository
	            .findByUser_UserIdAndCategoryAndFileStatus(userId, category, status);

	        if (!Utility.isNullOrEmptyList(mediaFiles)) {
	            outputList = mediaFiles.stream()
	                                   .map(this::transformData)
	                                   .collect(Collectors.toList());
	        }
	    } catch (Exception e) {
	        logger.error("Error in getMediaFilesByUserIdAndCategoryAndStatus() -> {}", e.getMessage(), e);
	    }
	    return outputList;
	}


}