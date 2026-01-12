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
import com.annular.filmhook.model.AdminUser;
import com.annular.filmhook.repository.AdminUserRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.security.jwt.JwtUtils;
import com.annular.filmhook.service.AdminService;
import com.annular.filmhook.webmodel.AdminRegisterRequest;
import com.annular.filmhook.webmodel.UserWebModel;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/admin")
public class AdminController {

	public static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	AdminService adminService;
	
    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    AdminUserRepository adminUserRepository;

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
    
    @GetMapping("/roles")
    public ResponseEntity<Map<String, Object>> getRoles() {
        return ResponseEntity.ok(adminService.getRoles());
    }

    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerAdmin(
            @RequestBody AdminRegisterRequest userWebModel) {
        return adminService.registerAdmin(userWebModel);
    }
    
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, Object>> login(
//            @RequestBody AdminRegisterRequest userWebModel) {
//        return adminService.login(userWebModel);
//    }
    
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody AdminRegisterRequest req) {

        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        AdminUser admin = adminUserRepository.findByEmail(req.getEmail()).orElseThrow();

        String jwt = jwtUtils.generateAdminJwt(
            admin.getEmail(),
            admin.getRole().getRoleCode()
        );

        return ResponseEntity.ok(Map.of(
            "status", 1,
            "jwt", jwt,
            "role", admin.getRole().getRoleCode()
        ));
    }

    
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {
        return adminService.verifyOtp(email, otp);
    }
}
