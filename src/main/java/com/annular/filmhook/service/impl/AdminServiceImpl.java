package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AdminService;
import com.annular.filmhook.webmodel.UserWebModel;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	UserRepository userRepository;

	public static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

	@Override
	public ResponseEntity<?> userRegister(UserWebModel userWebModel) {
		
		
		try {
			logger.info("Admin Register method start");
			Optional<User> userData = userRepository.findByEmailIdAndUserType(userWebModel.getEmail(),
					userWebModel.getUserType());
			BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
			if (!userData.isPresent()) {
				User user = new User();
				user.setName(userWebModel.getName());
				user.setEmail(userWebModel.getEmail());
				user.setUserType(userWebModel.getUserType());
				user.setStatus(userWebModel.isStatus());
				String encryptPwd = bcrypt.encode(userWebModel.getPassword());
				user.setPassword(encryptPwd);

				// Save the user entity in the database
				userRepository.save(user);
				 return ResponseEntity.status(HttpStatus.OK)
		                    .body(new Response(1, "Profile Created Successfully", user));

				
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
						new Response(-1, "User already exists", "User with this email and userType already exists"));
			}
		} catch (Exception e) {
			logger.error("Register Method Exception...", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to create profile", e.getMessage()));
		}
		
	}

	@Override
	public ResponseEntity<?> updateRegister(UserWebModel userWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		try {
			logger.info("Admin Update Register method start");

			// Fetch user data by userId
			Optional<User> userData = userRepository.findById(userWebModel.getUserId());

			// Check if the user exists
			if (userData.isPresent()) {
				User user = userData.get();
				user.setName(userWebModel.getName());
				user.setEmail(userWebModel.getEmail());
				user.setUserType(userWebModel.getUserType());

				// Only update password if it's provided in the request
				if (userWebModel.getPassword() != null && !userWebModel.getPassword().isEmpty()) {
					BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
					String encryptPwd = bcrypt.encode(userWebModel.getPassword());
					user.setPassword(encryptPwd);
				}

				userRepository.save(user);
				response.put("message", "User profile updated successfully.");

				return ResponseEntity.status(HttpStatus.OK).body(response);
			} else {
				// User with provided userId not found
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(-1, "User not found", "User with provided userId does not exist."));
			}
		} catch (Exception e) {
			logger.error("Update Register Method Exception...", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to update profile", e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> deleteRegister(UserWebModel userWebModel) {
		try {
			logger.info("Admin Delete Register method start");
			Optional<User> userData = userRepository.findById(userWebModel.getUserId());
			if (userData.isPresent()) {
				User user = userData.get();
				user.setStatus(false);
				userRepository.save(user);
				return ResponseEntity.status(HttpStatus.OK).body(new Response(1, "User marked as deleted", null));
			} else {

				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(-1, "User not found", "User with provided userId does not exist."));
			}
		} catch (Exception e) {
			logger.error("Delete Register Method Exception...", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to mark user as deleted", e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> getRegister(UserWebModel userWebModel) {
		try {
			logger.info("Admin Get Register method start");
			List<User> subAdminUsers = userRepository.findByUserType(userWebModel.getUserType());
			if (!subAdminUsers.isEmpty()) {
				List<HashMap<String, Object>> responseList = new ArrayList<>();
				for (User user : subAdminUsers) {
					HashMap<String, Object> userInfo = new HashMap<>();
					userInfo.put("userId", user.getUserId());
					userInfo.put("name", user.getName());
					userInfo.put("email", user.getEmail());

					responseList.add(userInfo);
				}
				return ResponseEntity.status(HttpStatus.OK)
						.body(new Response(1, "Sub-admin users retrieved successfully", responseList));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(-1, "No sub-admin users found", "No sub-admin users exist in the system."));
			}
		} catch (Exception e) {
			logger.error("Get Register Method Exception...", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to retrieve sub-admin users", e.getMessage()));
		}
	}

}
