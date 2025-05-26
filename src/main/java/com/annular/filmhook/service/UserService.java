package com.annular.filmhook.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.model.Location;
import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.LocationWebModel;
import com.annular.filmhook.webmodel.UserSearchWebModel;
import com.annular.filmhook.webmodel.UserWebModel;

public interface UserService {

    List<UserWebModel> getAllUsers();

    Optional<UserWebModel> getUserByUserId(Integer userId);

    Optional<User> getUser(Integer userId);

    Optional<?> updateBiographyData(UserWebModel userWebModel);

    Optional<?> updateBiologicalData(UserWebModel userWebModel);

    Optional<?> updatePersonalInformation(UserWebModel userWebModel);

    Optional<?> updateEducationInformation(UserWebModel userWebModel);

    Optional<?> updateProfessionInformation(UserWebModel userWebModel);

    FileOutputWebModel saveProfilePhoto(UserWebModel userWebModel);

    FileOutputWebModel getProfilePic(UserWebModel userWebModel);

    void deleteUserProfilePic(UserWebModel userWebModel);

    List<FileOutputWebModel> saveCoverPhoto(UserWebModel userWebModel);

    List<FileOutputWebModel> getCoverPic(UserWebModel userWebModel);

    void deleteUserCoverPic(UserWebModel userWebModel);

    List<UserSearchWebModel> getAllIndustryByCountryIds(List<Integer> countryId);

    List<UserSearchWebModel> getAllProfessionByPlatformId(Integer platformId);

    List<UserSearchWebModel> getAllSubProfessionByProfessionId(List<Integer> professionIds);

    Map<String, List<Map<String, Object>>> getUserByAllSearchCriteria(UserSearchWebModel searchWebModel);

    ResponseEntity<?> getAllAddressListOnSignUp();

    ResponseEntity<?> getAddressListOnSignUp(String address);

    Optional<?> updateUserName(UserWebModel userWebModel);

    Optional<?> getUserId(UserWebModel userWebModel);

    String getProfilePicUrl(Integer userId);

    List<UserWebModel> getUserByName(String name);

    Optional<Location> saveUserLocation(LocationWebModel locationWebModel);

   // List<Map<String, Object>> findNearByUsers(Integer userId, Integer range, String profession);

    Optional<User> changePrimaryEmaiId(UserWebModel userWebModel);

    Optional<User> changePrimaryEmaiIdVerified(UserWebModel userWebModel);

	ResponseEntity<?> getNewAddressListOnSignUp(String address);

	ResponseEntity<?> getLocationByuserId(Integer userId);

	List<Map<String, Object>> findNearByUsers(Integer userId);

	ResponseEntity<?> deactivateUserId(Integer userId, String password);

	ResponseEntity<?> saveDeleteReason(UserWebModel userWebModel);

	ResponseEntity<?> getDeleteStatus(UserWebModel userWebModel);

	ResponseEntity<?> confirmdeleteUserId(Integer userId, String password);

	ResponseEntity<?> updateRerferrralcode(UserWebModel userWebModel);

	ResponseEntity<?> getReferralCodeByUserId(Integer userId);

	ResponseEntity<?> addLocation(UserWebModel userWebModel);

}
