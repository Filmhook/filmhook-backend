package com.annular.filmhook.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.User;
import com.annular.filmhook.service.DetailService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.DetailRequest;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryFileInputWebModel;
import com.annular.filmhook.webmodel.IndustryTemporaryWebModel;
import com.annular.filmhook.webmodel.IndustryUserPermanentDetailWebModel;
import com.annular.filmhook.webmodel.PlatformDetailDTO;
import com.annular.filmhook.webmodel.ShootingLocationWebModal;
import com.annular.filmhook.webmodel.UserWebModel;
import com.annular.filmhook.util.MailNotification;
import com.annular.filmhook.util.Utility;

@RestController
@RequestMapping("/industryUser")
public class DetailsController {

    public static final Logger logger = LoggerFactory.getLogger(DetailsController.class);

    @Autowired
    DetailService detailService;
    
    @Autowired
    MailNotification mailNotification;
    
	@Autowired
	private UserService userService;
	
	

    @PostMapping("/getDetails")
    public ResponseEntity<?> getDetails(@RequestBody DetailRequest detailRequest) {
        try {
            logger.info("getDetails controller start");
            return detailService.getDetails(detailRequest);
        } catch (Exception e) {
            logger.error("getDetails Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @PostMapping("/addTemporaryDetails")
    public ResponseEntity<?> addTemporaryDetails(@RequestBody IndustryTemporaryWebModel industryTemporaryWebModel) {
        try {
            logger.info("addTemporaryDetails controller start");
            return detailService.addTemporaryDetails(industryTemporaryWebModel);
        } catch (Exception e) {
            logger.error("industryTemporaryWebModel Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @PostMapping("/updateTemporaryDetails")
    public ResponseEntity<?> updateTemporaryDetails(@RequestBody IndustryTemporaryWebModel industryTemporaryWebModel) {
        try {
            logger.info("updateTemporaryDetails controller start");
            return detailService.updateTemporaryDetails(industryTemporaryWebModel);
        } catch (Exception e) {
            logger.error("updateTemporaryDetails Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @PostMapping("/getTemporaryDetails")
    public ResponseEntity<?> getTemporaryDetails(@RequestBody IndustryTemporaryWebModel industryTemporaryWebModel) {
        try {
            logger.info("getTemporaryDetails controller start");
            return detailService.getTemporaryDetails(industryTemporaryWebModel);
        } catch (Exception e) {
            logger.error("getTemporaryWebModel Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @PostMapping("/getTemporaryDuplicateDetails")
    public ResponseEntity<?> getTemporaryDuplicateDetails(
            @RequestBody IndustryTemporaryWebModel industryTemporaryWebModel) {
        try {
            logger.info("getTemporaryDuplicateDetails controller start");
            return detailService.getTemporaryDuplicateDetails(industryTemporaryWebModel);
        } catch (Exception e) {
            logger.error("getTemporaryDuplicateDetails Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @PostMapping("/addIndustryUserPermanentDetails")
    public ResponseEntity<?> addIndustryUserPermanentDetails(@RequestParam Integer userId,
                                                             @RequestBody List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels) {
        try {
            logger.info("addIndustryUserPermanentDetails controller start");
            return detailService.addIndustryUserPermanentDetails(userId, industryUserPermanentDetailWebModels);
        } catch (Exception e) {
            logger.error("addIndustryUserPermanentDetails Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }


    @PostMapping("/updatePermanentDetails")
    public ResponseEntity<?> updateIndustryUserPermanentDetails(@RequestParam Integer userId,
                                                                @RequestBody List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels) {
        try {
            logger.info("updateIndustryUserPermanentDetails controller start");
            return detailService.updateIndustryUserPermanentDetails(userId, industryUserPermanentDetailWebModels);
        } catch (Exception e) {
            logger.error("updateIndustryUserPermanentDetails() Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @PostMapping("/getIndustryUserPermanentDetails")
    public ResponseEntity<?> getIndustryUserPermanentDetails(@RequestParam Integer userId) {
        try {
            logger.info("getIndustryUserPermanentDetails controller start");
            return detailService.getIndustryUserPermanentDetails(userId);
        } catch (Exception e) {
            logger.error("getIndustryUserPermanentDetails Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

//    @RequestMapping(path = "/saveIndustryUserFiles", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public Response saveIndustryUserFiles(@ModelAttribute IndustryFileInputWebModel inputFileData) {
//        try {
//            logger.info("Inputs for industry user file save -> {}", inputFileData);
//            List<FileOutputWebModel> outputFilesList = detailService.saveIndustryUserFiles(inputFileData);
//            if (!Utility.isNullOrEmptyList(outputFilesList))
//                return new Response(1, "File(s) saved successfully...", outputFilesList);
//        } catch (Exception e) {
//            logger.error("Error at saveIndustryUserFiles() -> {}", e.getMessage());
//            e.printStackTrace();
//        }
//        return new Response(-1, "Error occurred while saving files...", null);
//    }
    @RequestMapping(path = "/saveIndustryUserFiles", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response saveIndustryUserFiles(@ModelAttribute IndustryFileInputWebModel inputFileData) {
        try {
            logger.info("Inputs for industry user file save -> {}", inputFileData);
            List<FileOutputWebModel> outputFilesList = detailService.saveIndustryUserFiles(inputFileData);
            
            if (!Utility.isNullOrEmptyList(outputFilesList)) {
                Optional<User> verifiedUser = userService.getUser(inputFileData.getUserId());
                
                if (verifiedUser.isPresent()) {
                    String mailContent = "<p>Dear " + verifiedUser.get().getName() + ",</p>" +
                            "<p>Thank you for signing up with Film-hook Media Apps! We are currently reviewing your profile. Please note that our verification process can take up to 24 hours.</p>" +
                            "<p>If your profile does not meet the industry criteria and appears to match a public user profile, we will notify you with the necessary steps to complete your registration. We appreciate your patience and understanding as we ensure the best experience for all our users.</p>" +
                            "<p>If you have any questions or need assistance, feel free to reach out to our support team.</p>";

                    // Send success email
                    mailNotification.sendEmail(
                        verifiedUser.get().getName(),
                        verifiedUser.get().getEmail(),
                        "Profile Verification Update",
                        mailContent
                    );
                    
                    logger.info("Verification email sent successfully to {}", verifiedUser.get().getEmail());
                } else {
                    logger.warn("User not found or flag not set for ID: {}", inputFileData.getUserId());
                }

                return new Response(1, "File(s) saved successfully...", outputFilesList);
            }
        } catch (Exception e) {
            logger.error("Error at saveIndustryUserFiles() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return new Response(-1, "Error occurred while saving files...", null);
    }
    
    @RequestMapping(path = "/saveOneMinuteVideo", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response saveOneMinuteVideo(@ModelAttribute IndustryFileInputWebModel inputFileData) {
        try {
            logger.info("Inputs for industry user file save -> {}", inputFileData);
            
            List<FileOutputWebModel> outputFilesList = detailService.saveIndustryUserFiless(inputFileData);

  
            return new Response(1, "File(s) saved successfully...", outputFilesList);
            
        } catch (Exception e) {
            logger.error("Error at saveIndustryUserFiles() -> {}", e.getMessage(), e);
        }
        return new Response(-1, "Error occurred while saving files...", null);
    }
    
    @RequestMapping(path = "/saveGovermentIdProof", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response saverGovermentIdPoof(@ModelAttribute IndustryFileInputWebModel inputFileData) {
        try {
            logger.info("Inputs for industry user file save -> {}", inputFileData);
            
            List<FileOutputWebModel> outputFilesList = detailService.saveIndustryUserFilesss(inputFileData);

  
            return new Response(1, "File(s) saved successfully...", outputFilesList);
            
        } catch (Exception e) {
            logger.error("Error at saveIndustryUserFiles() -> {}", e.getMessage(), e);
        }
        return new Response(-1, "Error occurred while saving files...", null);
    }

   
    
    @GetMapping("/getIndustryFilesByUserId")
    public Response getIndustryFiles(@RequestParam("userId") Integer userId) {
        try {
            List<FileOutputWebModel> outputList = detailService.getIndustryFiles(userId);
            if (!Utility.isNullOrEmptyList(outputList)) {
                logger.info("[{}] Industry files found for userId :- {}", outputList.size(), userId);
                return new Response(1, "Industry file(s) found successfully...", outputList);
            } else {
                return new Response(-1, "No file(s) available for this user...", null);
            }
        } catch (Exception e) {
            logger.error("Error at getIndustryFiles() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return new Response(-1, "Files were not found...", null);
    }

    @GetMapping("/downloadIndustryFile")
    public ResponseEntity<?> downloadGalleryFile(@RequestParam("userId") Integer userId,
                                                 @RequestParam("category") String category,
                                                 @RequestParam("fileId") String fileId) {
        try {
            logger.info("downloadIndustryFile Input Category :- {}, File Id :- {}", category, fileId);
            Resource resource = detailService.getIndustryFile(userId, category, fileId);
            if (resource != null) {
                String contentType = "application/octet-stream";
                String headerValue = "attachment; filename=\"" + fileId + "\"";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            }
        } catch (Exception e) {
            logger.error("Error at downloadIndustryFile() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.internalServerError().build();
    }

//    @PostMapping("/updateIndustryUserPermanentDetails")
//	public ResponseEntity<?> updateIndustryUserPermanentDetails(@RequestBody List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels) {
//		try {
//			logger.info("updateIndustryUserPermanentDetails controller start");
//			return detailService.updateIndustryUserPermanentDetails(industryUserPermanentDetailWebModels);
//		} catch (Exception e) {
//			logger.error("updateIndustryUserPermanentDetails Method Exception: {}", e);
//			e.printStackTrace();
//			return ResponseEntity.ok(new Response(-1, "Fail", ""));
//		}
//	}

    @PostMapping("/updateIndustryUserPermanentDetails")
    public ResponseEntity<?> updateIndustryUserPermanentDetails(@RequestBody PlatformDetailDTO platformDetailDTO) {
        try {
            logger.info("updateIndustryUserPermanentDetails start");
            return detailService.updateIndustryUserPermanentDetails(platformDetailDTO);
        } catch (Exception e) {
            logger.error("updateIndustryUserPermanentDetails Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/emailSendFilmHookCode")
    public ResponseEntity<?> verifyFilmHookCode(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("emailSendFilmHookCode controller start");
            return detailService.verifyFilmHookCode(userWebModel);
        } catch (Exception e) {
            logger.error("emailSendFilmHookCode Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/verifyFilmHookCode")
    public ResponseEntity<?> verifyFilmHook(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("verifyFilmHookCode controller start");
            return detailService.verifyFilmHook(userWebModel);
        } catch (Exception e) {
            logger.error("verifyFilmHookCode Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }
    
    @GetMapping("/getIndustryByuserId")
    public ResponseEntity<?> getIndustryByUserId(@RequestParam("userId")Integer userId) {
        try {
            logger.info("getIndustryByUserId controller start");
            return detailService.getIndustryByuserId(userId);
        } catch (Exception e) {
            logger.error("getIndustryByUserId Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/deleteTemporaryDetails")
    public ResponseEntity<?> deleteTemporaryDetails(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("deleteTemporaryDetails controller start");
            return detailService.deleteTemporaryDetails(userWebModel.getUserId());
        } catch (Exception e) {
            logger.error("deleteTemporaryDetails Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }
    
    @Autowired
    MailNotification mailService;
    
    @GetMapping("/report")
    public String sendReport(@RequestParam String to) {
        mailService.sendOtpMail(to, "Today's report details with some HTML formatting.");
        return "Report mail sent!";
    }
}