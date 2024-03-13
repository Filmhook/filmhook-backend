package com.annular.filmHook.service.impl;

import java.time.Duration;

import java.time.Instant;
import java.time.LocalTime;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.annular.filmHook.Response;
import com.annular.filmHook.Utility;
import com.annular.filmHook.model.RefreshToken;
import com.annular.filmHook.model.User;
import com.annular.filmHook.repository.RefreshTokenRepository;
import com.annular.filmHook.repository.UserRepository;
import com.annular.filmHook.service.UserService;
import com.annular.filmHook.webModel.UserWebModel;


@Service
public class UserServiceImpl implements UserService {

	public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	RefreshTokenRepository refreshTokenRepository;

	@Value("${annular.app.url}")
	private String url;
	
	@Override
	public ResponseEntity<?> register(UserWebModel userWebModel) {
		HashMap<String, Object> response = new HashMap<String, Object>();
		try {
			logger.info("Register method start");
			Optional<User> userData = userRepository.findByEmail(userWebModel.getEmail(),
					userWebModel.getUserType());
//			Optional<User> userData = userRepository.findByUserName(userWebModel.getName(),
//					userWebModel.getUserType());
			BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
			if (!userData.isPresent()) {
				User user = new User();
				if (userWebModel.getUserType().equalsIgnoreCase("commonUser")
						|| userWebModel.getUserType().equalsIgnoreCase("industrialUser"))
						 {
					user.setPhoneNumber(userWebModel.getPhoneNumber());
                    user.setName(userWebModel.getName());
                    user.setEmail(userWebModel.getEmail());
					user.setUserType(userWebModel.getUserType());
					user.setDob(userWebModel.getDob());
					user.setGender(userWebModel.getGender());
					user.setCountry(userWebModel.getCountry());
					user.setState(userWebModel.getState());
					user.setDistrict(userWebModel.getDistrict());
					
					System.out.println("password-------->"+userWebModel.getPassword());
					String encryptPwd = bcrypt.encode(userWebModel.getPassword());
					user.setPassword(encryptPwd);

					if (userWebModel.getUserType().equalsIgnoreCase("commonUser")) {
//						user.setUserFirstName(userWebModel.getUserFirstName());
//						user.setUserLastName(userWebModel.getUserLastName());
//						user.setUserAccountName(userWebModel.getUserFirstName() + " " + userWebModel.getUserLastName());
					} 
					else if (userWebModel.getUserType().equalsIgnoreCase("industrialUser")) {
//						user.setUserFirstName(userWebModel.getUserFirstName());
//						user.setUserLastName(userWebModel.getUserLastName());
//						user.setUserAccountName(userWebModel.getUserFirstName() + " " + userWebModel.getUserLastName());
					} else {
						return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
								.body(new Response(1, "Invalid user type", ""));
					}
					
					int min = 1000;
					int max = 9999;
					int otpNumber = (int) (Math.random() * (max - min + 1) + min);
					user.setVerificationCode(otpNumber);
					user = userRepository.save(user);
//					CompletableFuture.runAsync(() -> {
//						String message = "Your OTP is " + otpNumber + " for verification";
//						twilioConfig.smsNotification(userWebModel.getPhoneNumber(), message);
//					});
					response.put("user", user);
				} 
			} else {
				return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
						.body(new Response(1, "This Account is already exist", ""));
			}
			logger.info("Register method end");
		} catch (Exception e) {
			logger.error("Register Method Exception {} " + e);
			e.printStackTrace();
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body(new Response(1, "Profile Created Successful", response));
	}

	@Override
	public boolean verify(String code) {
		User user = userRepository.findByVerificationCode(code);
		if (user == null || user.isUserIsActive()) {
			return false;
		} else {
			user.setVerificationCode(null);
			user.setUserIsActive(true);
			userRepository.save(user);
		
			return true;
		}
	}

	@Override
	public Response verifyExpiration(RefreshToken token) {
		LocalTime currentTime = LocalTime.now();
		if(currentTime.isBefore(token.getExpiryToken())) {
			return new Response(1,"Success",token.getToken());
		}else {
//			refreshTokenRepository.delete(token);
//			throw new RuntimeException(token.getToken() + " RefreshToken was expired. Please make a new signIn request");
			return new Response(-1,"RefreshToken expired","");
		}
	}
	
	@Override
	public RefreshToken createRefreshToken(UserWebModel userWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		RefreshToken refreashToken = new RefreshToken();
		try {
			logger.info("createRefreshToken method start");
			Optional<User> data = userRepository.findByUserName(userWebModel.getEmail(), userWebModel.getUserType());
			if(data.isPresent()) {
				Optional<RefreshToken> refreshTokenData = refreshTokenRepository.findByUserId(data.get().getUserId());
				if(refreshTokenData.isPresent()) {
					refreshTokenRepository.delete(refreshTokenData.get());
				}
				refreashToken.setUserId(data.get().getUserId());
				refreashToken.setToken(UUID.randomUUID().toString());
				refreashToken.setExpiryToken(LocalTime.now().plusMinutes(17));
				refreashToken = refreshTokenRepository.save(refreashToken);
				response.put("refreashToken", refreashToken);
				logger.info("createRefreshToken method end");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return refreashToken;
	}

//	@Override
//	public ResponseEntity<?> forgotPassword(UserWebModel userWebModel,HttpServletRequest request) {
//		try {
//			logger.info("forgotPassword method start");
//			String siteUrl = Utility.getSiteUrl(request);
//			Optional<User> data = userRepository.findByAllUserEmailId(userWebModel.getEmail(), false,false);
//			if(data.isPresent()) {
//				String token = UUID.randomUUID().toString();
//				int expirationTimeMinutes = 2;
//				Instant expirationTime = Instant.now().plus(Duration.ofMinutes(expirationTimeMinutes));
//				User user = data.get();
//				String subject = "Change Password";
//				String senderName = "Film-Hook";
//				String mailContent = "<p>Hello ,</p>";
//				mailContent += "<p>Please click below link for change password, </p>";
//				String verifyUrl = url+"/forgetpass?id=" + token;
////				String verifyUrl = "https://www.annulartechnologies.com";
//				mailContent += "<h3><a href= \"" + siteUrl + "\">Change Password</a></h3>";
//				mailContent += "<p>Thank You<br>Film-Hook</p>";
//				MimeMessage message = javaMailSender.createMimeMessage();
//				MimeMessageHelper helper = new MimeMessageHelper(message);
//				helper.setFrom("tech.annular@gmail.com", senderName);
//				helper.setTo(user.getEmail());
//				helper.setSubject(subject);
//				String str = mailContent.replace(siteUrl, verifyUrl);
////				mailContent=mailContent.replace(request, "/login");
//				helper.setText(str, true);
//				user.setResetPassword(token);
//				userRepository.save(user);
//				javaMailSender.send(message);
//				logger.info("forgotPassword method end");
//			}else {
//				return (ResponseEntity<?>) ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).body(new Response(-1,"Please enter the Register Email",""));
//			}
//		}catch(Exception e) {
//			e.printStackTrace();
//			return (ResponseEntity<?>) ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(-1,"Fail",""));
//		}
//		return (ResponseEntity<?>) ResponseEntity.status(org.springframework.http.HttpStatus.OK).body(new Response(1,"Link for password change has been sent in Email.Please check your inbox.","Email Sent Successfull"));
//	}


}
