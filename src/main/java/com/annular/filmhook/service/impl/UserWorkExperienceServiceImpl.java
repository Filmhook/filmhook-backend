package com.annular.filmhook.service.impl;


import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserWorkExperience;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.repository.UserWorkExperienceRepository;
import com.annular.filmhook.service.UserWorkExperienceService;
import com.annular.filmhook.webmodel.UserWorkExperienceWebModel;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserWorkExperienceServiceImpl implements UserWorkExperienceService {
	
	@Autowired
	UserWorkExperienceRepository repository;

	@Autowired
   UserRepository userRepository;

    @Override
    public UserWorkExperienceWebModel saveUserWorkExperience(UserWorkExperienceWebModel model) {
        User user = userRepository.findById(model.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserWorkExperience experience = UserWorkExperience.builder()
                .companyName(model.getCompanyName())
                .designation(model.getDesignation())
                .companyLocation(model.getCompanyLocation())
                .startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .currentlyWorking(model.getCurrentlyWorking())
                .user(user)
                .build();

        repository.save(experience);

        model.setId(experience.getId());
        return model;
    }

    @Override
    public List<UserWorkExperienceWebModel> getUserWorkExperience(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return repository.findByUser(user).stream()
                .map(e -> UserWorkExperienceWebModel.builder()
                        .id(e.getId())
                        .companyName(e.getCompanyName())
                        .designation(e.getDesignation())
                        .companyLocation(e.getCompanyLocation())
                        .startDate(e.getStartDate())
                        .endDate(e.getEndDate())
                        .currentlyWorking(e.getCurrentlyWorking())
                        .userId(userId)
                        .build())
                .collect(Collectors.toList());
    }
}
