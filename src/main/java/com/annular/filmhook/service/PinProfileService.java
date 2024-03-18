package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.UserProfilePinWebModel;

public interface PinProfileService {

	ResponseEntity<?> addProfile(UserProfilePinWebModel userProfilePinWebModel);

	ResponseEntity<?> addMedia(UserProfilePinWebModel userProfilePinWebModel);

	ResponseEntity<?> getAllProfilePin();

	ResponseEntity<?> getAllMediaPin();

	ResponseEntity<?> getByProfileId(UserProfilePinWebModel userProfilePinWebModel);

	ResponseEntity<?> profilePinStatus(UserProfilePinWebModel userProfilePinWebModel);

	ResponseEntity<?> mediaPinStatus(UserProfilePinWebModel userProfilePinWebModel);

}
