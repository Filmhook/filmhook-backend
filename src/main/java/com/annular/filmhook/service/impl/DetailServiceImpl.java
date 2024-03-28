package com.annular.filmhook.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.Industry;
import com.annular.filmhook.model.IndustryDetails;
import com.annular.filmhook.model.IndustryTemporaryDetails;
import com.annular.filmhook.model.IndustryUserPermanentDetails;
import com.annular.filmhook.model.Platform;
import com.annular.filmhook.model.PlatformDetails;
import com.annular.filmhook.model.PlatformPermanentDetail;
import com.annular.filmhook.model.Profession;
import com.annular.filmhook.model.ProfessionPermanentDetail;
import com.annular.filmhook.model.ProfesssionDetails;
import com.annular.filmhook.model.SubProfessionDetails;
import com.annular.filmhook.model.SubProfesssion;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.IndustryDetailRepository;
import com.annular.filmhook.repository.IndustryRepository;
import com.annular.filmhook.repository.IndustryTemporaryDetailRepository;
import com.annular.filmhook.repository.IndustryUserPermanentDetailsRepository;
import com.annular.filmhook.repository.PlatformDetailRepository;
import com.annular.filmhook.repository.PlatformPermanentDetailRepository;
import com.annular.filmhook.repository.PlatformRepository;
import com.annular.filmhook.repository.ProfessionDetailRepository;
import com.annular.filmhook.repository.ProfessionPermanentDetailRepository;
import com.annular.filmhook.repository.ProfessionRepository;
import com.annular.filmhook.repository.SubProfessionDetailRepository;
import com.annular.filmhook.repository.SubProfesssionRepository;
import com.annular.filmhook.service.AuthenticationService;
import com.annular.filmhook.service.DetailService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserMediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.webmodel.DetailRequest;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryFileInputWebModel;
import com.annular.filmhook.webmodel.IndustryTemporaryWebModel;
import com.annular.filmhook.webmodel.IndustryUserPermanentDetailWebModel;

@Service
public class DetailServiceImpl implements DetailService {

	@Autowired
	private IndustryRepository industryRepository;

	@Autowired
	MediaFilesService mediaFilesService;

	@Autowired
	private PlatformRepository platformRepository;

	@Autowired
	private PlatformPermanentDetailRepository platformPermanentDetailRepository;

	@Autowired
	private IndustryUserPermanentDetailsRepository industryUserPermanentDetailsRepository;

	@Autowired
	private ProfessionRepository professionRepository;

	@Autowired
	private IndustryTemporaryDetailRepository industryTemporaryDetailsRepository;

	@Autowired
	private SubProfesssionRepository subProfessionRepository;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	UserService userService;
	
	@Autowired
	private UserMediaFilesService userMediaFileService;

	@Autowired
	private PlatformDetailRepository platformDetailsRepository;

	@Autowired
	private ProfessionDetailRepository professsionDetailsRepository;

	@Autowired
	private ProfessionPermanentDetailRepository professionPermanentDetailRepository;

	@Autowired
	private IndustryUserPermanentDetailsRepository industryPermanentDetailsRepository;

	@Autowired
	private IndustryDetailRepository industryDetailsRepository;

	@Autowired
	private SubProfessionDetailRepository subProfessionDetailsRepository;

	@Autowired
	UserDetails userDetails;

	@Autowired
	AuthenticationService authenticationService;

	public static final Logger logger = LoggerFactory.getLogger(DetailServiceImpl.class);

