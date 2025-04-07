package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
			return adminService.userRegister(userWebModel);
		} catch (Exception e) {
			logger.error("userRegister Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@PostMapping("updateRegister")
	public ResponseEntity<?> updateRegister(@RequestBody UserWebModel userWebModel) {
		try {
			return adminService.updateRegister(userWebModel);
		} catch (Exception e) {
			logger.error("updateRegister Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@PostMapping("deleteRegister")
	public ResponseEntity<?> deleteRegister(@RequestBody UserWebModel userWebModel) {
		try {
			return adminService.deleteRegister(userWebModel);
		} catch (Exception e) {
			logger.error("deleteRegister Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@PostMapping("getRegister")
	public ResponseEntity<?> getRegister(@RequestBody UserWebModel userWebModel) {
		try {
			return adminService.getRegister(userWebModel);
		} catch (Exception e) {
			logger.error("getRegister Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@PostMapping("adminPageStatus")
	public ResponseEntity<?> adminPageStatus(@RequestBody UserWebModel userWebModel) {
		try {
			return adminService.adminPageStatus(userWebModel);
		} catch (Exception e) {
			logger.error("adminPageStatus Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(new Response(-1, "Fail", ""));
	}

	@PostMapping("getAllUnverifiedIndustrialUsers")
	public Response getAllUnverifiedIndustrialUsers(@RequestBody UserWebModel userWebModel) {
		try {
			logger.info("getAllUnverifiedIndustrialUsers controller start");
			return adminService.getAllUnverifiedIndustrialUsers(userWebModel);
		} catch (Exception e) {
			logger.error("getAllUnverifiedIndustrialUsers Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}
	

    @PostMapping("/getAdminIndustryUserPermanentDetails")
    public ResponseEntity<?> getIndustryUserPermanentDetails(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("getAdminIndustryUserPermanentDetails controller start");
            return adminService.getIndustryUserPermanentDetails(userWebModel);
        } catch (Exception e) {
            logger.error("getIndustryUserPermanentDetails Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }
    
    @PostMapping("changeStatusUnverifiedIndustrialUsers")
	public Response changeStatusUnverifiedIndustrialUsers(@RequestBody UserWebModel userWebModel) {
		try {
			logger.info("changeStatusUnverifiedIndustrialUsers controller start");
			return adminService.changeStatusUnverifiedIndustrialUsers(userWebModel);
		} catch (Exception e) {
			logger.error("changeStatusUnverifiedIndustrialUsers Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}
    @GetMapping("getAllUsers")
    public Response getAllUsers(
            @RequestParam("pageNo") Integer page,
            @RequestParam("pageSize") Integer size) {
        try {
            logger.info("getAllUsers controller start - Page: {}, Size: {}", page, size);
            return adminService.getAllUsers(page, size);
        } catch (Exception e) {
            logger.error("getAllUsers Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error occurred", "");
        }
    }

    @GetMapping("getAllUsersByUserType")
	public Response getAllUsersByUserType(@RequestParam("userType") String userType,@RequestParam("pageNo") Integer page,
            @RequestParam("pageSize") Integer size) {
		try {
			logger.info("getAllUsersByUserType controller start");
			return adminService.getAllUsersByUserType(userType,page,size);
		} catch (Exception e) {
			logger.error("getAllUsersByUserType Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}
    @GetMapping("getAllUsersManagerCount")
	public Response getAllUsersManagerCount() {
		try {
			logger.info("getAllUsersManagerCount controller start");
			return adminService.getAllUsersManagerCount();
		} catch (Exception e) {
			logger.error("getAllUsersManagerCount Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}

    @GetMapping("getAllReportPostCount")
	public Response getAllReportPostCount() {
		try {
			logger.info("getAllReportPostCount controller start");
			return adminService.getAllReportPostCount();
		} catch (Exception e) {
			logger.error("getAllReportPostCount Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}
    
    @GetMapping("getAllPaymentUserData")
	public Response getAllPaymentUserData(@RequestParam("pageNo") Integer page,
            @RequestParam("pageSize") Integer size) {
		try {
			logger.info("getAllPaymentUserData controller start");
			return adminService.getAllPaymentUserData(page,size);
		} catch (Exception e) {
			logger.error("getAllPaymentUserData Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}
    
    @GetMapping("getAllPaymentStatusCount")
	public Response getAllPaymentStatusCount() {
		try {
			logger.info("getAllPaymentStatusCount controller start");
			return adminService.getAllPaymentStatusCount();
		} catch (Exception e) {
			logger.error("getAllPaymentStatusCount Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}


}
