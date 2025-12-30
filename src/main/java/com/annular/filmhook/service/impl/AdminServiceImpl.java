package com.annular.filmhook.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.FilmProfessionPermanentDetail;
import com.annular.filmhook.model.FilmSubProfession;
import com.annular.filmhook.model.IndustryMediaFiles;
import com.annular.filmhook.model.IndustrySignupDetails;
import com.annular.filmhook.model.IndustryUserPermanentDetails;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.PaymentDetails;
import com.annular.filmhook.model.PlatformPermanentDetail;
import com.annular.filmhook.model.ReportPost;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.FilmSubProfessionRepository;
import com.annular.filmhook.repository.IndustryMediaFileRepository;
import com.annular.filmhook.repository.IndustrySignupDetailsRepository;
import com.annular.filmhook.repository.IndustryUserPermanentDetailsRepository;
import com.annular.filmhook.repository.PaymentDetailsRepository;
import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.ReportRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AdminService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryUserResponseDTO;
import com.annular.filmhook.webmodel.PaymentDetailsWebModel;
import com.annular.filmhook.webmodel.PlatformDetailDTO;
import com.annular.filmhook.webmodel.ProfessionDetailDTO;
import com.annular.filmhook.webmodel.UserWebModel;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.util.MailNotification;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    IndustryMediaFileRepository industryMediaFileRepository;

    @Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    private MailNotification mailNotification;
    
    @Autowired
    private PostsRepository postRepository;

    @Autowired
    private ReportRepository reportPostRepository;
    
    @Autowired
    PaymentDetailsRepository paymentDetailsRepository;

    @Autowired
    private IndustryUserPermanentDetailsRepository industryUserPermanentDetailsRepository;

    @Autowired
    UserService userService;

    @Autowired
    IndustrySignupDetailsRepository industrySignupDetailsRepository;
    
    @Autowired
    FilmSubProfessionRepository filmSubProfessionRepository;

    public static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Override
    public ResponseEntity<?> userRegister(UserWebModel userWebModel) {
        try {
            logger.info("Admin Register method start");
            Optional<User> userData = userRepository.findByEmailAndUserType(userWebModel.getEmail(), userWebModel.getUserType());
            if (userData.isEmpty()) {
                User user = User.builder()
                        .name(userWebModel.getName())
                        .email(userWebModel.getEmail())
                        .userType(userWebModel.getUserType())
                        .status(userWebModel.isStatus())
                        .adminPageStatus(true)
                        .empId(userWebModel.getEmpId())
                        .adminReview((float) 0.0)
                        .password(new BCryptPasswordEncoder().encode(userWebModel.getPassword()))
                        .build();
                // Save the user entity in the database
                userRepository.save(user);
                return ResponseEntity.ok().body(new Response(1, "Profile Created Successfully", user));
            } else {
                return ResponseEntity.badRequest().body(new Response(-1, "User already exists", "User with this email and userType already exists"));
            }
        } catch (Exception e) {
            logger.error("Register Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to create profile", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> updateRegister(UserWebModel userWebModel) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Admin Update Register method start for userId: {}", userWebModel.getUserId());

            Optional<User> userData = userRepository.findById(userWebModel.getUserId());

            if (userData.isPresent()) {
                User user = userData.get();

                // Only update fields that are not null or blank
                if (!Utility.isNullOrBlankWithTrim(userWebModel.getName())) {
                    user.setName(userWebModel.getName());
                }

                if (!Utility.isNullOrBlankWithTrim(userWebModel.getEmail())) {
                    user.setEmail(userWebModel.getEmail());
                }

                if (!Utility.isNullOrBlankWithTrim(userWebModel.getUserType())) {
                    user.setUserType(userWebModel.getUserType());
                }

                if (!Utility.isNullOrBlankWithTrim(userWebModel.getEmpId())) {
                    user.setEmpId(userWebModel.getEmpId());
                }

                if (!Utility.isNullOrBlankWithTrim(userWebModel.getPassword())) {
                    user.setPassword(new BCryptPasswordEncoder().encode(userWebModel.getPassword()));
                }

                userRepository.save(user);
                response.put("status", 1);
                response.put("message", "User profile updated successfully.");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(-1, "User not found", "User with provided userId does not exist."));
            }
        } catch (Exception e) {
            logger.error("Update Register Exception", e);
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
                return ResponseEntity.ok().body(new Response(1, "User marked as deleted", null));
            } else {
                return ResponseEntity.ok().body(new Response(-1, "User not found", "User with provided userId does not exist."));
            }
        } catch (Exception e) {
            logger.error("Delete Register Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to mark user as deleted", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> getRegister(UserWebModel userWebModel) {
        try {
            logger.info("Admin Get Register method start");
            HashMap<String, Object> response = new HashMap<>();

            Pageable paging = PageRequest.of(userWebModel.getPageNo() - 1, userWebModel.getPageSize());
            Page<User> subAdminUsers = userRepository.findByUserType(userWebModel.getUserType(), paging);

            List<HashMap<String, Object>> responseList = new ArrayList<>();
            if (!subAdminUsers.isEmpty()) {
                for (User user : subAdminUsers) {
                    HashMap<String, Object> userInfo = new HashMap<>();
                    userInfo.put("userId", user.getUserId());
                    userInfo.put("name", user.getName());
                    userInfo.put("email", user.getEmail());
                    userInfo.put("empId", user.getEmpId());
                    userInfo.put("adminPageStaus", user.getAdminPageStatus());
                    userInfo.put("status", user.getStatus());
                    responseList.add(userInfo);
                }

                Map<String, Object> pageDetails = new HashMap<>();
                pageDetails.put("totalPages", subAdminUsers.getTotalPages());
                pageDetails.put("totalRecords", subAdminUsers.getTotalElements());

                response.put("Data", responseList);
                response.put("PageInfo", pageDetails);
                return ResponseEntity.ok().body(new Response(1, "Sub-admin users retrieved successfully", response));
            } else {
                return ResponseEntity.ok().body(new Response(-1, "No sub-admin users found", response));
            }
        } catch (Exception e) {
            logger.error("Get Register Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to retrieve sub-admin users", e.getMessage()));
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
                return ResponseEntity.ok().body(new Response(-1, "User not found with ID: " + userWebModel.getUserId(), null));
            }
        } catch (Exception e) {
            logger.error("adminPageStatus Method Exception {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to set adminPageStatus", e.getMessage()));
        }
    }

    @Override
    public Response getAllUnverifiedIndustrialUsers(UserWebModel userWebModel) {
        HashMap<String, Object> response = new HashMap<>();

        Pageable paging = PageRequest.of(userWebModel.getPageNo() - 1, userWebModel.getPageSize());
        Page<IndustryMediaFiles> unverifiedIndustrialUsers = industryMediaFileRepository.getAllUnverifiedIndustrialUsers(paging);

        Set<Integer> userIds = new HashSet<>();
        List<Map<String, Object>> responseList = new ArrayList<>();

        // Collect unique user IDs
        for (IndustryMediaFiles user : unverifiedIndustrialUsers.getContent()) {
            User userEntity = user.getUser();
            if (userEntity != null) {
                userIds.add(userEntity.getUserId());
            }
        }

        // Batch fetch user details for performance
        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userId", user.getUserId());
            userMap.put("name", user.getName());
            userMap.put("userProfilePic", userService.getProfilePicUrl());
            responseList.add(userMap);
        }

        if (!responseList.isEmpty()) {
            Map<String, Object> pageDetails = new HashMap<>();
            pageDetails.put("totalPages", unverifiedIndustrialUsers.getTotalPages());
            pageDetails.put("totalRecords", userIds.size()); // Count of unique users

            response.put("UserDetails", responseList);
            response.put("PageInfo", pageDetails);
            return new Response(1, "Success", response); // ✅ Fixed status code
        } else {
            return new Response(0, "There are no unverified users found.", responseList);
        }
    }