	@Override
	public ResponseEntity<?> getDetails(DetailRequest detailRequest) {
		try {
			Map<String, List<?>> details = new HashMap<>();

			if (detailRequest.isIndustries()) {
				List<Map<String, Object>> industryDetails = new ArrayList<>();
				List<Industry> industries = industryRepository.findAll();
				for (Industry industry : industries) {
					Map<String, Object> industryMap = new HashMap<>();
					industryMap.put("industryId", industry.getIndustryId());
					industryMap.put("industryName", industry.getIndustryName());
					industryDetails.add(industryMap);
				}
				details.put("industries", industryDetails);
			}

			if (detailRequest.isPlatforms()) {
				List<Map<String, Object>> industryDetails = new ArrayList<>();
				List<Platform> industries = platformRepository.findAll();
				for (Platform industry : industries) {
					Map<String, Object> industryMap = new HashMap<>();
					industryMap.put("patformId", industry.getPlatformId());
					industryMap.put("platformName", industry.getPlatformName());
					industryDetails.add(industryMap);
				}
				details.put("platform", industryDetails);
			}

			else if (detailRequest.isProfessions()) {
				List<Map<String, Object>> professionDetails = new ArrayList<>();
				List<Profession> professions = professionRepository.findAll();
				for (Profession profession : professions) {
					Map<String, Object> professionMap = new HashMap<>();
					professionMap.put("professionId", profession.getProfessionId());
					professionMap.put("professionName", profession.getProfessionName());
					professionDetails.add(professionMap);
				}
				details.put("professions", professionDetails);
			} else if (detailRequest.isSubProfessions()) {
				List<Map<String, Object>> subProfessionDetails = new ArrayList<>();
				List<SubProfesssion> subProfessions = subProfessionRepository.findAll();
				for (SubProfesssion subProfession : subProfessions) {
					Map<String, Object> subProfessionMap = new HashMap<>();
					subProfessionMap.put("subProfessionId", subProfession.getSubProfessionId());
					subProfessionMap.put("subProfessionName", subProfession.getSubProfessionName());
					subProfessionDetails.add(subProfessionMap);
				}
				details.put("subProfessions", subProfessionDetails);
			}

			return ResponseEntity.ok(details);
		} catch (Exception e) {

			logger.error("getDetails Service Method Exception: {}", e);
			e.printStackTrace();

			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
	}

	@Override
	public ResponseEntity<?> addTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel) {
		try {
			// Extract data from the IndustryTemporaryWebModel object
			List<String> industriesName = industryTemporaryWebModel.getIndustriesName();
			List<String> platformName = industryTemporaryWebModel.getPlatformName();
			List<String> professionName = industryTemporaryWebModel.getProfessionName();
			List<String> subProfessionName = industryTemporaryWebModel.getSubProfessionName();
			Integer userId = industryTemporaryWebModel.getUserId() ;

			// Save details to IndustryTemporaryDetails
			IndustryTemporaryDetails tempDetails = new IndustryTemporaryDetails();
			tempDetails.setIndustriesname(String.join(",", industriesName));
			tempDetails.setPlatformname(String.join(",", platformName));
			tempDetails.setProfessionname(String.join(",", professionName));
			tempDetails.setSubProfessionname(String.join(",", subProfessionName));
			tempDetails.setUserId(userId);
			IndustryTemporaryDetails savedTempDetails = industryTemporaryDetailsRepository.save(tempDetails);

			// Save details to PlatformDetails
			for (String platform : platformName) {
				PlatformDetails platformDetails = new PlatformDetails();
				platformDetails.setIntegerTemporaryDetailId(savedTempDetails.getItId());
				platformDetails.setPlatformName(platform);
				platformDetailsRepository.save(platformDetails);
			}

			// Save details to ProfesssionDetails
			for (String prof : professionName) {
				ProfesssionDetails professionDetails = new ProfesssionDetails();
				professionDetails.setProfessionTemporaryDetailId(savedTempDetails.getItId());
				professionDetails.setProfessionname(prof);
				professsionDetailsRepository.save(professionDetails);
			}

			// Save details to IndustryDetails
			for (String industry : industriesName) {
				IndustryDetails industryDetails = new IndustryDetails();
				industryDetails.setIntegerTemporaryDetailId(savedTempDetails.getItId());
				industryDetails.setIndustry_name(industry);
				industryDetailsRepository.save(industryDetails);
			}

			// Save details to SubProfessionDetails
			for (String subProf : subProfessionName) {
				SubProfessionDetails subProfessionDetails = new SubProfessionDetails();
				subProfessionDetails.setIntegerTemporaryDetailId(savedTempDetails.getItId());
				subProfessionDetails.setSubProfessionName(subProf);
				subProfessionDetailsRepository.save(subProfessionDetails);
			}

			// Log the received data
			logger.info(
					"Received request with industries: {}, platforms: {}, professions: {}, subProfessions: {}, userId: {}",
					industriesName, platformName, professionName, subProfessionName, userId);

			return ResponseEntity.ok("Temporary details added successfully");
		} catch (Exception e) {
			// Handle any exceptions that occur during processing
			logger.error("addTemporaryDetails Service Method Exception: {}", e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
	}

	@Override
	public ResponseEntity<?> getTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel) {
		try {
			List<IndustryTemporaryDetails> temporaryDetailsList = industryTemporaryDetailsRepository
					.findByUserId(industryTemporaryWebModel.getUserId());
			Map<String, Object> response = new HashMap<>();

			for (IndustryTemporaryDetails tempDetails : temporaryDetailsList) {
				// Create a separate industry map for each industry
				Map<String, Object> industryMap = new HashMap<>();
				List<String> industriesName = Arrays.asList(tempDetails.getIndustriesname().split(","));

				for (String industryName : industriesName) {
					// Create a separate platform list for each industry
					List<Map<String, Object>> platformList = new ArrayList<>();
					List<PlatformDetails> platformDetailsList = platformDetailsRepository
							.findByIntegerTemporaryDetailId(tempDetails.getItId());

					for (PlatformDetails platformDetails : platformDetailsList) {
						Map<String, Object> platformMap = new HashMap<>();
						platformMap.put("platformName", platformDetails.getPlatformName());

						// Add professions for the platform
						List<ProfesssionDetails> professionDetailsList = professsionDetailsRepository
								.findByProfessionTemporaryDetailId(tempDetails.getItId());
						List<String> professions = new ArrayList<>();
						for (ProfesssionDetails professionDetails : professionDetailsList) {
							professions.add(professionDetails.getProfessionname());
						}
						platformMap.put("professions", professions);

						// Add sub-professions for the platform
						List<SubProfessionDetails> subProfessionDetailsList = subProfessionDetailsRepository
								.findByIntegerTemporaryDetailId(tempDetails.getItId());
						List<String> subProfessions = new ArrayList<>();
						for (SubProfessionDetails subProfessionDetails : subProfessionDetailsList) {
							subProfessions.add(subProfessionDetails.getSubProfessionName());
						}
						platformMap.put("subProfessions", subProfessions);

						platformList.add(platformMap);
					}

					// Add the platform list to the industry map
					industryMap.put("platforms", platformList);

					// Add the industry map to the response using the industry name as the key
					response.put(industryName, industryMap);
				}
			}

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			// Handle exceptions
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching temporary details.");
		}
	}

	@Override
	public ResponseEntity<?> addIndustryUserPermanentDetails(
			List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels) {
		try {
			for (IndustryUserPermanentDetailWebModel industryUserPermanentDetailWebModel : industryUserPermanentDetailWebModels) {
				// Create IndustryPermanentDetails object
				IndustryUserPermanentDetails industryPermanentDetails = new IndustryUserPermanentDetails();
				industryPermanentDetails.setIndustriesName(industryUserPermanentDetailWebModel.getIndustriesName());
				industryPermanentDetails.setUserId(industryUserPermanentDetailWebModel.getUserId()); // Assuming userId is present in the
																					// request

				// Save the IndustryPermanentDetails object
				IndustryUserPermanentDetails savedDetails = industryUserPermanentDetailsRepository
						.save(industryPermanentDetails);

				// Iterate over platform details
				for (PlatformPermanentDetail platformDetail : industryUserPermanentDetailWebModel
						.getPlatformDetails()) {
					// Create PlatformPermanentDetail object
					PlatformPermanentDetail platformPermanentDetail = new PlatformPermanentDetail();
					platformPermanentDetail.setPlatformName(platformDetail.getPlatformName());
					platformPermanentDetail.setIndustryUserPermanentDetails(savedDetails);

					// Save the PlatformPermanentDetail object
					PlatformPermanentDetail savedPlatform = platformPermanentDetailRepository
							.save(platformPermanentDetail);

					// Iterate over profession details for this platform
					for (ProfessionPermanentDetail professionDetail : platformDetail.getProfessionDetails()) {
						// Create ProfessionPermanentDetail object
						ProfessionPermanentDetail savedProfession = new ProfessionPermanentDetail();
						savedProfession.setProfessionName(professionDetail.getProfessionName());
						savedProfession.setSubProfessionName(professionDetail.getSubProfessionName());
						savedProfession.setPlatformPermanentDetail(savedPlatform);

						// Save the ProfessionPermanentDetail object
						professionPermanentDetailRepository.save(savedProfession);
					}
				}
			}

			// Return a success response
			return ResponseEntity.ok("Industry user permanent details added successfully.");
		} catch (Exception e) {
			// Return an error response if an exception occurs
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to add industry user permanent details.");
		}
	}

	

	@Override
	public FileOutputWebModel saveIndustryUserFiles(IndustryFileInputWebModel inputFileData) {
	    FileOutputWebModel fileOutputWebModel = null;
	    try {
	        Optional<User> userFromDB = userService.getUser(inputFileData.getUserId());
	        System.out.println(userFromDB.get().getUserId());
	        if (userFromDB.isPresent()) {
	            logger.info("User found: " + userFromDB.get().getName());
	            
	            // 1. Save media files in MySQL
	            fileOutputWebModel = userMediaFileService.saveMediaFiles(inputFileData, userFromDB.get());
	            
	            
	            // 2. Upload files to S3
	            uploadToS3(inputFileData.getImages(), fileOutputWebModel);
	            uploadToS3(inputFileData.getVideos(), fileOutputWebModel);
	            uploadToS3(inputFileData.getPanCard(), fileOutputWebModel);
	            uploadToS3(inputFileData.getAdharCard(), fileOutputWebModel);
	        }
	    } catch (Exception e) {
	        logger.error("Error at saveIndustryUserFiles(): ", e);
	        e.printStackTrace();
	    }
	    return fileOutputWebModel;
	}

	private void uploadToS3(MultipartFile file, FileOutputWebModel fileOutputWebModel) {
	    if (file != null && fileOutputWebModel != null) {
	        try {
	            // Check if the file is not null before accessing its properties
	            if (!file.isEmpty() && file.getOriginalFilename() != null) {
	                File tempFile = File.createTempFile(fileOutputWebModel.getFileId(), null);
	                FileUtil.convertMultiPartFileToFile(file, tempFile);
	                String response = fileUtil.uploadFile(tempFile, fileOutputWebModel.getFilePath());
	                logger.info("File saved in S3 response: " + response);
	                if (response != null && response.equalsIgnoreCase("File Uploaded")) {
	                    tempFile.delete(); // deleting temp file
	                }
	            } else {
	                logger.error("Error: Null or empty file provided for upload to S3.");
	            }
	        } catch (Exception e) {
	            logger.error("Error uploading file to S3: ", e);
	            e.printStackTrace();
	        }
	    } else {
	        logger.error("Error: Null file or fileOutputWebModel provided for upload to S3.");
	    }
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
	public ResponseEntity<?> updateTemporaryDetails(IndustryTemporaryWebModel industryTemporaryWebModel) {
	    try {
	        // Extract data from the IndustryTemporaryWebModel object
	        List<String> industriesName = industryTemporaryWebModel.getIndustriesName();
	        List<String> platformName = industryTemporaryWebModel.getPlatformName();
	        List<String> professionName = industryTemporaryWebModel.getProfessionName();
	        List<String> subProfessionName = industryTemporaryWebModel.getSubProfessionName();
	        Integer userId = industryTemporaryWebModel.getUserId() ;
	        Integer temporaryId = industryTemporaryWebModel.getItId(); // Assuming you have a method to get temporary ID

	        // Delete existing temporary details
	        industryTemporaryDetailsRepository.deleteById(temporaryId);
	        professsionDetailsRepository.deleteByProfessionTemporaryDetailId(temporaryId);
	        subProfessionDetailsRepository.deleteByIntegerTemporaryDetailId(temporaryId);
	        industryDetailsRepository.deleteByIntegerTemporaryDetailId(temporaryId);
	        platformDetailsRepository.deleteByIntegerTemporaryDetailId(temporaryId);


	        // Save new details
	        IndustryTemporaryDetails tempDetails = new IndustryTemporaryDetails();
	        tempDetails.setIndustriesname(String.join(",", industriesName));
	        tempDetails.setPlatformname(String.join(",", platformName));
	        tempDetails.setProfessionname(String.join(",", professionName));
	        tempDetails.setSubProfessionname(String.join(",", subProfessionName));
	        tempDetails.setUserId(userId);
	        IndustryTemporaryDetails savedTempDetails = industryTemporaryDetailsRepository.save(tempDetails);

	        // Save details to PlatformDetails
	        for (String platform : platformName) {
	            PlatformDetails platformDetails = new PlatformDetails();
	            platformDetails.setIntegerTemporaryDetailId(savedTempDetails.getItId());
	            platformDetails.setPlatformName(platform);
	            platformDetailsRepository.save(platformDetails);
	        }

	        // Save details to ProfesssionDetails
	        for (String prof : professionName) {
	            ProfesssionDetails professionDetails = new ProfesssionDetails();
	            professionDetails.setProfessionTemporaryDetailId(savedTempDetails.getItId());
	            professionDetails.setProfessionname(prof);
	            professsionDetailsRepository.save(professionDetails);
	        }

	        // Save details to IndustryDetails
	        for (String industry : industriesName) {
	            IndustryDetails industryDetails = new IndustryDetails();
	            industryDetails.setIntegerTemporaryDetailId(savedTempDetails.getItId());
	            industryDetails.setIndustry_name(industry);
	            industryDetailsRepository.save(industryDetails);
	        }

	        // Save details to SubProfessionDetails
	        for (String subProf : subProfessionName) {
	            SubProfessionDetails subProfessionDetails = new SubProfessionDetails();
	            subProfessionDetails.setIntegerTemporaryDetailId(savedTempDetails.getItId());
	            subProfessionDetails.setSubProfessionName(subProf);
	            subProfessionDetailsRepository.save(subProfessionDetails);
	        }

	        // Log the received data
	        logger.info(
	                "Updated temporary details with temporaryId: {}, industries: {}, platforms: {}, professions: {}, subProfessions: {}, userId: {}",
	                temporaryId, industriesName, platformName, professionName, subProfessionName, userId);

	        return ResponseEntity.ok("Temporary details updated successfully");
	    } catch (Exception e) {
	        // Handle any exceptions that occur during processing
	        logger.error("updateTemporaryDetails Service Method Exception: {}", e);
	        e.printStackTrace();
	        return ResponseEntity.ok(new Response(-1, "Fail", ""));
	    }
	}
}
