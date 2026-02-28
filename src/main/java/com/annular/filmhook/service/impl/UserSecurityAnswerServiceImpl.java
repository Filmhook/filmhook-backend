package com.annular.filmhook.service.impl;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.converter.UserSecurityAnswerConverter;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserSecurityAnswer;
import com.annular.filmhook.model.UserSecurityQuestion;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.repository.UserSecurityAnswerRepository;
import com.annular.filmhook.repository.UserSecurityQuestionRepository;
import com.annular.filmhook.service.AwsS3Service;
import com.annular.filmhook.service.UserSecurityAnswerService;
import com.annular.filmhook.util.MailNotification;
import com.annular.filmhook.webmodel.UserSecurityAnswerDTO;
import com.annular.filmhook.webmodel.UserWebModel;

@Service
public class UserSecurityAnswerServiceImpl implements UserSecurityAnswerService{
	@Autowired
	private UserSecurityAnswerRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MailNotification mailNotification;

	@Autowired
	private UserSecurityQuestionRepository questionRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserDetails userDetails;

	@Override
	public List<UserSecurityAnswerDTO> saveSecurityQuestions(
			List<UserSecurityAnswerDTO> dtoList,
			Integer loggedInUserId) {

		if (dtoList == null || dtoList.size() != 5) {
			throw new RuntimeException("Exactly 5 questions must be selected");
		}

		Set<Integer> uniqueQuestions = dtoList.stream()
				.map(dto -> {
					if (dto.getQuestion() == null || dto.getQuestion().getId() == null) {
						throw new RuntimeException("Question ID cannot be null");
					}
					return dto.getQuestion().getId();
				})
				.collect(Collectors.toSet());

		if (uniqueQuestions.size() != 5) {
			throw new RuntimeException("Duplicate questions not allowed");
		}

		User user = userRepository.findById(loggedInUserId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		List<UserSecurityAnswer> existingAnswers =
				repository.findByUser_UserId(loggedInUserId);

		// 🔐 If already configured → require OTP verification
		if (!existingAnswers.isEmpty()) {

			UserSecurityAnswer first = existingAnswers.get(0);

			if (!Boolean.TRUE.equals(first.getSecurityOtpVerified())) {
				throw new RuntimeException(
						"Please verify OTP before editing security questions");
			}
		}

		List<UserSecurityAnswerDTO> responseList = new ArrayList<>();

		for (UserSecurityAnswerDTO dto : dtoList) {

			UserSecurityQuestion question = questionRepository
					.findById(dto.getQuestion().getId())
					.orElseThrow(() -> new RuntimeException("Invalid question"));

			if (dto.getAnswer() == null || dto.getAnswer().isBlank()) {
				throw new RuntimeException("Answer cannot be empty");
			}

			String normalized = dto.getAnswer().trim().toLowerCase();

			Optional<UserSecurityAnswer> existing =
					existingAnswers.stream()
					.filter(a -> a.getQuestion().getId()
							.equals(question.getId()))
					.findFirst();

			UserSecurityAnswer entity;

			if (existing.isPresent()) {
				entity = existing.get();
				entity.setAnswerHash(passwordEncoder.encode(normalized));
				entity.setUpdatedBy(loggedInUserId);
				entity.setUpdatedOn(LocalDateTime.now());
			} else {
				entity = new UserSecurityAnswer();
				entity.setUser(user);
				entity.setQuestion(question);
				entity.setAnswerHash(passwordEncoder.encode(normalized));
				entity.setCreatedBy(loggedInUserId);
				entity.setUpdatedBy(loggedInUserId);
				entity.setCreatedOn(LocalDateTime.now());
				entity.setUpdatedOn(LocalDateTime.now());
				entity.setStatus(true);
			}

			UserSecurityAnswer saved = repository.save(entity);

			responseList.add(
					UserSecurityAnswerConverter.convertToDTO(saved)
					);
		}

		// ✅ After successful edit → clear OTP fields
		for (UserSecurityAnswer ans : existingAnswers) {
			ans.setSecurityEmailOtp(null);
			ans.setSecurityEmailOtpCreatedOn(null);
			ans.setSecurityOtpVerified(false);
		}

		repository.saveAll(existingAnswers);

		return responseList;
	}

	@Override
	public Response getAllSecurityQuestions() {
		try {

			List<UserSecurityQuestion> questions =
					questionRepository.findByStatusTrue();

			if (questions.isEmpty()) {
				return new Response(-1, "No security questions found", null);
			}

			return new Response(1, "Security questions fetched successfully", questions);

		} catch (Exception e) {
			return new Response(-1, "Failed to fetch security questions", null);
		}
	}

	@Override
	public Response getUserSecurityQuestionsWithAnswers(Integer userId) {

		List<UserSecurityAnswer> list =
				repository.findByUser_UserIdAndStatusTrue(userId);

		if (list.isEmpty()) {
			return new Response(-1, "No security questions configured", null);
		}

		List<UserSecurityAnswerDTO> dtoList = list.stream()
				.map(a -> UserSecurityAnswerDTO.builder()
						.id(a.getId())
						.user(a.getUser().getUserId())
						.question(a.getQuestion())
						.answer(a.getAnswerHash())  
						.build()
						)
				.collect(Collectors.toList());

		return new Response(1, "Security questions fetched successfully", dtoList);
	}


	@Override
	public Response verifySecurityAnswers( Integer userId, List<UserSecurityAnswerDTO> requestList) {
		  User user = userRepository.findById(userId)
		            .orElseThrow(() -> new RuntimeException("User not found"));

		    // 🔐 Permanent lock check
		    if (Boolean.TRUE.equals(user.getSecurityLocked())) {
		        return new Response(-1,
		                "Your account is locked due to multiple failed attempts. Please contact support team.",
		                null);
		    }

		    LocalDate today = LocalDate.now();

		    // Initialize null values
		    if (user.getSecurityAttemptCount() == null)
		        user.setSecurityAttemptCount(0);

		    if (user.getSecurityFailedDays() == null)
		        user.setSecurityFailedDays(0);

		    // 🔄 Reset daily attempt if new day
		    if (user.getSecurityAttemptDate() == null ||
		            !today.equals(user.getSecurityAttemptDate())) {

		        user.setSecurityAttemptCount(0);
		        user.setSecurityAttemptDate(today);
		    }

		    int attempts = user.getSecurityAttemptCount();

		    // 🚫 Already 3 attempts today
		    if (attempts >= 3) {
		        return new Response(-1,
		                "Maximum attempts reached for today. Try again after 24 hours.",
		                null);
		    }

		List<UserSecurityAnswer> savedAnswers =repository.findByUser_UserIdAndStatusTrue(userId);

		if (savedAnswers.isEmpty()) {
			return new Response(-1, "Security questions not configured", null);
		}

		List<Map<String, Object>> resultList = new ArrayList<>();

		boolean allCorrect = true;

		for (UserSecurityAnswerDTO request : requestList) {

			Map<String, Object> result = new HashMap<>();

			Integer questionId = request.getQuestion().getId();

			UserSecurityAnswer stored = savedAnswers.stream()
					.filter(a -> a.getQuestion().getId()
							.equals(questionId))
					.findFirst()
					.orElse(null);

			result.put("questionId", questionId);

			if (stored == null) {
				result.put("status", "INVALID_QUESTION");
				result.put("verified", false);
				allCorrect = false;
			} else {

				boolean match = passwordEncoder.matches(
						request.getAnswer().trim().toLowerCase(),
						stored.getAnswerHash()
						);

				if (match) {
					result.put("status", "CORRECT");
					result.put("verified", true);
				} else {
					result.put("status", "INCORRECT");
					result.put("verified", false);
					allCorrect = false;
				}
			}

			resultList.add(result);
		}

		if (allCorrect) {
			  user.setSecurityAttemptCount(0);
		        user.setSecurityFailedDays(0);
		        user.setSecurityQuestionsVerified(true);
		        userRepository.save(user);

			return new Response(1,
					"All security answers verified successfully",
					resultList);
		} else {
			
			  attempts++;
			    user.setSecurityAttemptCount(attempts);

			    // If reached 3 attempts today
			    if (attempts >= 3) {

			        int failedDays = user.getSecurityFailedDays() + 1;
			        user.setSecurityFailedDays(failedDays);

			        // 🔒 If failed 3 different days → permanent lock
			        if (failedDays >= 3) {
			            user.setSecurityLocked(true);
			            userRepository.save(user);

			            return new Response(-1,
			                    "Account locked due to multiple failed attempts. Please contact support team.",
			                    resultList);
			        }

			        userRepository.save(user);

			        return new Response(-1,
			                "Maximum attempts reached for today. Try again after 24 hours.",
			                resultList);
			    }

			    userRepository.save(user);

			    return new Response(-1,
			            "Incorrect security answers. Attempts left: "
			                    + (3 - attempts),
			            resultList);
		}
	}

	@Override
	public Response sendSecurityEditOtp(Integer userId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		List<UserSecurityAnswer> answers =
				repository.findByUser_UserId(userId);

		if (answers.isEmpty()) {
			return new Response(-1, "Security questions not configured", null);
		}

		SecureRandom random = new SecureRandom();
		int otpNumber = 100000 + random.nextInt(900000); 
		String otp = String.valueOf(otpNumber);

		for (UserSecurityAnswer ans : answers) {
			ans.setSecurityEmailOtp(otp);
			ans.setSecurityEmailOtpCreatedOn(LocalDateTime.now());
		}

		repository.saveAll(answers);

		String subject = "FilmHook Security Question Verification Code";
		String content = "<p>For your protection, we require verification before modifying your security questions.</p>"
				+ "<p>Please enter the following OTP to continue:</p>"
				+ "<h1 style='color:#1F618D;'>" + otp + "</h1>"
				+ "<p>This verification code is valid for <b>2 minutes</b>.</p>"
				+ "<p>If this wasn’t you, please secure your account immediately.</p>";


		mailNotification.sendEmailSync(
				user.getName(),
				user.getEmail(),
				subject,
				content
				);

		return new Response(1, "OTP sent successfully", null);
	}

	@Override
	public Response verifySecurityEditOtp(Integer userId, String otpInput) {

		List<UserSecurityAnswer> answers =
				repository.findByUser_UserId(userId);

		if (answers.isEmpty()) {
			return new Response(-1, "Security questions not configured", null);
		}

		UserSecurityAnswer first = answers.get(0);

		if (first.getSecurityEmailOtp() == null) {
			return new Response(-1, "OTP not generated", null);
		}

		long minutes = Duration.between(
				first.getSecurityEmailOtpCreatedOn(),
				LocalDateTime.now()
				).toMinutes();


		if (minutes > 2) {
			return new Response(-1, "OTP expired", null);
		}

		if (!first.getSecurityEmailOtp().equals(otpInput)) {
			return new Response(-1, "Invalid OTP", null);
		}

		// ✅ Mark verified
		for (UserSecurityAnswer ans : answers) {
			ans.setSecurityOtpVerified(true);
		}

		repository.saveAll(answers);

		return new Response(1, "OTP verified successfully", null);
	}



	@Override
	public ResponseEntity<?> changingPassword(UserWebModel userWebModel) {

		try {

			Integer userId = userDetails.userInfo().getId();

			User user = userRepository.findById(userId)
					.orElseThrow(() -> new RuntimeException("User not found"));

			// 🔐 Check security verification
			if (!Boolean.TRUE.equals(user.getSecurityQuestionsVerified())) {
				return ResponseEntity.badRequest()
						.body(new Response(0,
								"Security questions not verified.",
								""));
			}

			BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

			String encryptPwd = bcrypt.encode(userWebModel.getNewPassword());

			user.setPassword(encryptPwd);

			// 🔄 Reset verification after password change
			user.setSecurityQuestionsVerified(false);

			userRepository.save(user);

			return ResponseEntity.ok()
					.body(new Response(1,
							"Password changed successfully",
							""));

		} catch (Exception e) {

			return ResponseEntity.internalServerError()
					.body("An error occurred while changing the password.");
		}
	}
}
