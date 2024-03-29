package com.annular.filmhook.controller;

import java.time.LocalTime;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.webmodel.UserWebModel;

@RestController
@RequestMapping("/user")
public class UserController {

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

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

    @PostMapping("register")
    public ResponseEntity<?> userRegister(@RequestBody UserWebModel userWebModel) {
        try {
            logger.info("User register controller start");
            System.out.println("Username -> " + userWebModel.getName() + ", User Password -> " + userWebModel.getPassword());
            return userService.register(userWebModel);
        } catch (Exception e) {
            logger.error("userRegister Method Exception {}" + e);
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody UserWebModel userWebModel) {
//		Optional<User> checkUser = userRepo.findByUserName(userWebModel.getUserName());
        System.out.println("" + userWebModel.getEmail());
        Optional<User> checkUsername = userRepository.findByEmail(userWebModel.getEmail(), userWebModel.getUserType());
        System.out.println("------->" + userWebModel.getEmail());
        System.out.println("HHHHHHH" + checkUsername);
        if (checkUsername.isPresent()) {
//			 logger.info("Checking Controller----> " + userWebModel.getUserEmailId());
//			jwtUtils.setAdmin(userWebModel.isUserIsAdmin());
//			jwtUtils.setDriver(userWebModel.isUserIsDriver());
//			loginConstants.setAdmin(userWebModel.isUserIsAdmin());
//			loginConstants.setDriver(userWebModel.isUserIsDriver());
            loginConstants.setUserType(userWebModel.getUserType());
            System.out.println("User type ---> " + loginConstants.getUserType());
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userWebModel.getEmail(), userWebModel.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            RefreshToken refreshToken = userService.createRefreshToken(userWebModel);
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            logger.info("Login Controller ---- Finished");
            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), "Login Successful", 1, ""));
        }
        return (ResponseEntity<?>) ResponseEntity.badRequest().body(new Response(-1, "Invalid EmailId", ""));
    }

    @GetMapping("verify")
    public Response verifyUser(@Param("code") String code) {
        try {
            if (userService.verify(code)) {
                logger.info("User credentials verified successfully");
            } else {
                logger.info("User verification failed");
                return new Response(-1, "Verify Failed", "");
            }
        } catch (Exception e) {
            logger.info("verifyUser method Exception " + e);
        }
        return new Response(1, "Verify Success", "");
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
            return ResponseEntity.ok(new JwtResponse(jwt, userData.get().getUserId(), userData.get().getName(), userData.get().getEmail(), "Success", 1, token.getData().toString()));
        }
        return (ResponseEntity<?>) ResponseEntity.badRequest().body(new Response(-1, "Refresh Token Failed", ""));
    }

//	@PostMapping("forgotPassword")
//	public ResponseEntity<?> forgotPassword(@RequestBody UserWebModel userWebModel, HttpServletRequest request) {
//		try {
//			logger.info("getUser controller start");
//			return userService.forgotPassword(userWebModel, request);
//		} catch (Exception e) {
//			logger.info("getUser Method Exception" + e);
//			e.printStackTrace();
//		}
//		return ResponseEntity.ok(new Response(-1, "Fail", ""));
//	}

}