//
//    @Override
//    public ResponseEntity<?> getIndustryUserPermanentDetails(UserWebModel userWebModel) {
//        try {
//            HashMap<String, Object> response = new HashMap<>();
//
//            // Fetch all user permanent details by userId without pagination
//            List<IndustryUserPermanentDetails> userPermanentDetailsList = industryUserPermanentDetailsRepository.findByUserId(userWebModel.getUserId());
//
//            if (userPermanentDetailsList.isEmpty()) {
//                return ResponseEntity.ok().body("User permanent details not found for user id: " + userWebModel.getUserId());
//            } else {
//                Optional<User> userOptional = userService.getUser(userWebModel.getUserId());
//                if (userOptional.isEmpty()) return ResponseEntity.ok().body("User not found for user id: " + userWebModel.getUserId());
//
//                User user = userOptional.get();
//
//                List<IndustryUserResponseDTO> responseDTOList = new ArrayList<>();
//                for (IndustryUserPermanentDetails details : userPermanentDetailsList) {
//                    IndustryUserResponseDTO responseDTO = new IndustryUserResponseDTO();
//                    responseDTO.setIndustriesName(details.getIndustriesName());
//                    responseDTO.setIupdId(details.getIupdId());
//
//                    List<PlatformPermanentDetail> platformDetails = details.getPlatformDetails();
//                    List<PlatformDetailDTO> platformDetailDTOList = new ArrayList<>();
//                    for (PlatformPermanentDetail platformDetail : platformDetails) {
//                        PlatformDetailDTO platformDetailDTO = new PlatformDetailDTO();
//                        platformDetailDTO.setPlatformName(platformDetail.getPlatformName());
//                        platformDetailDTO.setPlatformPermanentId(platformDetail.getPlatformPermanentId());
//
//                        List<FileOutputWebModel> outputWebModelList = mediaFilesService.getMediaFilesByUserIdAndCategoryAndRefId(userWebModel.getUserId(), MediaFileCategory.Project, platformDetail.getPlatformPermanentId());
//                        platformDetailDTO.setOutputWebModelList(outputWebModelList);
//
//                        platformDetailDTO.setPdPlatformId(platformDetail.getPpdPlatformId());
//                        platformDetailDTO.setDailySalary(platformDetail.getDailySalary());
//                        platformDetailDTO.setFilmCount(platformDetail.getFilmCount());
//                        platformDetailDTO.setNetWorth(platformDetail.getNetWorth());
//
//                        List<ProfessionDetailDTO> professionDetailDTOList = new ArrayList<>();
//                        for (FilmProfessionPermanentDetail professionDetail : platformDetail.getProfessionDetails()) {
//                            ProfessionDetailDTO professionDetailDTO = new ProfessionDetailDTO();
//                            professionDetailDTO.setProfessionName(professionDetail.getProfessionName());
//
//                            List<String> filmSubProfessionNames = filmSubProfessionRepository.findBySubProfessionName(professionDetail.getProfessionName().toUpperCase())
//                                    .stream()
//                                    .map(FilmSubProfession::getSubProfessionName)
//                                    .collect(Collectors.toList());
//                            professionDetailDTO.setSubProfessionName(filmSubProfessionNames);
//
//                            professionDetailDTO.setProfessionPermanentId(professionDetail.getProfessionPermanentId());
//                            professionDetailDTO.setPpdProfessionId(professionDetail.getPpdProfessionId());
//
//                            professionDetailDTOList.add(professionDetailDTO);
//                        }
//                        platformDetailDTO.setProfessionDetails(professionDetailDTOList);
//                        platformDetailDTOList.add(platformDetailDTO);
//                    }
//                    responseDTO.setPlatformDetails(platformDetailDTOList);
//                    responseDTOList.add(responseDTO);
//                }
//                response.put("Data", responseDTOList);
//                response.put("userInfo", user);
//                response.put("profilePicturePath", userService.getProfilePicUrl(user.getUserId()));
//
//                return ResponseEntity.ok(response);
//            }
//        } catch (Exception e) {
//            logger.error("Error occurred while retrieving industry user permanent details: {}", e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Failed to retrieve industry user permanent details.");
//        }
//    }
    @Override
    public ResponseEntity<?> getIndustryUserPermanentDetails(UserWebModel userWebModel) {
        try {
            HashMap<String, Object> response = new HashMap<>();

            // Fetch all user permanent details by userId without pagination
            List<IndustryUserPermanentDetails> userPermanentDetailsList = industryUserPermanentDetailsRepository.findByUserId(userWebModel.getUserId());

            if (userPermanentDetailsList.isEmpty()) {
                return ResponseEntity.ok().body("User permanent details not found for user id: " + userWebModel.getUserId());
            } else {
                Optional<User> userOptional = userService.getUser(userWebModel.getUserId());
                if (userOptional.isEmpty()) return ResponseEntity.ok().body("User not found for user id: " + userWebModel.getUserId());

                User user = userOptional.get();
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

                        List<FileOutputWebModel> outputWebModelList = mediaFilesService
                            .getMediaFilesByUserIdAndCategoryAndRefId(userWebModel.getUserId(), MediaFileCategory.Project, platformDetail.getPlatformPermanentId());
                        platformDetailDTO.setOutputWebModelList(outputWebModelList);

                        platformDetailDTO.setPdPlatformId(platformDetail.getPpdPlatformId());
                        platformDetailDTO.setDailySalary(platformDetail.getDailySalary());
                        platformDetailDTO.setFilmCount(platformDetail.getFilmCount());
                        platformDetailDTO.setNetWorth(platformDetail.getNetWorth());

                        List<ProfessionDetailDTO> professionDetailDTOList = new ArrayList<>();
                        for (FilmProfessionPermanentDetail professionDetail : platformDetail.getProfessionDetails()) {
                            ProfessionDetailDTO professionDetailDTO = new ProfessionDetailDTO();
                            professionDetailDTO.setProfessionName(professionDetail.getProfessionName());
                            professionDetailDTO.setProfessionPermanentId(professionDetail.getProfessionPermanentId());
                            professionDetailDTO.setPpdProfessionId(professionDetail.getPpdProfessionId());

                            // ✅ Get sub-profession names from associated FilmSubProfessionPermanentDetail
                            List<String> subProfessionNames = professionDetail.getFilmSubProfessionPermanentDetails()
                                .stream()
                                .map(f -> f.getFilmSubProfession().getSubProfessionName())
                                .collect(Collectors.toList());

                            professionDetailDTO.setSubProfessionName(subProfessionNames);
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
                response.put("profilePicturePath", userService.getProfilePicUrl());

                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving industry user permanent details: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to retrieve industry user permanent details.");
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
//	            return ResponseEntity.ok().body("User permanent details not found for user id: " + userWebModel.getUserId());
//	        } else {
//	            Map<String, Object> pageDetails = new HashMap<>();
//	            pageDetails.put("totalPages", userPermanentDetails.getTotalPages());
//	            pageDetails.put("totalRecords", userPermanentDetails.getTotalElements());
//
//	            Optional<User> userOptional = userService.getUser(userWebModel.getUserId());
//	            if (!userOptional.isPresent()) {
//	                return ResponseEntity.ok().body("User not found for user id: " + userWebModel.getUserId());
//	            }
//	            User user = userOptional.get();
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
//	            response.put("profilePicturePath", userService.getProfilePicUrl(user.getUserId()));
//
//	            return ResponseEntity.ok(response);
//	        }
//	    } catch (Exception e) {
//	        logger.error("Error occurred while retrieving industry user permanent details: {}", e.getMessage());
//	        e.printStackTrace();
//	        return ResponseEntity.internalServerError().body("Failed to retrieve industry user permanent details.");
//	    }
//	}

    @Override
    public Response changeStatusUnverifiedIndustrialUsers(UserWebModel userWebModel) {
        // Check if userId is not null
        if (Utility.isNullOrZero(userWebModel.getUserId())) return new Response(-1, "User ID must not be null", null);

        try {
            List<IndustryMediaFiles> industryDbData = industryMediaFileRepository.findByUserId(userWebModel.getUserId());

            Boolean status = userWebModel.isStatus();
            logger.info(">>>>>>>>>>>>{}", userWebModel.isStatus());

            // Iterate over the list and set status to false
            for (IndustryMediaFiles industryMediaFile : industryDbData) {
                industryMediaFile.setStatus(true);
                industryMediaFile.setUnverifiedList(status);
                
                
                // You may perform additional operations if needed
            }

            // Save the updated records back to the database
            industryMediaFileRepository.saveAll(industryDbData);
           
			Optional<IndustrySignupDetails> signupOpt =
                    industrySignupDetailsRepository.findByUser_UserId(userWebModel.getUserId());
            
            if (signupOpt.isPresent()) {
                IndustrySignupDetails signupDetails = signupOpt.get();
                signupDetails.setVerified(status); 
                industrySignupDetailsRepository.save(signupDetails);
            }
            
            
            // Update the userType in the User table
            Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
            logger.info(">>>>>>>>>>>{}", userWebModel.getUserId());
            if (userOptional.isEmpty()) return new Response(-1, "User not found", null); // Return an error response if user is not found

            User user = userOptional.get();
            if (status) {
                logger.info("User id -> {}", userWebModel.getUserId());
                user.setUserType("Industry User");
                user.setAdminReview(userWebModel.getAdminReview());
                user.setRejectReason(userWebModel.getRejectReason());
                user.setIndustryUserVerified(true);
                user.setUnVerifiedList(true);
                userRepository.save(user);

                // Send verification email
                boolean emailSent = sendVerificationEmail(user, status);
                if (!emailSent) {
                    // Handle case where email sending fails
                    return new Response(-1, "Failed to send verification email", null);
                }
            } else {
                // status true means userType change to Industry user and send mail notification
                user.setUserType("Public User");
                user.setIndustryUserVerified(false);
                user.setUnVerifiedList(true);
                user.setRejectReason(userWebModel.getRejectReason());
                userRepository.save(user);

                // Send notification email
                boolean emailSent = sendVerificationEmail(user, status);
                if (!emailSent) {
                    // Handle case where email sending fails
                    return new Response(-1, "Failed to send notification email", null);
                }
            }

            // Return a success response
            return new Response(1, "Success", "Status updated successfully");
        } catch (Exception e) {
            logger.error("Error occurred while updating status: {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Failed to update status", e.getMessage());
        }
    }

//	@Override
//	public Response changeStatusUnverifiedIndustrialUsers(UserWebModel userWebModel) {
//	    List<IndustryMediaFiles> industryDbData = industryMediaFileRepository.findByUserId(userWebModel.getUserId());
//
//	    Boolean status = userWebModel.isStatus();
//	    System.out.println(">>>>>>>>>>>>"+userWebModel.isStatus());
//	    
//	    // Iterate over the list and set status to false
//	    for (IndustryMediaFiles industryMediaFile : industryDbData) {
//	        industryMediaFile.setStatus(status);
//	        // You may perform additional operations if needed
//	    }
//
//	    // Save the updated records back to the database
//	    industryMediaFileRepository.saveAll(industryDbData);
//
//	    // Update the userType in the User table
//	    if (!status) {
//	    	System.out.println("user"+userWebModel.getUserId());
//	        Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
//	        if (userOptional.isPresent()) {
//	            User user = userOptional.get();
//	            user.setUserType("IndustryUser");
//	            user.setAdminReview(userWebModel.getAdminReview());
//	            userRepository.save(user);
//	            
//	            // Send verification email
//	            boolean emailSent = sendVerificationEmail(user, status);
//	            if (!emailSent) {
//	                // Handle case where email sending fails
//	                return new Response(-1, "Failed to send verification email", null);
//	            }
//	        } else {
//	            return new Response(-1, "User not found", null); // Return an error response if user is not found
//	        }
//	    } else {
//	        // status true means userType change to Industry user and send mail notification
//	        Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
//	        if (userOptional.isPresent()) {
//	            User user = userOptional.get();
//	            user.setUserType("commonUser");
//	            userRepository.save(user);
//	            
//	            // Send notification email
//	            boolean emailSent = sendVerificationEmail(user,status);
//	            if (!emailSent) {
//	                // Handle case where email sending fails
//	                return new Response(-1, "Failed to send notification email", null);
//	            }
//	        } else {
//	            return new Response(-1, "User not found", null); // Return an error response if user is not found
//	        }
//	    }
//
//	    // Return a success response
//	    return new Response(1, "Success", "Status updated successfully");
//	}

    public boolean sendVerificationEmail(User user, Boolean status) {
        try {
            String subject, mailContent = "";
            if (!status) { // If status is false, indicating verification
                subject = "Welcome to Film-hook Media Apps";
               // mailContent += "<p>Dear " + user.getName() + ",</p>";
                mailContent += "<p>As a valued member of the entertainment industry, you now have access to a world of opportunities. "
                            + "Whether you're here to connect, collaborate, or showcase your work, Film-Hook is your best platform to elevate your professional journey.</p>";
                mailContent += "<p>Let’s create something great together!</p>";
                mailContent += "<p>Lights, Camera, Action! 🎥</p>";
                //mailContent += "<p>Best Regards,<br/>The Film-hook Team</p>";
            } else { // If status is true, indicating rejection
                subject = "Profile Rejected";
                mailContent += "<p>Your profile on FilmHook has been rejected. Please contact support for further details.</p>";
            }
            return mailNotification.sendEmailSync(user.getName(), user.getEmail(), subject, mailContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public Response getAllUsers(Integer pageNo, Integer pageSize, String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            ZoneId zoneId = ZoneId.systemDefault();
            Date startDateTime = Date.from(start.atStartOfDay(zoneId).toInstant());
            Date endDateTime = Date.from(end.atTime(23, 59, 59).atZone(zoneId).toInstant());

            Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("userId").descending());

            Page<User> userPage = userRepository.findByStatusTrueAndCreatedOnBetween(startDateTime, endDateTime, pageable);

            List<Map<String, Object>> responseList = new ArrayList<>();
            for (User user : userPage.getContent()) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userId", user.getUserId());
                userMap.put("name", user.getName());
                userMap.put("email", user.getEmail());
                userMap.put("userType", user.getUserType());
                userMap.put("phoneNumber", user.getPhoneNumber());
                userMap.put("gender", user.getGender());
                userMap.put("dob", user.getDob());
                userMap.put("country", user.getCountry());
                userMap.put("state", user.getState());
                userMap.put("verified", user.getVerified());
                userMap.put("onlineStatus", user.getOnlineStatus());
                responseList.add(userMap);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("users", responseList);
            result.put("currentPage", userPage.getNumber());
            result.put("totalPages", userPage.getTotalPages());
            result.put("totalUsers", userPage.getTotalElements());

            return new Response(1, "Success", result);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-1, "Error fetching users", null);
        }
    }


    @Override
    public Response getAllAdminUsersByUserType(String userType, Integer page, Integer size) {
        try {
            // Create Pageable object
            Pageable pageable = PageRequest.of(page - 1, size); // Convert to 0-based index
            
            // Get paginated users
            Page<User> userPage = userRepository.findByUserTypeAndStatusTrue(userType, pageable);
            
            // Build response list
            List<Map<String, Object>> responseList = new ArrayList<>();
            for (User user : userPage.getContent()) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userId", user.getUserId());
                userMap.put("name", user.getName());
                userMap.put("email", user.getEmail());
                userMap.put("userType", user.getUserType());
                userMap.put("phoneNumber", user.getPhoneNumber());
                userMap.put("gender", user.getGender());
                userMap.put("dob", user.getDob());
                userMap.put("country", user.getCountry());
                userMap.put("state", user.getState());
                userMap.put("verified", user.getVerified());
                userMap.put("onlineStatus", user.getOnlineStatus());
                responseList.add(userMap);
            }
            
            // Build final result
            Map<String, Object> result = new HashMap<>();
            result.put("users", responseList);
            result.put("currentPage", userPage.getNumber() + 1); // Convert back to 1-based
            result.put("totalPages", userPage.getTotalPages());
            result.put("totalUsers", userPage.getTotalElements());
            
            return new Response(1, "Success", result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-1, "Error fetching users by userType", null);
        }
    }
    
    @Override
    public Response getAllUsersByUserType(String userType, Integer pageNo, Integer pageSize, String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            ZoneId zoneId = ZoneId.systemDefault();
            Date startDateTime = Date.from(start.atStartOfDay(zoneId).toInstant());
            Date endDateTime = Date.from(end.atTime(23, 59, 59).atZone(zoneId).toInstant());

            Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by("userId").descending());
            Page<User> userPage = userRepository.findByUserTypeAndStatusTrueAndCreatedOnBetween(
                    userType, startDateTime, endDateTime, pageable);

            List<Map<String, Object>> responseList = new ArrayList<>();
            for (User user : userPage.getContent()) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userId", user.getUserId());
                userMap.put("name", user.getName());
                userMap.put("email", user.getEmail());
                userMap.put("userType", user.getUserType());
                userMap.put("phoneNumber", user.getPhoneNumber());
                userMap.put("gender", user.getGender());
                userMap.put("dob", user.getDob());
                userMap.put("country", user.getCountry());
                userMap.put("state", user.getState());
                userMap.put("verified", user.getVerified());
                userMap.put("onlineStatus", user.getOnlineStatus());
                responseList.add(userMap);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("users", responseList);
            result.put("currentPage", userPage.getNumber() + 1);
            result.put("totalPages", userPage.getTotalPages());
            result.put("totalUsers", userPage.getTotalElements());

            return new Response(1, "Success", result);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-1, "Error fetching users by userType", null);
        }
    }


    @Override
    public Response getAllUsersManagerCount(String startDate, String endDate) {
        try {
            // Parse input strings to LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            // Convert to Date (via LocalDateTime)
            ZoneId zoneId = ZoneId.systemDefault();
            Date startDateTime = Date.from(start.atStartOfDay(zoneId).toInstant());
            Date endDateTime = Date.from(end.atTime(23, 59, 59).atZone(zoneId).toInstant());

            // Call repository methods with correct types
            int totalUserCount = userRepository.getTotalActiveUserCount(startDateTime, endDateTime);
            int publicUserCount = userRepository.getActivePublicUserCount(startDateTime, endDateTime);
            int industryUserCount = userRepository.getActiveIndustryUserCount(startDateTime, endDateTime);

            Map<String, Object> countMap = new HashMap<>();
            countMap.put("totalUserCount", totalUserCount);
            countMap.put("publicUserCount", publicUserCount);
            countMap.put("industryUserCount", industryUserCount);

            return new Response(1, "User counts fetched successfully", countMap);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-1, "Date parsing or database error", null);
        }
    }

    @Override
    public Response getAllReportPostCount(String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            ZoneId zoneId = ZoneId.systemDefault();
            Date startDateTime = Date.from(start.atStartOfDay(zoneId).toInstant());
            Date endDateTime = Date.from(end.atTime(23, 59, 59).atZone(zoneId).toInstant());

            // Filtered counts using date range
            int totalPostCount = postRepository.getTotalPostCount(startDateTime, endDateTime);
            int totalReportCount = reportPostRepository.getTotalCount(startDateTime, endDateTime);
            int activeReportCount = reportPostRepository.getActiveCount(startDateTime, endDateTime);
            int inactiveReportCount = reportPostRepository.getInactiveCount(startDateTime, endDateTime);

            Map<String, Object> countMap = new HashMap<>();
            countMap.put("totalPostCount", totalPostCount);
            countMap.put("totalReportCount", totalReportCount);
            countMap.put("activeReportCount", activeReportCount);
            countMap.put("inactiveReportCount", inactiveReportCount);

            return new Response(1, "Post and report post counts fetched successfully", countMap);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-1, "Error while fetching report/post counts", null);
        }
    }
    @Override
    public Response getAllPaymentUserData(Integer page, Integer size, String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            ZoneId zoneId = ZoneId.systemDefault();
            Date startDateTime = Date.from(start.atStartOfDay(zoneId).toInstant());
            Date endDateTime = Date.from(end.atTime(23, 59, 59).atZone(zoneId).toInstant());

            // Cumulative pagination: calculate total items to fetch
            int effectiveSize = page * size;
            Pageable pageable = PageRequest.of(0, effectiveSize, Sort.by("paymentId").descending());
            Page<PaymentDetails> paymentPage = paymentDetailsRepository.findByCreatedOnBetween(startDateTime, endDateTime, pageable);
            List<PaymentDetails> payments = paymentPage.getContent();

            List<Map<String, Object>> result = new ArrayList<>();
            for (PaymentDetails payment : payments) {
                Map<String, Object> map = new HashMap<>();
                map.put("paymentId", payment.getPaymentId());
                map.put("userId", payment.getUserId());
                map.put("firstname", payment.getFirstname());
                map.put("email", payment.getEmail());
                map.put("amount", payment.getAmount());
                map.put("postId", payment.getPostId());
                map.put("promoteId", payment.getPromoteId());
                map.put("status", payment.getStatus());
                map.put("createdOn", payment.getCreatedOn());
                map.put("promotionStatus", payment.getPromotionStatus());
                result.add(map);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("currentPage", page);
            response.put("totalPages", paymentPage.getTotalPages());
            response.put("totalItems", paymentPage.getTotalElements());
            response.put("data", result);

            return new Response(1, "success", response);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-1, "Error fetching payment user data", null);
        }
    }

    @Override
    public Response getAllPaymentStatusCount(String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            ZoneId zoneId = ZoneId.systemDefault();
            Date startDateTime = Date.from(start.atStartOfDay(zoneId).toInstant());
            Date endDateTime = Date.from(end.atTime(23, 59, 59).atZone(zoneId).toInstant());

            Integer total = paymentDetailsRepository.getTotalCount(startDateTime, endDateTime);
            Integer success = paymentDetailsRepository.getCountByPromotionStatus("SUCCESS", startDateTime, endDateTime);
            Integer pending = paymentDetailsRepository.getCountByPromotionStatus("PENDING", startDateTime, endDateTime);
            Integer failed = paymentDetailsRepository.getCountByPromotionStatus("FAILED", startDateTime, endDateTime);
            Integer expired = paymentDetailsRepository.getCountByPromotionStatus("EXPIRED", startDateTime, endDateTime);

            Map<String, Object> map = new HashMap<>();
            map.put("total", total);
            map.put("SUCCESS", success);
            map.put("PENDING", pending);
            map.put("FAILED", failed);
            map.put("EXPIRED", expired);

            return new Response(1, "Payment status counts fetched successfully", map);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-1, "Error while fetching payment status counts", null);
        }
    }


    @Override
    public Response getAllPaymentStatus(String status, Integer page, Integer size, String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            ZoneId zoneId = ZoneId.systemDefault();
            Date startDateTime = Date.from(start.atStartOfDay(zoneId).toInstant());
            Date endDateTime = Date.from(end.atTime(23, 59, 59).atZone(zoneId).toInstant());

            // Adjust page index to be 0-based (Spring Data uses 0-based paging)
            int pageIndex = (page != null && page > 0) ? page - 1 : 0;
            Pageable pageable = PageRequest.of(pageIndex, size, Sort.by("paymentId").descending());

            Page<PaymentDetails> paymentPage = paymentDetailsRepository.findByPromotionStatusAndCreatedOnBetween(
                    status, startDateTime, endDateTime, pageable);

            List<PaymentDetailsWebModel> responseList = paymentPage.getContent().stream().map(payment ->
                PaymentDetailsWebModel.builder()
                    .paymentId(payment.getPaymentId())
                    .txnid(payment.getTxnid())
                    .amount(payment.getAmount())
                    .productinfo(payment.getProductinfo())
                    .firstname(payment.getFirstname())
                    .email(payment.getEmail())
                    .postId(payment.getPostId())
                    .promotionStatus(payment.getPromotionStatus())
                    .reason(payment.getReason())
                    .build()
            ).collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("currentPage", paymentPage.getNumber() + 1); // Return to 1-based index for response
            result.put("totalPages", paymentPage.getTotalPages());
            result.put("totalItems", paymentPage.getTotalElements());
            result.put("data", responseList);

            return new Response(1, "Payment status fetched successfully", result);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-1, "Error fetching payment status data", null);
        }
    }

    @Override
    public Response getAllUnVerifiedRejectedList(Integer pageNo, Integer pageSize,Boolean status) {
        try {
            Pageable paging = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "userId"));

            Page<User> usersPage = userRepository.findUnverifiedOrRejectedUsers(status,paging);
           

            List<Map<String, Object>> userList = new ArrayList<>();
            for (User user : usersPage.getContent()) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userId", user.getUserId());
                userMap.put("name", user.getName());
                userMap.put("email", user.getEmail());
                userMap.put("rejectReason", user.getRejectReason());
                userMap.put("industryUserVerified", user.getIndustryUserVerified());
                userMap.put("status", user.getStatus());
                userList.add(userMap);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("Data", userList);
            result.put("PageInfo", Map.of(
                "totalPages", usersPage.getTotalPages(),
                "totalRecords", usersPage.getTotalElements()
            ));

            return new Response(1, "Unverified or rejected users fetched successfully", result);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-1, "Failed to fetch unverified/rejected users", e.getMessage());
        }
    }

    @Override
    public Response changeNotificationStatus() {
        try {
            // 1. Update notificationCount in User table
            List<User> users = userRepository.findAll();
            for (User user : users) {
                user.setNotificationCount(true);
            }
            userRepository.saveAll(users);

            // 2. Update notificationCount in PaymentDetails table
            List<PaymentDetails> payments = paymentDetailsRepository.findAll();
            for (PaymentDetails payment : payments) {
                payment.setNotificationCount(true);
            }
            paymentDetailsRepository.saveAll(payments);

            // 3. Update notificationCount in Report table
            List<ReportPost> reports = reportPostRepository.findAll();
            for (ReportPost report : reports) {
                report.setNotificationCount(true);
            }
            reportPostRepository.saveAll(reports);

            return new Response(1, "Notification status updated to true for all entries", null);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-1, "Failed to update notification status", null);
        }
    }

    @Override
    public Response getTotalNotificationCount() {
        try {
            // 1. Count from User table
            Integer userCount = userRepository.countByNotificationCountIsNullOrNotificationCountFalseAndStatusTrue();

            // 2. Count from ReportPost table
            Integer reportCount = reportPostRepository.countByNotificationCountIsNullOrNotificationCountFalseAndStatusTrue();

            // 3. Count from PaymentDetails table
            Integer paymentCount = paymentDetailsRepository.countByNotificationCountIsNullOrNotificationCountFalseAndStatusTrue();

            // 4. Total sum
            Integer totalCount = userCount + reportCount + paymentCount;

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("userCount", userCount);
            responseMap.put("reportCount", reportCount);
            responseMap.put("paymentCount", paymentCount);
            responseMap.put("totalNotificationCount", totalCount);

            return new Response(1, "Total notification count fetched successfully", responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-1, "Failed to fetch notification counts", null);
        }
    }

    @Override
    @Transactional
    public Response changeNotificationStatusByIndustryUsers() {
        try {
            List<IndustryMediaFiles> mediaFilesList = industryMediaFileRepository.findAll();
            for (IndustryMediaFiles file : mediaFilesList) {
                file.setNotificationCount(1);
            }

            industryMediaFileRepository.saveAll(mediaFilesList);

            return new Response(1, "Updated " + mediaFilesList.size() + " records", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-1, "Failed to update notificationCount", null);
        }
    }

    @Override
    public Response getIndustryUserCount() {
        try {
            Integer count = industryMediaFileRepository.countDistinctUsersByNotificationCountNullOrZero();
            return new Response(1, "Success", count);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-1, "Failed to fetch count", null);
        }
    }

	




}
