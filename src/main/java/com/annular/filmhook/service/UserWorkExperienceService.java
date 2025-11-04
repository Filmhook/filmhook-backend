package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.UserWorkExperienceWebModel;
import java.util.List;

public interface UserWorkExperienceService {
    UserWorkExperienceWebModel saveUserWorkExperience(UserWorkExperienceWebModel model);
    List<UserWorkExperienceWebModel> getUserWorkExperience(Integer userId);
	boolean deleteUserWorkExperience(Integer experienceId);
}
