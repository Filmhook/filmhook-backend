package com.annular.filmHook.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.annular.filmHook.Response;
import com.annular.filmHook.model.RefreshToken;
import com.annular.filmHook.webModel.UserWebModel;

public interface UserService {

	ResponseEntity<?> register(UserWebModel userWebModel);

	boolean verify(String code);

	Response verifyExpiration(RefreshToken refreshToken);

	RefreshToken createRefreshToken(UserWebModel userWebModel);

	//ResponseEntity<?> forgotPassword(UserWebModel userWebModel, HttpServletRequest request);

	
}
