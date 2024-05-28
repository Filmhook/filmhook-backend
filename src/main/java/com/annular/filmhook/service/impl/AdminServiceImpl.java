package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.FilmProfessionPermanentDetail;
import com.annular.filmhook.model.FilmSubProfession;
import com.annular.filmhook.model.IndustryMediaFiles;
import com.annular.filmhook.model.IndustryUserPermanentDetails;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.PlatformPermanentDetail;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.FilmProfessionRepository;
import com.annular.filmhook.repository.FilmSubProfessionRepository;
import com.annular.filmhook.repository.IndustryMediaFileRepository;
import com.annular.filmhook.repository.IndustryUserPermanentDetailsRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AdminService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserMediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryUserResponseDTO;
import com.annular.filmhook.webmodel.PlatformDetailDTO;
import com.annular.filmhook.webmodel.ProfessionDetailDTO;
import com.annular.filmhook.webmodel.UserWebModel;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	IndustryMediaFileRepository industryMediaFileRepository;

	@Autowired
	MediaFilesService mediaFilesService;
	
	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	UserMediaFilesService userMediaFilesService;

	@Autowired
	private IndustryUserPermanentDetailsRepository industryUserPermanentDetailsRepository;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	UserService userService;

	@Autowired
	FilmProfessionRepository filmProfessionRepository;

	@Autowired
	FilmSubProfessionRepository filmSubProfessionRepository;

	public static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

	@Override
	public ResponseEntity<?> userRegister(UserWebModel userWebModel) {

		try {
			logger.info("Admin Register method start");
			Optional<User> userData = userRepository.findByEmailIdAndUserType(userWebModel.getEmail(),
					userWebModel.getUserType());
			BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
			if (!userData.isPresent()) {
				User user = new User();
				user.setName(userWebModel.getName());
				user.setEmail(userWebModel.getEmail());
				user.setUserType(userWebModel.getUserType());
				user.setStatus(userWebModel.isStatus());
				user.setAdminPageStatus(true);
				String encryptPwd = bcrypt.encode(userWebModel.getPassword());
				user.setPassword(encryptPwd);

				// Save the user entity in the database
				userRepository.save(user);
				return ResponseEntity.status(HttpStatus.OK).body(new Response(1, "Profile Created Successfully", user));

			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
						new Response(-1, "User already exists", "User with this email and userType already exists"));
			}
		} catch (Exception e) {
			logger.error("Register Method Exception...", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to create profile", e.getMessage()));
		}

	}

	@Override
	public ResponseEntity<?> updateRegister(UserWebModel userWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			logger.info("Admin Update Register method start");

			// Fetch user data by userId
			Optional<User> userData = userRepository.findById(userWebModel.getUserId());

			// Check if the user exists
			if (userData.isPresent()) {
				User user = userData.get();
				user.setName(userWebModel.getName());
				user.setEmail(userWebModel.getEmail());
				user.setUserType(userWebModel.getUserType());

				// Only update password if it's provided in the request
				if (userWebModel.getPassword() != null && !userWebModel.getPassword().isEmpty()) {
					BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
					String encryptPwd = bcrypt.encode(userWebModel.getPassword());
					user.setPassword(encryptPwd);
				}

				userRepository.save(user);
				response.put("message", "User profile updated successfully.");

				return ResponseEntity.status(HttpStatus.OK).body(response);
			} else {
				// User with provided userId not found
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(-1, "User not found", "User with provided userId does not exist."));
			}
		} catch (Exception e) {
			logger.error("Update Register Method Exception...", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to update profile", e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> deleteRegister(UserWebModel userWebModel) {
		try {
			logger.info("Admin Delete Register method start");
			Optional<User> userData = userRepository.findById(userWebModel.getUserId());
			if (userData.isPresent()) {
				User user = userData.get();
				user.setStatus(false);
				userRepository.save(user);
				return ResponseEntity.status(HttpStatus.OK).body(new Response(1, "User marked as deleted", null));
			} else {

				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(-1, "User not found", "User with provided userId does not exist."));
			}
		} catch (Exception e) {
			logger.error("Delete Register Method Exception...", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to mark user as deleted", e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> getRegister(UserWebModel userWebModel) {
		try {
			logger.info("Admin Get Register method start");
			
			HashMap<String, Object> response = new HashMap<>();
			Pageable paging = PageRequest.of(userWebModel.getPageNo()-1,userWebModel.getPageSize());		
			Map<String, Object> pageDetails = new HashMap<>();
			Page<User> subAdminUsers = userRepository.findByUserType(userWebModel.getUserType(),paging);
			List<HashMap<String, Object>> responseList = new ArrayList<>();
			if (!subAdminUsers.isEmpty()) {
				
				for (User user : subAdminUsers) {
					HashMap<String, Object> userInfo = new HashMap<>();
					userInfo.put("userId", user.getUserId());
					userInfo.put("name", user.getName());
					userInfo.put("email", user.getEmail());
					userInfo.put("adminPageStaus", user.getAdminPageStatus());
					userInfo.put("status", user.getStatus());

					responseList.add(userInfo);
				}
				pageDetails.put("totalPages", subAdminUsers.getTotalPages());
				pageDetails.put("totalRecords", subAdminUsers.getTotalElements());
				response.put("Data", responseList);
				response.put("PageInfo", pageDetails);
				return ResponseEntity.status(HttpStatus.OK)
						.body(new Response(1, "Sub-admin users retrieved successfully", response));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(-1, "No sub-admin users found", response));
			}
		} catch (Exception e) {
			logger.error("Get Register Method Exception...", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to retrieve sub-admin users", e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> adminPageStatus(UserWebModel userWebModel) {
		try {
			logger.info("adminPageStatus method start");
			Optional<User> userData = userRepository.findById(userWebModel.getUserId());
			if (userData.isPresent()) {
				User user = userData.get();
				user.setAdminPageStatus(userWebModel.getAdminPageStatus());
				userRepository.save(user);
				logger.info("adminPageStatus method end");
				return ResponseEntity.ok(new Response(1, "success", "adminPageStatus successfully"));
			} else {
				logger.info("adminPageStatus method end");
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(0, "User not found with ID: " + userWebModel.getUserId(), null));
			}
		} catch (Exception e) {
			logger.error("adminPageStatus Method Exception {} " + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to set adminPageStatus", e.getMessage()));
		}
	}

	@Override
	public Response getAllUnverifiedIndustrialUsers(UserWebModel userWebModel) {
		HashMap<String, Object> response = new HashMap<>();

		Pageable paging = PageRequest.of(userWebModel.getPageNo()-1,userWebModel.getPageSize());		
		Page<IndustryMediaFiles> unverifiedIndustrialUsers =  industryMediaFileRepository.getAllUnverifiedIndustrialUsers(paging);
		
		Map<String, Object> pageDetails = new HashMap<>();
		
		Set<Integer> userIds = new HashSet<>();
		List<Map<String, Object>> responseList = new ArrayList<>();
		
		// Collect unique user IDs
		for (IndustryMediaFiles user : unverifiedIndustrialUsers.getContent()) {
			User userEntity = user.getUser();
			if (userEntity != null) {
				userIds.add(userEntity.getUserId());
			}
		}

		// Fetch user details for each user ID
		for (int userId : userIds) {
			User user = userRepository.findById(userId).orElse(null);
			if (user != null) {
				Map<String, Object> userMap = new HashMap<>();
				userMap.put("userId", userId);
				userMap.put("name", user.getName()); // Assuming 'name' is a field in the User entity
				// Add other fields you want to include in the response
				// userMap.put("email", user.getEmail());
				// Add other fields as needed
				responseList.add(userMap);
			}
		}

		if (!responseList.isEmpty()) {
			pageDetails.put("totalPages", unverifiedIndustrialUsers.getTotalPages());
//			pageDetails.put("totalRecords", unverifiedIndustrialUsers.getTotalElements());
			/*
			 * To get total number of records the above line is actual procedure, since we
			 * have 3 documents for single user count may differ, so I used count of unique
			 * userId for total no. of records
			 */
			pageDetails.put("totalRecords", userIds.size());
			response.put("UserDetails", responseList);
			response.put("PageInfo", pageDetails);
			return new Response(-1, "Success", response);
		} else {
			return new Response(-1, "There are no unverified users found.", responseList);
		}
	}
	
	@Override
	public ResponseEntity<?> getIndustryUserPermanentDetails(UserWebModel userWebModel) {
	    try {
	        HashMap<String, Object> response = new HashMap<>();

	        // Fetch all user permanent details by userId without pagination
	        List<IndustryUserPermanentDetails> userPermanentDetailsList = industryUserPermanentDetailsRepository.findByUserId(userWebModel.getUserId());

	        if (userPermanentDetailsList.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User permanent details not found for user id: " + userWebModel.getUserId());
	        } else {
	            Optional<User> userOptional = userService.getUser(userWebModel.getUserId());
	            if (!userOptional.isPresent()) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found for user id: " + userWebModel.getUserId());
	            }
	            User user = userOptional.get();

	            // Fetching the ProfilePic Path
	            List<FileOutputWebModel> userProfilePic = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.ProfilePic, user.getUserId());
	            String profilePicturePath = null;
	            if (!userProfilePic.isEmpty()) {
	                FileOutputWebModel profilePic = userProfilePic.get(0);
	                profilePicturePath = profilePic.getFilePath();
	            }

	            List<IndustryUserResponseDTO> responseDTOList = new ArrayList<>();
	            for (IndustryUserPermanentDetails details : userPermanentDetailsList) {
	                IndustryUserResponseDTO responseDTO = new IndustryUserResponseDTO();
	                responseDTO.setIndustriesName(details.getIndustriesName());
	                responseDTO.setIupdId(details.getIupdId());

	                List<PlatformPermanentDetail> platformDetails = details.getPlatformDetails();
	                List<PlatformDetailDTO> platformDetailDTOList = new ArrayList<>();
	                for (PlatformPermanentDetail platformDetail : platformDetails) {
	                    PlatformDetailDTO platformDetailDTO = new PlatformDetailDTO();
	                    platformDetailDTO.setPlatformName(platformDetail.getPlatformName());
	                    platformDetailDTO.setPlatformPermanentId(platformDetail.getPlatformPermanentId());

	                    List<FileOutputWebModel> outputWebModelList = mediaFilesService.getMediaFilesByUserIdAndCategoryAndRefId(userWebModel.getUserId(), MediaFileCategory.Project, platformDetail.getPlatformPermanentId());
	                    platformDetailDTO.setOutputWebModelList(outputWebModelList);

	                    platformDetailDTO.setPdPlatformId(platformDetail.getPpdPlatformId());
	                    platformDetailDTO.setDailySalary(platformDetail.getDailySalary());
	                    platformDetailDTO.setFilmCount(platformDetail.getFilmCount());
	                    platformDetailDTO.setNetWorth(platformDetail.getNetWorth());

	                    List<ProfessionDetailDTO> professionDetailDTOList = new ArrayList<>();
	                    for (FilmProfessionPermanentDetail professionDetail : platformDetail.getProfessionDetails()) {
	                        ProfessionDetailDTO professionDetailDTO = new ProfessionDetailDTO();
	                        professionDetailDTO.setProfessionName(professionDetail.getProfessionName());

	                        List<String> filmSubProfessionNames = filmSubProfessionRepository.findBySubProfessionName(professionDetail.getProfessionName().toUpperCase())
	                                .stream()
	                                .map(FilmSubProfession::getSubProfessionName)
	                                .collect(Collectors.toList());
	                        professionDetailDTO.setSubProfessionName(filmSubProfessionNames);

	                        professionDetailDTO.setProfessionPermanentId(professionDetail.getProfessionPermanentId());
	                        professionDetailDTO.setPpdProfessionId(professionDetail.getPpdProfessionId());

	                        professionDetailDTOList.add(professionDetailDTO);
	                    }
	                    platformDetailDTO.setProfessionDetails(professionDetailDTOList);
	                    platformDetailDTOList.add(platformDetailDTO);
	                }
	                responseDTO.setPlatformDetails(platformDetailDTOList);
	                responseDTOList.add(responseDTO);
	            }
	            response.put("Data", responseDTOList);
	            response.put("userInfo", user);
	            response.put("profilePicturePath", profilePicturePath);

	            return ResponseEntity.ok(response);
	        }
	    } catch (Exception e) {
	        logger.error("Error occurred while retrieving industry user permanent details: {}", e.getMessage());
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve industry user permanent details.");
	    }
	}


//	@Override
//	public ResponseEntity<?> getIndustryUserPermanentDetails(UserWebModel userWebModel) {
//	    try {
//	        HashMap<String, Object> response = new HashMap<>();
//
//	        Pageable paging = PageRequest.of(userWebModel.getPageNo() - 1, userWebModel.getPageSize());
//	        Page<IndustryUserPermanentDetails> userPermanentDetails = industryUserPermanentDetailsRepository.findByUserId(userWebModel.getUserId(), paging);
//
//	        if (userPermanentDetails.isEmpty()) {
//	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User permanent details not found for user id: " + userWebModel.getUserId());
//	        } else {
//	            Map<String, Object> pageDetails = new HashMap<>();
//	            pageDetails.put("totalPages", userPermanentDetails.getTotalPages());
//	            pageDetails.put("totalRecords", userPermanentDetails.getTotalElements());
//
//	            Optional<User> userOptional = userService.getUser(userWebModel.getUserId());
//	            if (!userOptional.isPresent()) {
//	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found for user id: " + userWebModel.getUserId());
//	            }
//	            User user = userOptional.get();
//	            // Fetching the ProfilePic Path
//                List<FileOutputWebModel> userProfilePic = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.ProfilePic, user.getUserId());
//                String profilePicturePath = null;
//                if (!userProfilePic.isEmpty()) {
//                    FileOutputWebModel profilePic = userProfilePic.get(0);
//                    profilePicturePath = profilePic.getFilePath();
//                }
//	            
//	            List<IndustryUserResponseDTO> responseDTOList = new ArrayList<>();
//	            for (IndustryUserPermanentDetails details : userPermanentDetails) {
//	                IndustryUserResponseDTO responseDTO = new IndustryUserResponseDTO();
//	                responseDTO.setIndustriesName(details.getIndustriesName());
//	                responseDTO.setIupdId(details.getIupdId());
//
//	                List<PlatformPermanentDetail> platformDetails = details.getPlatformDetails();
//	                List<PlatformDetailDTO> platformDetailDTOList = new ArrayList<>();
//	                for (PlatformPermanentDetail platformDetail : platformDetails) {
//	                    PlatformDetailDTO platformDetailDTO = new PlatformDetailDTO();
//	                    platformDetailDTO.setPlatformName(platformDetail.getPlatformName());
//	                    platformDetailDTO.setPlatformPermanentId(platformDetail.getPlatformPermanentId());
//
//	                    List<FileOutputWebModel> outputWebModelList = mediaFilesService.getMediaFilesByUserIdAndCategoryAndRefId(userWebModel.getUserId(), MediaFileCategory.Project, platformDetail.getPlatformPermanentId());
//	                    platformDetailDTO.setOutputWebModelList(outputWebModelList);
//
//	                    platformDetailDTO.setPdPlatformId(platformDetail.getPpdPlatformId());
//	                    platformDetailDTO.setDailySalary(platformDetail.getDailySalary());
//	                    platformDetailDTO.setFilmCount(platformDetail.getFilmCount());
//	                    platformDetailDTO.setNetWorth(platformDetail.getNetWorth());
//
//	                    List<ProfessionDetailDTO> professionDetailDTOList = new ArrayList<>();
//	                    for (FilmProfessionPermanentDetail professionDetail : platformDetail.getProfessionDetails()) {
//	                        ProfessionDetailDTO professionDetailDTO = new ProfessionDetailDTO();
//	                        professionDetailDTO.setProfessionName(professionDetail.getProfessionName());
//
//	                        List<String> filmSubProfessionNames = filmSubProfessionRepository.findBySubProfessionName(professionDetail.getProfessionName().toUpperCase())
//	                                .stream()
//	                                .map(FilmSubProfession::getSubProfessionName)
//	                                .collect(Collectors.toList());
//	                        professionDetailDTO.setSubProfessionName(filmSubProfessionNames);
//
//	                        professionDetailDTO.setProfessionPermanentId(professionDetail.getProfessionPermanentId());
//	                        professionDetailDTO.setPpdProfessionId(professionDetail.getPpdProfessionId());
//
//	                        professionDetailDTOList.add(professionDetailDTO);
//	                    }
//	                    platformDetailDTO.setProfessionDetails(professionDetailDTOList);
//	                    platformDetailDTOList.add(platformDetailDTO);
//	                }
//	                responseDTO.setPlatformDetails(platformDetailDTOList);
//	                responseDTOList.add(responseDTO);
//	            }
//	            //response.put("PageInfo", pageDetails);
//	            response.put("Data", responseDTOList);
//	            response.put("userInfo", user);
//	            response.put("profilePicturePath", profilePicturePath);
//
//	            return ResponseEntity.ok(response);
//	        }
//	    } catch (Exception e) {
//	        logger.error("Error occurred while retrieving industry user permanent details: {}", e.getMessage());
//	        e.printStackTrace();
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve industry user permanent details.");
//	    }
//	}


	@Override
	public Response changeStatusUnverifiedIndustrialUsers(UserWebModel userWebModel) {
	    List<IndustryMediaFiles> industryDbData = industryMediaFileRepository.findByUserId(userWebModel.getUserId());

	    Boolean status = userWebModel.isStatus();
	    
	    // Iterate over the list and set status to false
	    for (IndustryMediaFiles industryMediaFile : industryDbData) {
	        industryMediaFile.setStatus(status);
	        // You may perform additional operations if needed
	    }

	    // Save the updated records back to the database
	    industryMediaFileRepository.saveAll(industryDbData);

	    // Update the userType in the User table
	    if (!status) {
	        Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
	        if (userOptional.isPresent()) {
	            User user = userOptional.get();
	            user.setUserType("IndustryUser");
	            user.setAdminReview(userWebModel.getAdminReview());
	            userRepository.save(user);
	            
	            // Send verification email
	            boolean emailSent = sendVerificationEmail(user, status);
	            if (!emailSent) {
	                // Handle case where email sending fails
	                return new Response(-1, "Failed to send verification email", null);
	            }
	        } else {
	            return new Response(-1, "User not found", null); // Return an error response if user is not found
	        }
	    } else {
	        // status true means userType change to Industry user and send mail notification
	        Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
	        if (userOptional.isPresent()) {
	            User user = userOptional.get();
	            user.setUserType("commonUser");
	            userRepository.save(user);
	            
	            // Send notification email
	            boolean emailSent = sendVerificationEmail(user,status);
	            if (!emailSent) {
	                // Handle case where email sending fails
	                return new Response(-1, "Failed to send notification email", null);
	            }
	        } else {
	            return new Response(-1, "User not found", null); // Return an error response if user is not found
	        }
	    }

	    // Return a success response
	    return new Response(1, "Success", "Status updated successfully");
	}



	public boolean sendVerificationEmail(User user, Boolean status) {
	    Boolean response = true;
	    try {
	        String subject;
	        String mailContent;

	        if (!status) { // If status is false, indicating verification
	            subject = "Your Profile Has Been Verified";
	            mailContent = "<p>Hello " + user.getName() + ",</p>";
	            mailContent += "<p>Your profile on FilmHook has been successfully verified.</p>";
	            mailContent += "<p>Thank You<br>FilmHook</p>";
	        } else { // If status is true, indicating rejection
	            subject = "Profile Rejected";
	            mailContent = "<p>Hello " + user.getName() + ",</p>";
	            mailContent += "<p>Your profile on FilmHook has been rejected.</p>";
	            mailContent += "<p>Please contact support for further details.</p>";
	            mailContent += "<p>Thank You<br>FilmHook</p>";
	        }

	        MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message);
	        helper.setFrom("filmhookapps@gmail", "FilmHook");
	        helper.setTo(user.getEmail());
	        helper.setSubject(subject);
	        helper.setText(mailContent, true);

	        javaMailSender.send(message);
	    } catch (Exception e) {
	        e.printStackTrace();
	        response = false;
	    }
	    return response;
	}


}
