package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.annular.filmhook.model.MediaFileCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.Audition;
import com.annular.filmhook.model.AuditionAcceptanceDetails;
import com.annular.filmhook.model.AuditionDetails;
import com.annular.filmhook.model.AuditionIgnoranceDetails;
import com.annular.filmhook.model.AuditionRoles;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.AuditionAcceptanceRepository;
import com.annular.filmhook.repository.AuditionDetailsRepository;
import com.annular.filmhook.repository.AuditionIgnoranceRepository;
import com.annular.filmhook.repository.AuditionRepository;
import com.annular.filmhook.repository.AuditionRolesRepository;
import com.annular.filmhook.service.AuditionService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.webmodel.AuditionAcceptanceWebModel;
import com.annular.filmhook.webmodel.AuditionDetailsWebModel;
import com.annular.filmhook.webmodel.AuditionIgnoranceWebModel;
import com.annular.filmhook.webmodel.AuditionRolesWebModel;
import com.annular.filmhook.webmodel.AuditionWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

@Service
public class AuditionServiceImpl implements AuditionService {

	public static final Logger logger = LoggerFactory.getLogger(AuditionServiceImpl.class);

	@Autowired
	MediaFilesService mediaFilesService;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	UserService userService;

	@Autowired
	AuditionRepository auditionRepository;

	@Autowired
	AuditionRolesRepository auditionRolesRepository;
	
	@Autowired
	AuditionAcceptanceRepository acceptanceRepository;
	
	@Autowired
	AuditionDetailsRepository auditionDetailsRepository;
	
	@Autowired
	AuditionIgnoranceRepository auditionIgnoranceRepository;

	@Autowired
	UserDetails userDetails;
	
	//	@Autowired
	//	KafkaProducer kafkaProducer;


	@Override
	public ResponseEntity<?> saveAudition(AuditionWebModel auditionWebModel) {
	    HashMap<String, Object> response = new HashMap<>();
	    try {
	        logger.info("Save audition method start");

	        Optional<User> userFromDB = userService.getUser(auditionWebModel.getAuditionCreatedBy());
	        if (!userFromDB.isPresent()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(-1, "User not found", null));
	        }

	        Audition audition = new Audition();
	        audition.setAuditionTitle(auditionWebModel.getAuditionTitle());
	        audition.setAuditionExperience(auditionWebModel.getAuditionExperience());
	        audition.setAuditionCategory(auditionWebModel.getAuditionCategory());
	        audition.setAuditionExpireOn(auditionWebModel.getAuditionExpireOn());
	        audition.setAuditionPostedBy(userFromDB.get().getFilmHookCode());
	        audition.setAuditionCreatedBy(auditionWebModel.getAuditionCreatedBy());
	        audition.setAuditionAddress(auditionWebModel.getAuditionAddress());
	        audition.setAuditionMessage(auditionWebModel.getAuditionMessage());
	        audition.setAuditionLocation(auditionWebModel.getAuditionLocation());
	        audition.setAuditionIsactive(true);

	        Audition savedAudition = auditionRepository.save(audition);
	        List<AuditionRoles> auditionRolesList = new ArrayList<>();

	        if (auditionWebModel.getAuditionRoles().length != 0) {
	            String[] auditionRolesArray = auditionWebModel.getAuditionRoles();
	            for (String role : auditionRolesArray) {
	                AuditionRoles auditionRoles = new AuditionRoles();  // Create a new instance inside the loop
	                auditionRoles.setAuditionRoleDesc(role);
	                auditionRoles.setAudition(savedAudition);
	                auditionRoles.setAuditionRoleCreatedBy(savedAudition.getAuditionCreatedBy());
	                auditionRoles.setAuditionRoleIsactive(true);

	                auditionRolesList.add(auditionRolesRepository.save(auditionRoles));
	            }
	        }

	        auditionWebModel.getFileInputWebModel().setCategory(MediaFileCategory.Audition);
	        auditionWebModel.getFileInputWebModel().setCategoryRefId(savedAudition.getAuditionId()); // adding the story table reference in media files table
	        List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.saveMediaFiles(auditionWebModel.getFileInputWebModel(), userFromDB.get());

	        response.put("Audition details", savedAudition);
	        response.put("Audition roles", auditionRolesList);
	        response.put("Media files", fileOutputWebModelList);

	    } catch (Exception e) {
	        logger.error("Save audition Method Exception...", e);
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new Response(-1, "Fail", e.getMessage()));
	    }
	    return ResponseEntity.status(HttpStatus.OK).body(new Response(1, "Audition details saved successfully", response));
	}

