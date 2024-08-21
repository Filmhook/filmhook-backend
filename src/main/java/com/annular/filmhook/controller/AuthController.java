package com.annular.filmhook.controller;

import java.time.LocalTime;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserStatusConfig;
import com.annular.filmhook.model.RefreshToken;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.RefreshTokenRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.security.UserDetailsImpl;
import com.annular.filmhook.security.jwt.JwtResponse;
import com.annular.filmhook.security.jwt.JwtUtils;
import com.annular.filmhook.service.AuthenticationService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.HelpAndSupportWebModel;
import com.annular.filmhook.webmodel.UserWebModel;

import com.annular.filmhook.util.Utility;

@RestController
@RequestMapping("/user")
public class AuthController {

    public static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserStatusConfig loginConstants;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    UserService userServices;

    @PostMapping("register")
    public ResponseEntity<?> userRegister(@RequestBody UserWebModel userWebModel, String request) {
        try {
            logger.info("User details to register :- {}", userWebModel);
            return userService.register(userWebModel, request);
        } catch (Exception e) {
            logger.error("userRegister Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("emailNotification")
    public ResponseEntity<?> emailNotification(@RequestBody UserWebModel userWebModel, String request) {
        try {
            logger.info("emailNotification to register :- {}", userWebModel);
            return userService.emailNotification(userWebModel, request);
        } catch (Exception e) {
            logger.error("emailNotification Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @GetMapping("verifyUser")
    public Response verifyUser(@RequestParam("code") String code) {
        try {
            logger.info("verifyUser start");
            boolean verificationResult = userService.verify(code);
            if (verificationResult) {
                logger.info("Verified successfully");
                return new Response(1, "Verify Success", "");
            } else {
                logger.info("Verification failed");
                return new Response(-1, "Verify Failed", "");
            }
        } catch (Exception e) {
            logger.error("Error occurred in verifyUser method -> {}", e.getMessage());
            return new Response(-1, "An error occurred", "");
        }
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody UserWebModel userWebModel) {
        try {
            Optional<User> checkUsername = userRepository.findByEmail(userWebModel.getEmail());
            if (checkUsername.isPresent()) {
                loginConstants.setUserType(userWebModel.getUserType());
                logger.info("In login() User type from constants -> {}", loginConstants.getUserType());

                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userWebModel.getEmail(), userWebModel.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                RefreshToken refreshToken = userService.createRefreshToken(userWebModel);

                User user = checkUsername.get();
                // Update device token if provided
                if (!Utility.isNullOrBlankWithTrim(userWebModel.getFirebaseDeviceToken())) {
                    user.setFirebaseDeviceToken(userWebModel.getFirebaseDeviceToken());
                    userRepository.save(user);
                }

                String jwt = jwtUtils.generateJwtToken(authentication);
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                logger.info("Login Controller ---- Finished");
                return ResponseEntity.ok(new JwtResponse(jwt,
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        "Login Successful",
                        1,
                        refreshToken.getToken(),
                        userDetails.getUserType(),
                        user.getFilmHookCode(),
                        user.getAdminReview(),
                        user.getLastName(),userServices.getProfilePicUrl(user.getUserId())));
            }
        } catch (Exception e) {
            logger.error("Error at login() -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error while validating the user credentials. Please try again...", null));
        }
        return ResponseEntity.badRequest().body(new Response(-1, "Invalid EmailId", ""));
    }

    @PostMapping("logins")
    public ResponseEntity<?> logins(@RequestBody UserWebModel userWebModel) {
        Optional<User> checkUsername = userRepository.findByEmailAndAdminStatus(userWebModel.getEmail());
        if (checkUsername.isPresent()) {
            loginConstants.setUserType(userWebModel.getUserType());
            logger.info("In logins() User type from constants -> {}", loginConstants.getUserType());
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userWebModel.getEmail(), userWebModel.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            RefreshToken refreshToken = userService.createRefreshToken(userWebModel);
            String jwt = jwtUtils.generateJwtToken(authentication);
            User user = checkUsername.get();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            logger.info("Admin Login ---- Finished");
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    "Login Successfully",
                    1,
                    refreshToken.getToken(),
                    userDetails.getUserType(),
                    "",user.getAdminReview(), "",""));
        }
        return ResponseEntity.badRequest().body(new Response(-1, "Invalid EmailId", ""));
    }

    @PostMapping("refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody UserWebModel userWebModel) {
        Optional<RefreshToken> data = refreshTokenRepository.findByToken(userWebModel.getToken());
        if (data.isPresent()) {
            Response token = userService.verifyExpiration(data.get());
            Optional<User> userData = userRepository.findById(data.get().getUserId());
            String jwt = jwtUtils.generateJwtTokenForRefreshToken(userData.get());
            RefreshToken refreshToken = data.get();
            refreshToken.setExpiryToken(LocalTime.now().plusMinutes(17));
            refreshTokenRepository.save(refreshToken);
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userData.get().getUserId(),
                    userData.get().getName(),
                    userData.get().getEmail(),
                    "Success",
                    1,
                    token.getData().toString(),
                    userData.get().getUserType(),
                    "",userData.get().getAdminReview(), "",""));
        }
        return ResponseEntity.badRequest().body(new Response(-1, "Refresh Token Failed", ""));
    }

    @PostMapping("verify")
    public ResponseEntity<?> verify(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("Verify controller start");
            return userService.verifyUser(userWebModel);
        } catch (Exception e) {
            logger.error("verify Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("verifyEmailOtp")
    public ResponseEntity<?> verifyEmailOtp(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("verifyEmailOtp controller start");
            return userService.verifyEmailOtp(userWebModel);
        } catch (Exception e) {
            logger.error("verifyEmailOtp Method Exception {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("verifyForgotOtp")
    public ResponseEntity<?> verifyForgotOtp(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("verifyForgotOtp controller start");
            return userService.verifyForgotOtp(userWebModel);
        } catch (Exception e) {
            logger.error("verifyForgotOtp Method Exception {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody UserWebModel userWebModel, HttpServletRequest request) {
        try {
            return userService.forgotPassword(userWebModel, request);
        } catch (Exception e) {
            logger.info("forgotPassword Method Exception {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("changeUserPassword")
    public ResponseEntity<?> changingPassword(@RequestBody UserWebModel userWebModel) {
        try {
            return userService.changingPassword(userWebModel);
        } catch (Exception e) {
            logger.info("changingPassword Method Exception {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("resendOtp")
    public ResponseEntity<?> resendOtp(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("resendOtp controller start");
            return userService.resendOtp(userWebModel);
        } catch (Exception e) {
            logger.error("resendOtp Method Exception {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("changePassword")
    public ResponseEntity<?> changePassword(@RequestBody UserWebModel userWebModel) {
        try {
            return userService.changePassword(userWebModel);
        } catch (Exception e) {
            logger.info("changePassword Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("addSecondaryMobileNo")
    public ResponseEntity<?> addSecondaryMobileNo(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("addSecondaryMobileNo to register :- {}", userWebModel);
            if (userWebModel.isFlag()) {
                return userService.addSecondaryMobileNo(userWebModel);
            } else if (!userWebModel.isFlag()) {
                return userService.otpSendEmail(userWebModel);
            } else {
                return ResponseEntity.badRequest().body(new Response(-1, "Invalid flag value", ""));
            }
        } catch (Exception e) {
            logger.error("addSecondaryMobileNo Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("verifyOtps")
    public ResponseEntity<?> verifyOtps(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("verifyOtps controller start");
            if (userWebModel.isFlag()) {
                return userService.verifyMobileOtp(userWebModel);
            } else if (!userWebModel.isFlag()) {
                return userService.verifyEmail(userWebModel);
            } else {
                return ResponseEntity.badRequest().body(new Response(-1, "Invalid flag value", ""));
            }
        } catch (Exception e) {
            logger.error("verifyOtps Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("addSecondaryEmail")
    public ResponseEntity<?> addSecondaryEmail(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("addSecondaryEmail :- {}", userWebModel);
            return userService.addSecondaryEmail(userWebModel);
        } catch (Exception e) {
            logger.error("addSecondaryEmail Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("verifyOldEmailOtps")
    public ResponseEntity<?> verifyOldEmailOtps(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("verifyOldEmailOtps :- {}", userWebModel);
            return userService.verifyOldEmailOtps(userWebModel);
        } catch (Exception e) {
            logger.error("verifyOldEmailOtps Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("verifynewEmailOtps")
    public ResponseEntity<?> verifynewEmailOtps(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("verifynewEmailOtps :- {}", userWebModel);
            return userService.verifynewEmailOtps(userWebModel);
        } catch (Exception e) {
            logger.error("verifynewEmailOtps Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }
    
    @PostMapping("saveQueries")
    public ResponseEntity<?> saveQueries(@RequestBody HelpAndSupportWebModel helpAndSupportWebModel)
    {
    	try {
    		logger.info("saveQueries :- {}",helpAndSupportWebModel);
    		return userService.saveQueries(helpAndSupportWebModel);
    	}
    	catch(Exception e)
    	{
    		logger.error("saveQueries Method Exception -->{}",e.getMessage());
    		e.printStackTrace();
    	}
    	return ResponseEntity.ok(new Response(-1,"Fail",""));
    }

}
