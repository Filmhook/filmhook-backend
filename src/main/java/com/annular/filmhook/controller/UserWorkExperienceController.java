package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.UserWorkExperienceService;
import com.annular.filmhook.webmodel.UserWorkExperienceWebModel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-work-experience")
@RequiredArgsConstructor
public class UserWorkExperienceController {

	private static final Logger logger = LoggerFactory.getLogger(UserWorkExperienceController.class);
	@Autowired
	UserWorkExperienceService service;


	@PostMapping("/save")
	public ResponseEntity<Response> saveWorkExperience(@RequestBody UserWorkExperienceWebModel model) {
		try {
			UserWorkExperienceWebModel savedData = service.saveUserWorkExperience(model);
			logger.info("Saving work experience for userId: {}", model.getUserId());
			return ResponseEntity.ok(new Response(1, "Work experience saved successfully", savedData));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new Response(0, "Failed to save work experience: " + e.getMessage(), null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(0, "Internal server error: " + e.getMessage(), null));
		}
	}

	@GetMapping("/{userId}")
	public ResponseEntity<Response> getUserWorkExperience(@PathVariable Integer userId) {
		try {
			List<UserWorkExperienceWebModel> experiences = service.getUserWorkExperience(userId);
			if (experiences.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(0, "No work experience found for userId: " + userId, null));
			}
			return ResponseEntity.ok(new Response(1, "Work experience fetched successfully", experiences));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new Response(0, "Failed to fetch data: " + e.getMessage(), null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(0, "Internal server error: " + e.getMessage(), null));
		}
	}
}
