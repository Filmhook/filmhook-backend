package com.annular.filmhook.controller;

import java.util.Map;

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
            @RequestParam("pageSize") Integer size,@RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        try {
            logger.info("getAllUsers controller start - Page: {}, Size: {}", page, size);
            return adminService.getAllUsers(page, size, startDate,endDate);
        } catch (Exception e) {
            logger.error("getAllUsers Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error occurred", "");
        }
    }

    @GetMapping("getAllUsersByUserType")
	public Response getAllUsersByUserType(@RequestParam("userType") String userType,@RequestParam("pageNo") Integer page,
            @RequestParam("pageSize") Integer size,@RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
		try {
			logger.info("getAllUsersByUserType controller start");
			return adminService.getAllUsersByUserType(userType,page,size,startDate,endDate);
		} catch (Exception e) {
			logger.error("getAllUsersByUserType Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}
    

    @GetMapping("getAllAdminUsersByUserType")
	public Response getAllAdminUsersByUserType(@RequestParam("userType") String userType,@RequestParam("pageNo") Integer page,
            @RequestParam("pageSize") Integer size) {
		try {
			
			logger.info("getAllAdminUsersByUserType controller start");
			return adminService.getAllAdminUsersByUserType(userType,page,size);
		} catch (Exception e) {
			logger.error("getAllAdminUsersByUserType Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}
    @GetMapping("getAllUsersManagerCount")
    public Response getAllUsersManagerCount(@RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
		try {
			logger.info("getAllUsersManagerCount controller start");
			return adminService.getAllUsersManagerCount(startDate,endDate);
		} catch (Exception e) {
			logger.error("getAllUsersManagerCount Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}

    @GetMapping("getAllReportPostCount")
	public Response getAllReportPostCount(@RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
		try {
			logger.info("getAllReportPostCount controller start");
			return adminService.getAllReportPostCount(startDate,endDate);
		} catch (Exception e) {
			logger.error("getAllReportPostCount Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}
    
    @GetMapping("getAllPaymentUserData")
	public Response getAllPaymentUserData(@RequestParam("pageNo") Integer page,
            @RequestParam("pageSize") Integer size,@RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
		try {
			logger.info("getAllPaymentUserData controller start");
			return adminService.getAllPaymentUserData(page,size,startDate,endDate);
		} catch (Exception e) {
			logger.error("getAllPaymentUserData Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}
    
    @GetMapping("getAllPaymentStatusCount")
	public Response getAllPaymentStatusCount(@RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
		try {
			logger.info("getAllPaymentStatusCount controller start");
			return adminService.getAllPaymentStatusCount(startDate,endDate);
		} catch (Exception e) {
			logger.error("getAllPaymentStatusCount Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}
    
    
    @GetMapping("getAllPaymentStatus")
	public Response getAllPaymentStatus(@RequestParam("status")String status,@RequestParam("pageNo") Integer page,
            @RequestParam("pageSize") Integer size,@RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
		try {
			logger.info("getAllPaymentStatusCount controller start");
			return adminService.getAllPaymentStatus(status,page,size,startDate,endDate);
		} catch (Exception e) {
			logger.error("getAllPaymentStatusCount Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}
    
    @GetMapping("getAllUnVerifiedRejectedList")
	public Response getAllUnVerifiedRejectedList(@RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize,@RequestParam("status")Boolean status) {
		try {
			logger.info("getAllUnVerifiedRejectedList controller start");
			return adminService.getAllUnVerifiedRejectedList(pageNo,pageSize,status);
		} catch (Exception e) {
			logger.error("getAllUnVerifiedRejectedList Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}
    
    @PostMapping("changeNotificationStatus")
	public Response changeNotificationStatus() {
		try {
			logger.info("changeNotificationStatus controller start");
			return adminService.changeNotificationStatus();
		} catch (Exception e) {
			logger.error("changeNotificationStatus Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}

    @GetMapping("getTotalNotificationCount")
	public Response getTotalNotificationCount() {
		try {
			logger.info("getTotalNotificationCount controller start");
			return adminService.getTotalNotificationCount();
		} catch (Exception e) {
			logger.error("getTotalNotificationCount Method Exception -> {}", e.getMessage());
			e.printStackTrace();
		}
		return new Response(-1, "Success", "");
	}
    
    @PostMapping("changeNotificationStatusByIndustryUsers")
   	public Response changeNotificationStatusByIndustryUsers() {
   		try {
   			logger.info("changeNotificationStatusByIndustryUsers controller start");
   			return adminService.changeNotificationStatusByIndustryUsers();
   		} catch (Exception e) {
   			logger.error("changeNotificationStatusByIndustryUsers Method Exception -> {}", e.getMessage());
   			e.printStackTrace();
   		}
   		return new Response(-1, "Success", "");
   	}

    @GetMapping("getIndustryUserCount")
   	public Response getIndustryUserCount() {
   		try {
   			logger.info("getIndustryUserCount controller start");
   			return adminService.getIndustryUserCount();
   		} catch (Exception e) {
   			logger.error("getIndustryUserCount Method Exception -> {}", e.getMessage());
   			e.printStackTrace();
   		}
   		return new Response(-1, "Success", "");
   	}
    
    @GetMapping("/generate-password")
    public ResponseEntity<Map<String, Object>> generatePassword() {
        return ResponseEntity.ok(adminService.generatePassword());
    }
       
}
