package com.annular.filmhook.service.impl;

import java.util.ArrayList;
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
import org.springframework.http.ResponseEntity;
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
import com.annular.filmhook.repository.FilmSubProfessionRepository;
import com.annular.filmhook.repository.IndustryMediaFileRepository;
import com.annular.filmhook.repository.IndustryUserPermanentDetailsRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AdminService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryUserResponseDTO;
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
    private IndustryUserPermanentDetailsRepository industryUserPermanentDetailsRepository;

    @Autowired
    UserService userService;

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

                // Only update the password if it's provided in the request
                if (!Utility.isNullOrBlankWithTrim(userWebModel.getPassword())) user.setPassword(new BCryptPasswordEncoder().encode(userWebModel.getPassword()));

                userRepository.save(user);
                response.put("message", "User profile updated successfully.");

                return ResponseEntity.ok().body(response);
            } else {
                // User with provided userId not found
                return ResponseEntity.ok().body(new Response(-1, "User not found", "User with provided userId does not exist."));
            }
        } catch (Exception e) {
            logger.error("Update Register Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to update profile", e.getMessage()));
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
            Map<String, Object> pageDetails = new HashMap<>();
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
                response.put("profilePicturePath", userService.getProfilePicUrl(user.getUserId()));

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
                industryMediaFile.setStatus(status);
                // You may perform additional operations if needed
            }

            // Save the updated records back to the database
            industryMediaFileRepository.saveAll(industryDbData);

            // Update the userType in the User table
            Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
            logger.info(">>>>>>>>>>>{}", userWebModel.getUserId());
            if (userOptional.isEmpty()) return new Response(-1, "User not found", null); // Return an error response if user is not found

            User user = userOptional.get();
            if (!status) {
                logger.info("User id -> {}", userWebModel.getUserId());
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
                // status true means userType change to Industry user and send mail notification
                user.setUserType("commonUser");
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


}
