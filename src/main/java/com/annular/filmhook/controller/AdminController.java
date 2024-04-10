package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AdminService;
import com.annular.filmhook.webmodel.UserWebModel;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	public static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AdminService adminService;
	
	@PostMapping("adminRegister")
	public ResponseEntity<?> userRegister(@RequestBody UserWebModel userWebModel) {
		try {
			logger.info("User :- " + userWebModel);
			return adminService.userRegister(userWebModel);
		} catch (Exception e) {
			logger.error("userRegister Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}
	
	@PostMapping("updateRegister")
	public ResponseEntity<?> updateRegister(@RequestBody UserWebModel userWebModel) {
		try {
			logger.info("User :- " + userWebModel);
			return adminService.updateRegister(userWebModel);
		} catch (Exception e) {
			logger.error("updateRegister Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@PostMapping("deleteRegister")
	public ResponseEntity<?> deleteRegister(@RequestBody UserWebModel userWebModel) {
		try {
			logger.info("User :- " + userWebModel);
			return adminService.deleteRegister(userWebModel);
		} catch (Exception e) {
			logger.error("deleteRegister Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@PostMapping("getRegister")
	public ResponseEntity<?> getRegister(@RequestBody UserWebModel userWebModel) {
		try {
			logger.info("User :- " + userWebModel);
			return adminService.getRegister(userWebModel);
		} catch (Exception e) {
			logger.error("getRegister Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@PostMapping("adminPageStatus")
	public ResponseEntity<?> adminPageStatus(@RequestBody UserWebModel userWebModel) {
		try {
			logger.info("User :- " + userWebModel);
			return adminService.adminPageStatus(userWebModel);
		} catch (Exception e) {
			logger.error("adminPageStatus Method Exception...", e);
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

}
