package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.PinProfileService;

import com.annular.filmhook.webmodel.UserProfilePinWebModel;

@RestController
@RequestMapping("/pin")
public class PinProfileController {

	public static final Logger logger = LoggerFactory.getLogger(PinProfileController.class);
	
    @Autowired
    PinProfileService pinProfileService;

    @PostMapping("/addPin")
    public ResponseEntity<?> addPin(@RequestBody UserProfilePinWebModel userProfilePinWebModel) {
        try {
            logger.info("addPin controller start");
            
            if (userProfilePinWebModel.getFlag() == 0) {
                return pinProfileService.addProfile(userProfilePinWebModel);
            } else if (userProfilePinWebModel.getFlag() == 1) {
                return pinProfileService.addMedia(userProfilePinWebModel);
            } else {
                return ResponseEntity.badRequest().body(new Response(-1, "Invalid flag value", ""));
            }
        } catch (Exception e) {
            logger.error("addPin Method Exception {} ", e);
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }
    
	@PostMapping("/getAllProfilePin")
	public ResponseEntity<?> getAllProfilePin() {
		try {
			logger.info("getAllMenu controller start");
			return pinProfileService.getAllProfilePin();
		} catch (Exception e) {
			logger.error("getAllProfilePin Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}
	
	@PostMapping("/getAllMediaPin")
	public ResponseEntity<?> getAllMediaPin() {
		try {
			logger.info("getAllMediaPin controller start");
			return pinProfileService.getAllMediaPin();
		} catch (Exception e) {
			logger.error("getAllMediaPin Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}
	@PostMapping("/getByProfileId")
	public ResponseEntity<?> getByProfileId(@RequestBody UserProfilePinWebModel userProfilePinWebModel) {
		try {
			logger.info("getAllMediaPin controller start");
			return pinProfileService.getByProfileId(userProfilePinWebModel);
		} catch (Exception e) {
			logger.error("userProfilePinWebModel Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

    
    
	@PostMapping("/profilePinStatus")
	public ResponseEntity<?> profilePinStatus(@RequestBody UserProfilePinWebModel userProfilePinWebModel) {
		try {
			logger.info("profilePinStatus controller start");
			return pinProfileService.profilePinStatus(userProfilePinWebModel);
		} catch (Exception e) {
			logger.error("profilePinStatus Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}
    
	@PostMapping("/mediaPinStatus")
	public ResponseEntity<?> mediaPinStatus(@RequestBody UserProfilePinWebModel userProfilePinWebModel) {
		try {
			logger.info("mediaPinStatus controller start");
			return pinProfileService.mediaPinStatus(userProfilePinWebModel);
		} catch (Exception e) {
			logger.error("mediaPinStatus Method Exception {}" + e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}
	
	
}
