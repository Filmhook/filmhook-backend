package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.FilmProfession;
import com.annular.filmhook.model.Industry;
import com.annular.filmhook.model.IndustryMediaFiles;
import com.annular.filmhook.model.IndustryUserPermanentDetails;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.Platform;
import com.annular.filmhook.model.PlatformPermanentDetail;
import com.annular.filmhook.model.ProfessionPermanentDetail;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.FilmProfessionRepository;
import com.annular.filmhook.repository.IndustryMediaFileRepository;
import com.annular.filmhook.repository.IndustryRepository;
import com.annular.filmhook.repository.IndustryTemporaryDetailRepository;
import com.annular.filmhook.repository.IndustryUserPermanentDetailsRepository;
import com.annular.filmhook.repository.PlatformDetailRepository;
import com.annular.filmhook.repository.PlatformPermanentDetailRepository;
import com.annular.filmhook.repository.PlatformRepository;
import com.annular.filmhook.repository.ProfessionDetailRepository;
import com.annular.filmhook.repository.ProfessionRepository;
import com.annular.filmhook.repository.SubProfesssionRepository;
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
	private IndustryRepository industryRepository;

	@Autowired
	MediaFilesService mediaFilesService;

	@Autowired
	UserMediaFilesService userMediaFilesService;

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
	FilmProfessionRepository filmProfessionRepository;
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
			List<User> subAdminUsers = userRepository.findByUserType(userWebModel.getUserType());
			if (!subAdminUsers.isEmpty()) {
				List<HashMap<String, Object>> responseList = new ArrayList<>();
				for (User user : subAdminUsers) {
					HashMap<String, Object> userInfo = new HashMap<>();
					userInfo.put("userId", user.getUserId());
					userInfo.put("name", user.getName());
					userInfo.put("email", user.getEmail());
					userInfo.put("adminPageStaus", user.getAdminPageStatus());
					userInfo.put("status", user.getStatus());

					responseList.add(userInfo);
				}
				return ResponseEntity.status(HttpStatus.OK)
						.body(new Response(1, "Sub-admin users retrieved successfully", responseList));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(-1, "No sub-admin users found", "No sub-admin users exist in the system."));
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
	public Response getAllUnverifiedIndustrialUsers() {
		List<IndustryMediaFiles> unverifiedIndustrialUsers = industryMediaFileRepository
				.getAllUnverifiedIndustrialUsers();

		Set<Integer> userIds = new HashSet<>();
		List<Map<String, Object>> responseList = new ArrayList<>();

		// Collect unique user IDs
		for (IndustryMediaFiles user : unverifiedIndustrialUsers) {
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
			return new Response(-1, "Success", responseList);
		} else {
			return new Response(-1, "There are no unverified users found.", "");
		}
	}

	@Override
	public ResponseEntity<?> getIndustryUserPermanentDetails(Integer userId) {
		try {
			List<IndustryUserPermanentDetails> userPermanentDetails = industryUserPermanentDetailsRepository
					.findByUserId(userId);
			if (userPermanentDetails.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("User permanent details not found for user id: " + userId);
			} else {
				List<IndustryUserResponseDTO> responseDTOList = new ArrayList<>();
				for (IndustryUserPermanentDetails details : userPermanentDetails) {
					IndustryUserResponseDTO responseDTO = new IndustryUserResponseDTO();
					responseDTO.setIndustriesName(details.getIndustriesName());
					responseDTO.setIupdId(details.getIupdId());

					System.out.println("<<<<<<<<<<<<<<<<" + details.getIndustriesName());
//					Optional<Industry> industryOptional = industryRepository
//							.findByIndustryName(details.getIndustriesName());
//
//					if (industryOptional.isPresent()) {
//						Industry industry = industryOptional.get();
//						responseDTO.setImage(Base64.getEncoder().encode(industry.getImage()));
//					}

					List<PlatformPermanentDetail> platformDetails = details.getPlatformDetails();
					List<PlatformDetailDTO> platformDetailDTOList = new ArrayList<>();
					for (PlatformPermanentDetail platformDetail : platformDetails) {
						PlatformDetailDTO platformDetailDTO = new PlatformDetailDTO();
						platformDetailDTO.setPlatformName(platformDetail.getPlatformName());
						platformDetailDTO.setPlatformPermanentId(platformDetail.getPlatformPermanentId());
						List<FileOutputWebModel> outputWebModelList = new ArrayList<>();

						outputWebModelList = mediaFilesService.getMediaFilesByUserIdAndCategoryAndRefId(userId,
								MediaFileCategory.Project, platformDetail.getPlatformPermanentId());
						platformDetailDTO.setOutputWebModelList(outputWebModelList); // Set outputWebModelList in DTO

						platformDetailDTO.setPdPlatformId(platformDetail.getPpdPlatformId());
						platformDetailDTO.setDailySalary(platformDetail.getDailySalary());
						platformDetailDTO.setFilmCount(platformDetail.getFilmCount());
						platformDetailDTO.setNetWorth(platformDetail.getNetWorth());
//						Optional<Platform> platformOptional = platformPermanentDetailRepository
//								.findByPlatformName(platformDetail.getPlatformName());
//						if (platformOptional.isPresent()) {
//							Platform platform = platformOptional.get();
//							platformDetailDTO.setImage(Base64.getEncoder().encode(platform.getImage()));
//						}

						List<ProfessionPermanentDetail> professionDetails = platformDetail.getProfessionDetails();
						List<ProfessionDetailDTO> professionDetailDTOList = new ArrayList<>();
						for (ProfessionPermanentDetail professionDetail : professionDetails) {
							ProfessionDetailDTO professionDetailDTO = new ProfessionDetailDTO();
							professionDetailDTO.setProfessionName(professionDetail.getProfessionName());
							professionDetailDTO.setSubProfessionName(professionDetail.getSubProfessionName());
							professionDetailDTO.setProfessionPermanentId(professionDetail.getProfessionPermanentId());
							professionDetailDTO.setPpdProfessionId(professionDetail.getPpdProfessionId());

							Optional<FilmProfession> filmProfessionOptional = filmProfessionRepository
									.findByProfesssionName(professionDetail.getProfessionName());
							if (filmProfessionOptional.isPresent()) {
								FilmProfession filmProfession = filmProfessionOptional.get();
								// professionDetailDTO.setImage(Base64.getEncoder().encode(filmProfession.getImage()));

							}
							professionDetailDTOList.add(professionDetailDTO);
						}
						platformDetailDTO.setProfessionDetails(professionDetailDTOList);
						platformDetailDTOList.add(platformDetailDTO);
					}
					responseDTO.setPlatformDetails(platformDetailDTOList);
					responseDTOList.add(responseDTO);
				}
				return ResponseEntity.ok(responseDTOList);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to retrieve industry user permanent details.");
		}
	}

	@Override
	public Response changeStatusUnverifiedIndustrialUsers(Integer userId) {
	    List<IndustryMediaFiles> industryDbData = industryMediaFileRepository.findByUserId(userId);

	    // Iterate over the list and set status to false
	    for (IndustryMediaFiles industryMediaFile : industryDbData) {
	        industryMediaFile.setStatus(false);
	        // You may perform additional operations if needed
	    }

	    // Save the updated records back to the database
	    industryMediaFileRepository.saveAll(industryDbData);

	    // Update the userType in the User table
	    Optional<User> userOptional = userRepository.findById(userId);
	    if (userOptional.isPresent()) {
	        User user = userOptional.get();
	        user.setUserType("IndustryUser");
	        userRepository.save(user);
	    } else {
	        return new Response(-1, "User not found", null); // Return an error response if user is not found
	    }

	    // Return a success response
	    return new Response(1, "Status updated successfully", industryDbData);
	}



}
