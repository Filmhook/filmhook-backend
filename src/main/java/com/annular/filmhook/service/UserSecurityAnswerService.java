package com.annular.filmhook.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;

import com.annular.filmhook.webmodel.UserSecurityAnswerDTO;
import com.annular.filmhook.webmodel.UserWebModel;

@Service
public interface UserSecurityAnswerService {
	
	List<UserSecurityAnswerDTO> saveSecurityQuestions(List<UserSecurityAnswerDTO> dtoList, Integer loggedInUserId);
	  public Response getAllSecurityQuestions();
	  Response getUserSecurityQuestionsWithAnswers(Integer userId);
	  Response verifySecurityAnswers(  Integer userId, List<UserSecurityAnswerDTO> requestList);
	     Response sendSecurityEditOtp(Integer userId);
	     Response verifySecurityEditOtp(Integer userId, String otpInput) ;
	     ResponseEntity<?> changingPassword(UserWebModel userWebModel);
	     Response getUserSecurityQuestionsWithAnswers(String email);
}
