package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserMediaPin;
import com.annular.filmhook.model.UserProfilePin;
import com.annular.filmhook.repository.PinMediaRepository;
import com.annular.filmhook.repository.PinProfileRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.PinProfileService;
import com.annular.filmhook.webmodel.UserProfilePinWebModel;

@Service
public class PinProfileServiceImpl implements PinProfileService {

	@Autowired
	UserDetails userDetails;

	@Autowired
	PinProfileRepository pinProfileRepository;

	@Autowired
	PinMediaRepository pinMediaRepository;

	@Autowired
	UserRepository userRepository;

	private static final Logger logger = LoggerFactory.getLogger(PinProfileServiceImpl.class);

	@Override
	public ResponseEntity<?> addProfile(UserProfilePinWebModel userProfilePinWebModel) {
		try {
			UserProfilePin userProfilePin = new UserProfilePin();
			userProfilePin.setUserId(userDetails.userInfo().getId());
			userProfilePin.setPinProfileId(userProfilePinWebModel.getPinProfileId());
			userProfilePin.setCreatedBy(userDetails.userInfo().getId());
			userProfilePin.setStatus(true);

			pinProfileRepository.save(userProfilePin);

			return ResponseEntity.ok("Pin added su  ccessfully");
		} catch (Exception e) {
			logger.error("Error setting addPin: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Error setting addPin", e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> addMedia(UserProfilePinWebModel userProfilePinWebModel) {
		try {
			UserMediaPin userMediaPin = new UserMediaPin();
			userMediaPin.setUserId(userDetails.userInfo().getId());
			userMediaPin.setPinMediaId(userProfilePinWebModel.getPinMediaId());
			userMediaPin.setCreatedBy(userDetails.userInfo().getId());
			userMediaPin.setStatus(true);

			pinMediaRepository.save(userMediaPin);

			return ResponseEntity.ok("Pin added successfully");
		} catch (Exception e) {
			logger.error("Error setting addPin: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Error setting addPin", e.getMessage()));

		}

	}

	public ResponseEntity<?> getAllProfilePin() {
		try {
			logger.info("getAllProfilePin service start");

			List<UserProfilePin> userProfilePins = pinProfileRepository.findByUserId(userDetails.userInfo().getId());
			List<LinkedHashMap<String, Object>> responseList = new ArrayList<>();
			for (UserProfilePin userProfilePin : userProfilePins) {
				Integer pinProfileId = userProfilePin.getPinProfileId();
				if (pinProfileId != null) {
					Optional<User> userOptional = userRepository.findById(pinProfileId);

					if (userOptional.isPresent()) {
						User user = userOptional.get();
						LinkedHashMap<String, Object> pinData = new LinkedHashMap<>();
						pinData.put("pinProfileId", userProfilePin.getPinProfileId());
						pinData.put("userId", userProfilePin.getUserId());
						pinData.put("userName", user.getName());
						pinData.put("userGender", user.getGender());
						responseList.add(pinData);
					} else {
						logger.warn("User not found for pinProfileId: " + pinProfileId);
					}
				} else {
					logger.warn("pinProfileId is null for userProfilePin: " + userProfilePin);
				}
			}
			return ResponseEntity.ok(responseList);
		} catch (Exception e) {
			logger.error("getAllProfilePin service Method Exception {} ", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Fail", ""));
		}
	}

	@Override
	public ResponseEntity<?> getAllMediaPin() {
		try {
			logger.info("getAllMediaPin service start");

			List<UserMediaPin> userMediaPins = pinMediaRepository.findByUserId(userDetails.userInfo().getId());

			return ResponseEntity.ok(userMediaPins);
		} catch (Exception e) {
			logger.error("userMediaPins service Method Exception {} ", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Fail", ""));
		}
	}

	@Override
	public ResponseEntity<?> getByProfileId(UserProfilePinWebModel userProfilePinWebModel) {
		try {
			logger.info("getByProfileId service start");

			Integer profileId = userProfilePinWebModel.getPinProfileId();
			if (profileId != null) {
				Optional<UserProfilePin> userProfilePinOptional = pinProfileRepository.findById(profileId);

				if (userProfilePinOptional.isPresent()) {
					UserProfilePin userProfilePin = userProfilePinOptional.get();
					Optional<User> userOptional = userRepository.findById(userProfilePin.getPinProfileId());

					if (userOptional.isPresent()) {
						User user = userOptional.get();
						LinkedHashMap<String, Object> responseData = new LinkedHashMap<>();
						responseData.put("pinProfileId", userProfilePin.getPinProfileId());
						// responseData.put("userId", userProfilePin.getUserId());
						responseData.put("userName", user.getName());
						responseData.put("userGender", user.getGender());
						// Add other details as needed
						return ResponseEntity.ok(responseData);
					} else {
						logger.warn("User not found for pinProfileId: " + userProfilePin.getPinProfileId());
					}
				} else {
					logger.warn("UserProfilePin not found for profileId: " + profileId);
				}
			} else {
				logger.warn("profileId is null in request");
			}

			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			logger.error("getByProfileId service Method Exception {} ", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Fail", ""));
		}

	}

	@Override
	public ResponseEntity<?> profilePinStatus(UserProfilePinWebModel userProfilePinWebModel) {
		try {
			logger.info("changePinStatus service start");

			Optional<UserProfilePin> pinOptional = pinProfileRepository
					.findById(userProfilePinWebModel.getUserProfilePinId());
			if (pinOptional.isPresent()) {
				UserProfilePin userProfilePin = pinOptional.get();
				userProfilePin.setStatus(userProfilePinWebModel.isStatus());

				pinProfileRepository.save(userProfilePin);

				return ResponseEntity.ok("Pin status changed successfully");
			} else {

				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error("changePinStatus service Method Exception {} ", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Fail", ""));
		}
	}

	@Override
	public ResponseEntity<?> mediaPinStatus(UserProfilePinWebModel userProfilePinWebModel) {
		try {
			logger.info("changePinStatus service start");

			Optional<UserMediaPin> pinOptional = pinMediaRepository
					.findById(userProfilePinWebModel.getUserMediaPinId());
			if (pinOptional.isPresent()) {
				UserMediaPin userProfilePin = pinOptional.get();
				userProfilePin.setStatus(userProfilePinWebModel.isStatus());

				pinMediaRepository.save(userProfilePin);

				return ResponseEntity.ok("Pin status changed successfully");
			} else {

				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error("changePinStatus service Method Exception {} ", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1, "Fail", ""));
		}
	}
}
