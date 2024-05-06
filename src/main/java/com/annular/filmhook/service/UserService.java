package com.annular.filmhook.service;

import java.util.List;
import java.util.Optional;

import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.FileOutputWebModel;
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
}
