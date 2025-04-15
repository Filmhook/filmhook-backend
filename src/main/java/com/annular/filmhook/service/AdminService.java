package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.Response;
import com.annular.filmhook.webmodel.UserWebModel;

public interface AdminService {

    ResponseEntity<?> userRegister(UserWebModel userWebModel);

    ResponseEntity<?> updateRegister(UserWebModel userWebModel);

    ResponseEntity<?> deleteRegister(UserWebModel userWebModel);

    ResponseEntity<?> getRegister(UserWebModel userWebModel);

    ResponseEntity<?> adminPageStatus(UserWebModel userWebModel);

    Response getAllUnverifiedIndustrialUsers(UserWebModel userWebModel);

    ResponseEntity<?> getIndustryUserPermanentDetails(UserWebModel userWebModel);

    Response changeStatusUnverifiedIndustrialUsers(UserWebModel userWebModel);

	Response getAllUsers(Integer page, Integer size);

	Response getAllUsersByUserType(String userType, Integer page, Integer size);

	Response getAllUsersManagerCount();

	Response getAllReportPostCount();

	Response getAllPaymentUserData(Integer page, Integer size);

	Response getAllPaymentStatusCount();

	Response getAllPaymentStatus(String status);

}
