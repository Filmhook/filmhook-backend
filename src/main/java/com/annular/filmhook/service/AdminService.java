package com.annular.filmhook.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.AdminRefreshToken;
import com.annular.filmhook.webmodel.AdminRegisterRequest;
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

	Response getAllUsers(Integer page, Integer size, String startDate, String endDate);

	Response getAllUsersByUserType(String userType, Integer page, Integer size, String startDate, String endDate);

	Response getAllUsersManagerCount(String startDate, String endDate);

	Response getAllReportPostCount(String startDate, String endDate);

	Response getAllPaymentUserData(Integer page, Integer size, String startDate, String endDate);

	Response getAllPaymentStatusCount(String startDate, String endDate);

	Response getAllPaymentStatus(String status, Integer page, Integer size, String startDate, String endDate);

	Response getAllUnVerifiedRejectedList(Integer pageNo, Integer pageSize, Boolean status);

	Response changeNotificationStatus();

	Response getTotalNotificationCount();

	Response changeNotificationStatusByIndustryUsers();

	Response getIndustryUserCount();

	Response getAllAdminUsersByUserType(String userType, Integer page, Integer size);

	Map<String, Object> generatePassword();

}
