package com.annular.filmhook.service.impl;

import java.text.SimpleDateFormat;

import java.time.Duration;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

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

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.Utility;
import com.annular.filmhook.model.RefreshToken;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.RefreshTokenRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AuthenticationService;
import com.annular.filmhook.util.CalendarUtil;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.util.Notifications;
import com.annular.filmhook.util.TwilioConfig;
import com.annular.filmhook.webmodel.UserWebModel;

import net.bytebuddy.utility.RandomString;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	public static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	Notifications notifications;

	@Autowired
	RefreshTokenRepository refreshTokenRepository;

	@Autowired
	TwilioConfig twilioConfig;

	@Value("${annular.app.url}")
	private String url;

	@Autowired
	UserDetails userDetails;

	@Autowired
	FileUtil fileUtil;

	@Override
	public ResponseEntity<?> register(UserWebModel userWebModel, String request) {
		HashMap<String, Object> response = new HashMap<>();
		 List<Map<String, Object>> dataList = new ArrayList<>();
		try {
			logger.info("Register method start");
			System.out.println(userWebModel.getEmail());
//			Optional<User> userData = userRepository.findByEmailAndUserType(userWebModel.getEmail(),
//					userWebModel.getUserType());
			Optional<User> userData = userRepository.findByEmailAndUserTypeAndMobile(userWebModel.getEmail(),
					userWebModel.getUserType());


			BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
			if (!userData.isPresent()) {
				User user = new User();
				user.setPhoneNumber(userWebModel.getPhoneNumber());
				user.setName(userWebModel.getName());
				user.setEmail(userWebModel.getEmail());
				user.setUserType(userWebModel.getUserType());
				user.setMobileNumberStatus(false);
				user.setDob(CalendarUtil.convertDateFormat(CalendarUtil.UI_DATE_FORMAT,
						CalendarUtil.MYSQL_DATE_FORMAT, userWebModel.getDob()));
				user.setGender(userWebModel.getGender());
				user.setCountry(userWebModel.getCountry());
				user.setState(userWebModel.getState());
				user.setUserType(userWebModel.getUserType());
				user.setDistrict(userWebModel.getDistrict());

				// Generate and set FilmHook code
				String filmHookCode = generateFilmHookCode();
				user.setFilmHookCode(filmHookCode);

				String encryptPwd = bcrypt.encode(userWebModel.getPassword());
				user.setPassword(encryptPwd);
				Boolean sendVerificationRes = sendVerificationEmail(user, request);
				int min = 1000;
				String randomCode = RandomString.make(64);
				user.setVerificationCode(randomCode);

				user.setStatus(false);
				user.setCreatedBy(user.getUserId()); // You might want to check how you're setting createdBy
				user.setCreatedOn(new Date());
				if (!sendVerificationRes)
					return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
							.body(new Response(-1, "Mail not sent", "error"));
				int mins = 1000;
				int max = 9999;
				int otpNumber = (int) (Math.random() * (max - mins + 1) + mins);
				user.setOtp(otpNumber);

				CompletableFuture.runAsync(() -> {
					String message = "Your OTP is " + otpNumber + " for verification";
					twilioConfig.smsNotification(userWebModel.getPhoneNumber(), message);
				});
				user = userRepository.save(user);

				response.put("userDetails", user);
				response.put("verificationCode", user.getVerificationCode());
				 dataList.add(response);
			} else {
				return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
						.body(new Response(1, "This Account already exists", ""));
			}
			logger.info("Register method end");
		} catch (Exception e) {
			logger.error("Register Method Exception...", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Failed to create profile", e.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new Response(1, "Profile Created Successfully", response));
	}

	private static final AtomicInteger counter = new AtomicInteger(1);

	// Method to generate FilmHook code
	private String generateFilmHookCode() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
		String timestamp = dateFormat.format(new Date());
		// Generate unique ID with automatic incrementing counter
		String uniqueId = String.format("%02d", counter.getAndIncrement());
		return "Fh" + timestamp + uniqueId;
	}

	public boolean sendVerificationEmail(User user, String request) {
		Boolean response = true;
		try {
			String subject = "Verify Your EmailID";
			String senderName = "FilmHook";
			String mailContent = "<p>Hello " + user.getName() + ",</p>";
			mailContent += "<p>Please verify your emailId for register in  FilmHook, </p>";
			String verifyUrl = url + "/verifyemail?code=" + user.getVerificationCode();
			mailContent += "<h3><a href= \"" + request + "\">VERIFY EMAIL</a></h3>";
			mailContent += "<p>Thank You<br>FilmHook</p>";
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);
			helper.setFrom("yaswanthshankar2705@gmail.com", senderName);
			helper.setTo(user.getEmail());
			helper.setSubject(subject);
			String str = mailContent;
			if (request != null) {
				str = mailContent.replace(request, verifyUrl);
				mailContent = mailContent.replace(request, "/login");
			}
			helper.setText(str, true);
			javaMailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			response = false;
		}
		return response;

	}

	@Override
	public boolean verify(String code) {
		User user = userRepository.findByVerificationCode(code);
		if (user == null || user.getStatus()) {
			return false;
		} else {
			user.setVerificationCode(null);
			user.setStatus(true);
			userRepository.save(user);
			String subject = "FilmHook Registration Confirmation";
			String mailContent = "<p>Hello " + user.getName() + ",</p>";
			mailContent += "<p>You have successfully registered with the FilmHook </p>";
			mailContent += "<p>Thank You<br>FilmHook</p>";
			notifications.emailNotification(user.getEmail(), subject, mailContent);
			return true;
		}
	}

	@Override
	public Response verifyExpiration(RefreshToken token) {
		LocalTime currentTime = LocalTime.now();
		if (currentTime.isBefore(token.getExpiryToken())) {
			return new Response(1, "Success", token.getToken());
		} else {
//			refreshTokenRepository.delete(token);
//			throw new RuntimeException(token.getToken() + " RefreshToken was expired. Please make a new signIn request");
			return new Response(-1, "RefreshToken expired", "");
		}
	}

	@Override
	public RefreshToken createRefreshToken(UserWebModel userWebModel) {
		HashMap<String, Object> response = new HashMap<>();
		RefreshToken refreashToken = new RefreshToken();
		try {
			logger.info("createRefreshToken method start");
			Optional<User> data = userRepository.findByEmailAndUserType(userWebModel.getEmail(),
					userWebModel.getUserType());
			if (data.isPresent()) {
				Optional<RefreshToken> refreshTokenData = refreshTokenRepository.findByUserId(data.get().getUserId());
				if (refreshTokenData.isPresent()) {
					refreshTokenRepository.delete(refreshTokenData.get());
				}
				refreashToken.setUserId(data.get().getUserId());
				refreashToken.setToken(UUID.randomUUID().toString());
				refreashToken.setExpiryToken(LocalTime.now().plusMinutes(17));
				refreashToken = refreshTokenRepository.save(refreashToken);
				response.put("refreashToken", refreashToken);
				logger.info("createRefreshToken method end");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return refreashToken;
	}

	@Override
	public ResponseEntity<?> verifyUser(UserWebModel userWebModel) {
		try {
			logger.info("verifyUser method start");
//			Optional<User> userData = userRepository.findByOtp(userWebModel.getVerificationCode(),
//					userWebModel.getPhoneNumber());
			Optional<User> userData = userRepository.findByOTps(userWebModel.getOtp(),userWebModel.getUserId());
			if (userData.isPresent()) {
				User user = userData.get();
				user.setMobileNumberStatus(true);
				user.setOtp(null);
				userRepository.save(user);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(1, "OTP Invalid", ""));
			}
			logger.info("verifyUser method end");
		} catch (Exception e) {
			logger.error("verifyUser Method Exception...", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new Response(1, "Account Verified", ""));
	}

	@Override
	public ResponseEntity<?> forgotPassword(UserWebModel userWebModel, HttpServletRequest request) {
		try {
			logger.info("forgotPassword method start");
			String siteUrl = Utility.getSiteUrl(request);
			System.out.println("------>" + userWebModel.getEmail());
			Optional<User> data = userRepository.findByEmail(userWebModel.getEmail());
			System.out.println("response------>" + data.get().getEmail());
			if (data.isPresent()) {
				String token = UUID.randomUUID().toString();
				int expirationTimeMinutes = 2;
				Instant expirationTime = Instant.now().plus(Duration.ofMinutes(expirationTimeMinutes));
				User user = data.get();
				String subject = "Change Password";
				String senderName = "Film-Hook";
				String mailContent = "<p>Hello ,</p>";
				mailContent += "<p>Please click below link for change password, </p>";
				String verifyUrl = url + "/forgetpass?id=" + token;
//				String verifyUrl = "https://www.annulartechnologies.com";
				mailContent += "<h3><a href= \"" + siteUrl + "\">Change Password</a></h3>";
				mailContent += "<p>Thank You<br>Film-Hook</p>";
				MimeMessage message = javaMailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message);
				helper.setFrom("tamil030405@gmail.com", senderName);
				helper.setTo(user.getEmail());
				helper.setSubject(subject);
				String str = mailContent.replace(siteUrl, verifyUrl);
//				mailContent=mailContent.replace(request, "/login");
				helper.setText(str, true);
				user.setResetPassword(token);
				userRepository.save(user);
				javaMailSender.send(message);
				logger.info("forgotPassword method end");
			} else {
				return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
						.body(new Response(-1, "Please enter the Register Email", ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", ""));
		}
		return ResponseEntity.status(org.springframework.http.HttpStatus.OK).body(new Response(1,
				"Link for password change has been sent in Email.Please check your inbox.", "Email Sent Successfull"));
	}

	@Override
	public ResponseEntity<?> changingPassword(UserWebModel userWebModel) {
		try {
			logger.info("changePassword method start");
			BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
			Optional<User> userFromDB = userRepository.findByEmail(userWebModel.getEmail());
			if (userFromDB.isPresent()) {
				User user = userFromDB.get();
				System.out.println("current" + userWebModel.getCurrentPassword());
				if (userWebModel.getCurrentPassword() != null
						&& bcrypt.matches(userWebModel.getCurrentPassword(), user.getPassword())) {

					String encryptPwd = bcrypt.encode(userWebModel.getNewPassword());
					user.setPassword(encryptPwd);
					user = userRepository.save(user);
					return ResponseEntity.status(HttpStatus.OK)
							.body(new Response(1, "password changed successfully", ""));
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(new Response(0, "Incorrect current password.", ""));
				}
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(-1, "User not found.", ""));
			}
		} catch (Exception e) {
			logger.error("Error occurred while changing password: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while changing the password.");
		}
	}

	@Override
	public ResponseEntity<?> resendOtp(UserWebModel userWebModel) {
		try {
			logger.info("resendOtp method start");
			System.out.println("---------->" + userWebModel.getPhoneNumber());
			Optional<User> userData = userRepository.findByPhoneNumberAndUserType(userWebModel.getPhoneNumber(),
					userWebModel.getUserType());
			System.out.println("data---------->" + userWebModel.getPhoneNumber());
			if (userData.isPresent()) {
				User user = userData.get();
				int min = 1000;
				int max = 9999;
				int otpNumber = (int) (Math.random() * (max - min + 1) + min);
				user.setOtp(otpNumber);
				user = userRepository.save(user);
				/*
				 * CompletableFuture.runAsync(() -> { String message = "Your OTP is " +
				 * otpNumber + " for verification";
				 * twilioConfig.smsNotification(userWebModel.getName(), message); });
				 */
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(-1, "Fail", "User not found, Register your account"));
			}
			logger.info("resendOtp method end");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("resendOtp Method Exception {} " + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new Response(1, "Success", "OTP sent successfully"));
	}

	@Override
	public ResponseEntity<?> changePassword(UserWebModel userWebModel) {
		try {
			logger.info("changePassword method start");
			BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
			Optional<User> data = userRepository.findByResetPassword(userDetails.userInfo().getId());
			if (data.isPresent()) {
				User user = data.get();
				String encryptPwd = bcrypt.encode(userWebModel.getPassword());
				user.setPassword(encryptPwd);
				user.setResetPassword(null);
				user = userRepository.save(user);
			} else {
				return (ResponseEntity<?>) ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
						.body(new Response(-1, "Link Expired", ""));
			}
			logger.info("changePassword method end");
		} catch (Exception e) {
			e.printStackTrace();
			return (ResponseEntity<?>) ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", ""));
		}
		return (ResponseEntity<?>) ResponseEntity.status(org.springframework.http.HttpStatus.OK).body(new Response(1,
				"Your password has been changed. Please login with new password.", "Password changed SuccessFully"));
//		return new Response(1,"Your password has been changed. Please login with new password.","Password changed SuccessFully");
	}

}