//	@Override
//	public ResponseEntity<?> getAuditionByCategory(Integer categoryId) {
//		HashMap<String, Object> response = new HashMap<String, Object>();
//		try {
//			logger.info("get audition by category method start");
//
//						List<Audition> auditions = auditionRepository.findByAuditionCategory(categoryId);
//			//List<Audition> auditions = auditionRepository.findByAuditionTitle(auditionTitle);
//
//			if(auditions.size()>0) {
//				List<AuditionWebModel> auditionWebModelsList = new ArrayList<>();
//
//				for(Audition audition : auditions) {
//
//					AuditionWebModel auditionWebModel = new AuditionWebModel();
//
//					auditionWebModel.setAuditionId(audition.getAuditionId());
//					auditionWebModel.setAuditionTitle(audition.getAuditionTitle());
//					auditionWebModel.setAuditionExperience(audition.getAuditionExperience());
//					auditionWebModel.setAuditionCategory(audition.getAuditionCategory());
//					auditionWebModel.setAuditionExpireOn(audition.getAuditionExpireOn());
//					auditionWebModel.setAuditionPostedBy(audition.getAuditionPostedBy());
//					auditionWebModel.setAuditionAddress(audition.getAuditionAddress());
//					auditionWebModel.setAuditionMessage(audition.getAuditionMessage());
//					auditionWebModel.setAuditionAttendedCount(acceptanceRepository.getAttendedCount(audition.getAuditionId()));
//					auditionWebModel.setAuditionIgnoredCount(acceptanceRepository.getIgnoredCount(audition.getAuditionId()));
//
//					if(audition.getAuditionRoles().size()>0) {
//						List<AuditionRolesWebModel> auditionRolesWebModelsList = new ArrayList<>();
//
//						for(AuditionRoles auditionRoles : audition.getAuditionRoles()) {
//
//							AuditionRolesWebModel auditionRolesWebModel = new AuditionRolesWebModel();
//							auditionRolesWebModel.setAuditionRoleId(auditionRoles.getAuditionRoleId());
//							auditionRolesWebModel.setAuditionRoleDesc(auditionRoles.getAuditionRoleDesc());
//
//							auditionRolesWebModelsList.add(auditionRolesWebModel);
//
//						}
//
//						auditionWebModel.setAuditionRolesWebModels(auditionRolesWebModelsList);
//					}
//
//					List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, audition.getAuditionId());
//					if (fileOutputWebModelList != null && !fileOutputWebModelList.isEmpty()) {
//						auditionWebModel.setFileOutputWebModel(fileOutputWebModelList);
//					}
//
//					auditionWebModelsList.add(auditionWebModel);
//
//				}
//				response.put("Audition List", auditionWebModelsList);
//
//			}
//			else {
//				response.put("No auditions found", "");
//			}
//
//		} catch (Exception e) {
//			logger.error("get audition by category Method Exception...", e);
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body(new Response(-1, "Fail", e.getMessage()));
//		}
//		return ResponseEntity.status(HttpStatus.OK).body(new Response(1, "Audition details fetched successfully", response));
//	}
	
	@Override
	public ResponseEntity<?> getAuditionByCategory(Integer categoryId) {
	    HashMap<String, Object> response = new HashMap<String, Object>();
	    try {
	        logger.info("get audition by category method start");

	        Integer userId = userDetails.userInfo().getId();
	        // Fetch the list of ignored auditions for the given user
	        List<Integer> ignoredAuditionIds = auditionIgnoranceRepository.findIgnoredAuditionIdsByUserId(userId);

	        // Fetch the list of auditions by category and exclude the ignored ones
	        List<Audition> auditions = auditionRepository.findByAuditionCategory(categoryId)
	                .stream()
	                .filter(audition -> !ignoredAuditionIds.contains(audition.getAuditionId()))
	                .collect(Collectors.toList());

	        if (auditions.size() > 0) {
	            List<AuditionWebModel> auditionWebModelsList = new ArrayList<>();

	            for (Audition audition : auditions) {
	                AuditionWebModel auditionWebModel = new AuditionWebModel();

	                auditionWebModel.setAuditionId(audition.getAuditionId());
	                auditionWebModel.setAuditionTitle(audition.getAuditionTitle());
	                auditionWebModel.setAuditionExperience(audition.getAuditionExperience());
	                auditionWebModel.setAuditionCategory(audition.getAuditionCategory());
	                auditionWebModel.setAuditionExpireOn(audition.getAuditionExpireOn());
	                auditionWebModel.setAuditionPostedBy(audition.getAuditionPostedBy());
	                auditionWebModel.setAuditionAddress(audition.getAuditionAddress());
	                auditionWebModel.setAuditionMessage(audition.getAuditionMessage());
	                auditionWebModel.setAuditionAttendedCount(acceptanceRepository.getAttendedCount(audition.getAuditionId()));
	                auditionWebModel.setAuditionIgnoredCount(acceptanceRepository.getIgnoredCount(audition.getAuditionId()));

	                if (audition.getAuditionRoles().size() > 0) {
	                    List<AuditionRolesWebModel> auditionRolesWebModelsList = new ArrayList<>();

	                    for (AuditionRoles auditionRoles : audition.getAuditionRoles()) {
	                        AuditionRolesWebModel auditionRolesWebModel = new AuditionRolesWebModel();
	                        auditionRolesWebModel.setAuditionRoleId(auditionRoles.getAuditionRoleId());
	                        auditionRolesWebModel.setAuditionRoleDesc(auditionRoles.getAuditionRoleDesc());

	                        auditionRolesWebModelsList.add(auditionRolesWebModel);
	                    }

	                    auditionWebModel.setAuditionRolesWebModels(auditionRolesWebModelsList);
	                }

	                List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, audition.getAuditionId());
	                if (fileOutputWebModelList != null && !fileOutputWebModelList.isEmpty()) {
	                    auditionWebModel.setFileOutputWebModel(fileOutputWebModelList);
	                }

	                auditionWebModelsList.add(auditionWebModel);
	            }
	            response.put("Audition List", auditionWebModelsList);
	        } else {
	            response.put("No auditions found", "");
	        }

	    } catch (Exception e) {
	        logger.error("get audition by category Method Exception...", e);
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new Response(-1, "Fail", e.getMessage()));
	    }
	    return ResponseEntity.status(HttpStatus.OK).body(new Response(1, "Audition details fetched successfully", response));}
	
	@Override
	public ResponseEntity<?> auditionAcceptance(AuditionAcceptanceWebModel acceptanceWebModel) {
		HashMap<String, Object> response = new HashMap<String, Object>();
		try {
			logger.info("Save audition acceptance method start");
			
			Optional<Audition> audition = auditionRepository.findById(acceptanceWebModel.getAuditionRefId());
			
			if(audition.isPresent()) {
				AuditionAcceptanceDetails acceptanceDetails = new AuditionAcceptanceDetails();

				acceptanceDetails.setAuditionAccepted(acceptanceWebModel.isAuditionAccepted());
				acceptanceDetails.setAuditionAcceptanceUser(acceptanceWebModel.getAuditionAcceptanceUser());
				acceptanceDetails.setAuditionRefId(acceptanceWebModel.getAuditionRefId());
				acceptanceDetails.setAuditionAcceptanceCreatedBy(acceptanceWebModel.getAuditionAcceptanceUser());
				
				acceptanceDetails = acceptanceRepository.save(acceptanceDetails);
				response.put("Audition acceptance", acceptanceDetails);
			}
			
			
		} catch (Exception e) {
			logger.error("Save audition acceptance Method Exception...", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new Response(1, "Audition acceptance details saved successfully", response));	
	}

	@Override
	public ResponseEntity<?> auditionIgnorance(AuditionIgnoranceWebModel auditionIgnoranceWebModel) {
	    HashMap<String, Object> response = new HashMap<>();
	    try {
	        logger.info("Save audition ignorance method start");

	        Optional<Audition> audition = auditionRepository.findById(auditionIgnoranceWebModel.getAuditionRefId());

	        if (audition.isPresent()) {
	            Optional<AuditionIgnoranceDetails> existingDetailsOptional = 
	                auditionIgnoranceRepository.findByAuditionRefIdAndAuditionIgnoranceUser(
	                    auditionIgnoranceWebModel.getAuditionRefId(), 
	                    auditionIgnoranceWebModel.getAuditionIgnoranceUser()
	                );

	            AuditionIgnoranceDetails ignoranceDetails;
	            if (existingDetailsOptional.isPresent()) {
	                // Update the existing record
	                ignoranceDetails = existingDetailsOptional.get();
	                ignoranceDetails.setIgnoranceAccepted(auditionIgnoranceWebModel.isIgnoranceAccepted());
	                ignoranceDetails.setAuditionIgnoranceUpdatedBy(auditionIgnoranceWebModel.getAuditionIgnoranceUser());
	                
	            } else {
	                // Create a new record
	                ignoranceDetails = new AuditionIgnoranceDetails();
	                ignoranceDetails.setIgnoranceAccepted(auditionIgnoranceWebModel.isIgnoranceAccepted());
	                ignoranceDetails.setAuditionIgnoranceUser(auditionIgnoranceWebModel.getAuditionIgnoranceUser());
	                ignoranceDetails.setAuditionRefId(auditionIgnoranceWebModel.getAuditionRefId());
	                ignoranceDetails.setAuditionIgnoranceCreatedBy(auditionIgnoranceWebModel.getAuditionIgnoranceUser());
	            }

	            ignoranceDetails = auditionIgnoranceRepository.save(ignoranceDetails);
	            response.put("Audition ignorance details", ignoranceDetails);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(new Response(-1, "Audition not found", null));
	        }

	    } catch (Exception e) {
	        logger.error("Save audition ignorance method exception", e);
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(new Response(-1, "Fail", e.getMessage()));
	    }
	    return ResponseEntity.status(HttpStatus.OK)
	        .body(new Response(1, "Audition ignorance details saved successfully", response));
	}

	  public ResponseEntity<?> getAuditionDetails(AuditionDetailsWebModel auditionDetailsWebModel) {
	        // Fetch all AuditionDetails
	        List<AuditionDetails> auditionDetailsList = auditionDetailsRepository.findAll();

	        if (auditionDetailsList.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }

	        // Create a list to hold the response data
	        List<Map<String, Object>> responseList = auditionDetailsList.stream()
	                .map(auditionDetails -> {
	                    Map<String, Object> response = new HashMap<>();
	                    response.put("auditionDetailsId", auditionDetails.getAuditionDetailsId());
	                    response.put("auditionDetailsName", auditionDetails.getAuditionDetailsName());
	                    return response;
	                })
	                .collect(Collectors.toList());

	        return ResponseEntity.ok(responseList);
	    }
}
