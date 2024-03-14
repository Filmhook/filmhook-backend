package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.UserWebModel;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserWebModel> getAllUsers();

    Optional<UserWebModel> getUserByUserId(Integer userId);

    Optional<?> updateBiographyData(UserWebModel userWebModel);

    Optional<?> updateBiologicalData(UserWebModel userWebModel);

    Optional<?> updatePersonalInformation(UserWebModel userWebModel);

    Optional<?> updateEducationInformation(UserWebModel userWebModel);

    Optional<?> updateProfessionInformation(UserWebModel userWebModel);
}
