package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.RefreshToken;
import com.annular.filmhook.webmodel.UserWebModel;

public interface UserService {

	ResponseEntity<?> register(UserWebModel userWebModel);

	boolean verify(String code);

	Response verifyExpiration(RefreshToken refreshToken);

	RefreshToken createRefreshToken(UserWebModel userWebModel);

	// ResponseEntity<?> forgotPassword(UserWebModel userWebModel, HttpServletRequest request);

}
