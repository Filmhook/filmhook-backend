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
import java.util.Optional;
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
		// ✅ 1. Validate user
		User user = userRepository.findById(model.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		// ✅ 2. Check duplicate company name and designation for same user
		Optional<UserWorkExperience> duplicateCheck = repository
				.findByUserAndCompanyNameIgnoreCaseAndDesignationIgnoreCase(
						user, model.getCompanyName(), model.getDesignation());

		if (duplicateCheck.isPresent()) {
			throw new RuntimeException("Work experience for this company and designation already exists.");
		}

		// ✅ 3. Validate currently working and end date logic
		if (Boolean.TRUE.equals(model.getCurrentlyWorking()) && model.getEndDate() != null) {
			throw new RuntimeException("End date should be empty if currently working is true.");
		}

		if (Boolean.FALSE.equals(model.getCurrentlyWorking()) && model.getEndDate() == null) {
			throw new RuntimeException("End date is required if currently working is false.");
		}

		// ✅ 4. Build and save entity
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

		// ✅ 5. Return updated model with ID
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
