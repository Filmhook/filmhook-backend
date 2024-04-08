package com.annular.filmhook.service;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.RefreshToken;
import com.annular.filmhook.webmodel.UserWebModel;

public interface AuthenticationService {

	ResponseEntity<?> register(UserWebModel userWebModel, String request);

	Response verifyExpiration(RefreshToken refreshToken);

	RefreshToken createRefreshToken(UserWebModel userWebModel);

	ResponseEntity<?> verifyUser(UserWebModel userWebModel);

	ResponseEntity<?> forgotPassword(UserWebModel userWebModel, HttpServletRequest request);

	ResponseEntity<?> changingPassword(UserWebModel userWebModel);

	ResponseEntity<?> resendOtp(UserWebModel userWebModel);

	ResponseEntity<?> changePassword(UserWebModel userWebModel);

	boolean verify(String code);

	ResponseEntity<?> verifyEmailOtp(UserWebModel userWebModel);

	ResponseEntity<?> verifyForgotOtp(UserWebModel userWebModel);

	

	// ResponseEntity<?> forgotPassword(UserWebModel userWebModel,
	// HttpServletRequest request);

}
