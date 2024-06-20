package com.annular.filmhook.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.AddressListWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
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

    ResponseEntity<?> getAddressListOnSignUp(AddressListWebModel addressListWebModel);

    Optional<?> updateUserName(UserWebModel userWebModel);

    Optional<?> getUserId(UserWebModel userWebModel);

    String getProfilePicUrl(Integer userId);

    List<UserWebModel> getUserByName(String name);
}
