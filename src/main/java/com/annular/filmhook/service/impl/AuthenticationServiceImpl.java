package com.annular.filmhook.service.impl;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.model.HelpAndSupport;
import com.annular.filmhook.model.RefreshToken;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.HelpAndSupportRepository;
import com.annular.filmhook.repository.RefreshTokenRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AuthenticationService;
import com.annular.filmhook.util.MailNotification;
import com.annular.filmhook.configuration.TwilioConfig;
import com.annular.filmhook.webmodel.HelpAndSupportWebModel;
import com.annular.filmhook.webmodel.UserWebModel;

import net.bytebuddy.utility.RandomString;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    public static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MailNotification mailNotification;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    TwilioConfig twilioConfig;
    
    @Autowired
    UserDetails userDetails;
    
    @Autowired
    HelpAndSupportRepository helpAndSupportRepository;

    @Value("${annular.app.url}")
    private String url;

    @Override
    public ResponseEntity<?> register(UserWebModel userWebModel, String request) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            logger.info("Register method start");
            Optional<User> userData = userRepository.findByEmailAndUserType(userWebModel.getEmail(), userWebModel.getUserType());
            if (userData.isEmpty()) {
                User user = new User();
                user.setPhoneNumber(userWebModel.getPhoneNumber());

                StringBuilder name = new StringBuilder();
                if (!Utility.isNullOrBlankWithTrim(userWebModel.getFirstName())) {
                    user.setFirstName(userWebModel.getFirstName());
                    name.append(userWebModel.getFirstName()).append(" ");
                }
                if (!Utility.isNullOrBlankWithTrim(userWebModel.getMiddleName())) {
                    user.setMiddleName(userWebModel.getMiddleName());
                    name.append(userWebModel.getMiddleName()).append(" ");
                }
                if (!Utility.isNullOrBlankWithTrim(userWebModel.getLastName())) {
                    user.setLastName(userWebModel.getLastName());
                    name.append(userWebModel.getLastName());
                }
                user.setName(name.toString());

                user.setEmail(userWebModel.getEmail());
                user.setUserType(userWebModel.getUserType());
                user.setMobileNumberStatus(false);
                user.setIndustryUserVerified(false);
                user.setDob(userWebModel.getDob());
                user.setAdminReview((float) 0.1);
                user.setGender(userWebModel.getGender());
                user.setBirthPlace(userWebModel.getBirthPlace());
                user.setLivingPlace(userWebModel.getLivingPlace());
                user.setDistrict(userWebModel.getDistrict());
                user.setCountryCode(userWebModel.getCountryCode());

                // Generate and set FilmHook code
                String filmHookCode = this.generateFilmHookCode();
                user.setFilmHookCode(filmHookCode);

                String encryptPwd = new BCryptPasswordEncoder().encode(userWebModel.getPassword());
                user.setPassword(encryptPwd);

                String randomCode = RandomString.make(64);
                user.setVerificationCode(randomCode);

                int otp = Integer.parseInt(Utility.generateOtp(4));
                user.setOtp(otp);
                CompletableFuture.runAsync(() -> {
                    String message = "Your OTP is " + otp + " for verification";
                    twilioConfig.smsNotification(userWebModel.getPhoneNumber(), message);
                });

                int emailOtp = Integer.parseInt(Utility.generateOtp(4));
                user.setEmailOtp(emailOtp);
                // boolean sendVerificationRes = this.sendVerificationEmail(user);
                // if (!sendVerificationRes) return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new Response(-1, "Mail not sent", "error"));

                user.setStatus(false);
                user.setCreatedBy(user.getUserId());
                user.setCreatedOn(new Date());

                user = userRepository.save(user);
                response.put("userDetails", user);
                //response.put("verificationCode", user.getVerificationCode());
            } else {
                return ResponseEntity.unprocessableEntity().body(new Response(1, "This Account already exists", ""));
            }
            logger.info("Register method end");
        } catch (Exception e) {
            logger.error("Register Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to register the user. Try Again...", e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(1, "User was registered in FilmHook app successfully...", response));
    }

    private static final AtomicInteger counter = new AtomicInteger(1);

    /** Method to generate unique FilmHook code
     * @return String
     */
    private String generateFilmHookCode() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());
        // Generate unique ID with automatic incrementing counter
        String uniqueId = String.format("%02d", counter.getAndIncrement());
        return "Fh" + timestamp + uniqueId;
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
            String mailContent = "<p>You have successfully registered with the FilmHook </p>";
            mailNotification.sendEmailAsync(user.getName(), user.getEmail(), subject, mailContent);
            return true;
        }
    }

    @Override
    public Response verifyExpiration(RefreshToken token) {
//        LocalTime currentTime = LocalTime.now();
//        if (currentTime.isBefore(token.getExpiryToken())) {
//            return new Response(1, "Success", token.getToken());
//        } else {
//            refreshTokenRepository.delete(token);
//            throw new RuntimeException(token.getToken() + " RefreshToken was expired. Please make a new signIn request");
//        }
        return new Response(-1, "RefreshToken expired", "");
    }

    @Override
    public RefreshToken createRefreshToken(UserWebModel userWebModel) {
        try {
            logger.info("createRefreshToken method start");
            Optional<User> data = userRepository.findByEmail(userWebModel.getEmail());
//            if (data.isPresent()) {
//                Optional<RefreshToken> refreshTokenData = refreshTokenRepository.findByUserId(data.get().getUserId());
//                refreshTokenData.ifPresent(token -> refreshTokenRepository.delete(token));

                RefreshToken refreshToken = RefreshToken.builder().build();
                refreshToken.setUserId(data.map(User::getUserId).orElse(null));
                refreshToken.setToken(UUID.randomUUID().toString());
//                refreshToken.setExpiryToken(LocalTime.now().plusMinutes(45));
                refreshToken = refreshTokenRepository.save(refreshToken);

                logger.info("createRefreshToken method end");
                return refreshToken;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<?> verifyUser(UserWebModel userWebModel) {
        try {
            logger.info("verifyUser method start");
            List<User> userData = userRepository.findByOtp(userWebModel.getOtp());
            if (!userData.isEmpty()) {
                User user = userData.get(0); // Assuming only one user should be returned
                user.setMobileNumberStatus(true);
                // user.setOtp(null);
                userRepository.save(user);
            } else {
                return ResponseEntity.badRequest().body(new Response(1, "OTP Invalid", ""));
            }
            logger.info("verifyUser method end");
        } catch (Exception e) {
            logger.error("verifyUser Method Exception...", e);
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(1, "Account Verified", ""));
    }

    @Override
    public ResponseEntity<?> forgotPassword(UserWebModel userWebModel, HttpServletRequest request) {
        try {
            logger.info("forgotPassword method start");
            Optional<User> data = userRepository.findByEmail(userWebModel.getEmail());
            if (data.isPresent()) {
                // Generate OTP
                String forgotOtp = Utility.generateOtp(6); // You need to implement this method to generate OTP

                User user = data.get();
                user.setForgotOtp(forgotOtp); // Save OTP in user's forgotOtp column

                // Save the updated user object
                userRepository.save(user);

                String subject = "Reset Password";
                String mailContent = "<p>Your OTP to reset your password is: <b>" + forgotOtp + "</b></p>";
                mailContent += "<p>Please use this OTP to reset your password. If you didn't request this, you can safely ignore this email.</p>";
                mailNotification.sendEmailAsync(user.getName(), user.getEmail(), subject, mailContent);

                logger.info("forgotPassword method end");
            } else {
                return ResponseEntity.badRequest().body(new Response(-1, "Please provide the registered Email", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", ""));
        }
        return ResponseEntity.ok().body(new Response(1, "OTP for password change has been sent in Email. Please check your inbox.", "Email Sent Successfully"));
    }

    @Override
    public ResponseEntity<?> changingPassword(UserWebModel userWebModel) {
        try {
            logger.info("changingPassword method start");
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            Optional<User> userFromDB = userRepository.findByEmail(userWebModel.getEmail());
            if (userFromDB.isPresent()) {
                User user = userFromDB.get();
                if (!Utility.isNullOrBlankWithTrim(userWebModel.getCurrentPassword()) && bcrypt.matches(userWebModel.getCurrentPassword(), user.getPassword())) {
                    String encryptPwd = bcrypt.encode(userWebModel.getNewPassword());
                    user.setPassword(encryptPwd);
                    user = userRepository.save(user);
                    return ResponseEntity.ok().body(new Response(1, "password changed successfully", ""));
                } else {
                    return ResponseEntity.badRequest().body(new Response(0, "Incorrect current password.", ""));
                }
            } else {
                return ResponseEntity.ok().body(new Response(-1, "User not found.", ""));
            }
        } catch (Exception e) {
            logger.error("Error occurred while changing password :- {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("An error occurred while changing the password.");
        }
    }

    @Override
    public ResponseEntity<?> resendOtp(UserWebModel userWebModel) {
        try {
            logger.info("resendOtp method start");
            Optional<User> userData = userRepository.findByPhoneNumberAndUserType(userWebModel.getPhoneNumber(), userWebModel.getUserType());
            if (userData.isPresent()) {
                User user = userData.get();
                int min = 1000;
                int max = 9999;
                int otpNumber = (int) (Math.random() * (max - min + 1) + min);
                user.setOtp(otpNumber);
                user = userRepository.save(user);
//                CompletableFuture.runAsync(() -> {
//                    String message = "Your OTP is " + otpNumber + " for verification";
//                    twilioConfig.smsNotification(userWebModel.getName(), message);
//                });
            } else {
                return ResponseEntity.ok().body(new Response(-1, "Fail", "User not found, Register your account"));
            }
            logger.info("resendOtp method end");
        } catch (Exception e) {
            logger.error("resendOtp Method Exception {} ", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(1, "Success", "OTP sent successfully"));
    }

    @Override
    public ResponseEntity<?> changePassword(UserWebModel userWebModel) {
        try {
            logger.info("changePassword method start");
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            Optional<User> data = userRepository.findByResetPasswords(userWebModel.getForgotOtp());
            if (data.isPresent()) {
                User user = data.get();
                String encryptPwd = bcrypt.encode(userWebModel.getPassword());
                user.setPassword(encryptPwd);
                user.setResetPassword(null);
                user = userRepository.save(user);
            } else {
                return ResponseEntity.badRequest().body(new Response(-1, "Link Expired", ""));
            }
            logger.info("changePassword method end");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", ""));
        }
        return ResponseEntity.ok().body(new Response(1, "Your password has been changed. Please login with new password.", "Password changed SuccessFully"));
    }

//    @Override
//    public ResponseEntity<?> verifyEmailOtp(UserWebModel userWebModel) {
//        try {
//            List<User> userList = userRepository.findAll();
//            boolean emailOtpVerified = false; // Flag to track if email OTP is verified
//
//            for (User user : userList) {
//                if (user.getEmailOtp() != null && user.getEmailOtp().equals(userWebModel.getEmailOtp())) {
//                    // Email OTP matches, set the status of this user to true
//                    user.setStatus(true);
//                    userRepository.save(user);
//                    emailOtpVerified = true; // Set flag to true since email OTP is verified
//                    break; // Exit loop once OTP is verified
//                }
//            }
//
//            if (emailOtpVerified) {
//                // Return a success response if email OTP is verified
//                return ResponseEntity.ok(new Response(1, "Email OTP verified successfully", ""));
//            } else {
//                // Return an error response if email OTP is not verified
//                return ResponseEntity.badRequest().body(new Response(-1, "Invalid Email OTP", ""));
//            }
//
//        } catch (Exception e) {
//            // Handle any unexpected exceptions and return an error response
//            logger.error("Error verifying email OTP: {}", e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to verify email OTP", ""));
//        }
//    }
    @Override
    public ResponseEntity<?> verifyEmailOtp(UserWebModel userWebModel) {
        try {
            List<User> userList = userRepository.findAll();
            boolean emailOtpVerified = false; // Flag to track if email OTP is verified
            User verifiedUser = null; // Hold the user object once OTP is verified

            for (User user : userList) {
                if (user.getEmailOtp() != null && user.getEmailOtp().equals(userWebModel.getEmailOtp())) {
                    // Email OTP matches, set the status of this user to true
                    user.setStatus(true);
                    userRepository.save(user);
                    emailOtpVerified = true; // Set flag to true since email OTP is verified
                    verifiedUser = user; // Store the verified user
                    break; // Exit loop once OTP is verified
                }
            }

            if (emailOtpVerified) {
                // Check the user's flag (assuming this is a field in the User entity)
                if (verifiedUser != null ) {
                    // If the userFlag is true, send the success email
                    if (verifiedUser.getUserFlag()) {
                    	String mailContent ="<p>Thank you for joining the Film-hook community! We're thrilled to have you on board as a Public User.</p>" +
                                "<p>Explore the world of cinema and entertainment like never before. Whether you're here to follow your favorite stars or discover fresh talent, you're in the right place. Browse through the latest films, engage with creative content, and join a community that celebrates storytelling in all its forms.</p>";                        mailNotification.sendEmail(verifiedUser.getName(), verifiedUser.getEmail(), "Welcome to Film-hook  Media Apps", mailContent);
                                mailNotification.sendEmail(verifiedUser.getName(), verifiedUser.getEmail(), "Welcome to Film-hook Media Apps", mailContent);
                    }
                }
                // Return a success response if email OTP is verified
                return ResponseEntity.ok(new Response(1, "Email OTP verified successfully. Public user account created in FilmHook.", ""));
            } else {
                // Return an error response if email OTP is not verified
                return ResponseEntity.badRequest().body(new Response(-1, "Invalid Email OTP. Unable to create a public user account.", ""));
            }

        } catch (Exception e) {
            // Handle any unexpected exceptions and return an error response
            logger.error("Error verifying email OTP: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to verify email OTP", ""));
        }
    }



    @Override
    public ResponseEntity<?> verifyForgotOtp(UserWebModel userWebModel) {
        try {
            // Retrieve all users from repository
            List<User> userList = userRepository.findAll();

            boolean found = false; // Flag to track if a user with the provided forgotOtp is found

            // Iterate through the list of users
            for (User user : userList) {
                if (!Utility.isNullOrBlankWithTrim(user.getForgotOtp()) && user.getForgotOtp().equals(userWebModel.getForgotOtp())) {
                    // Update the user if forgotOtp is verified
                    // user.setForgotOtp(null); // Clear the forgotOtp after verification
                    userRepository.save(user);
                    found = true; // Set the flag to true indicating user found
                    break; // Exit the loop as soon as the user is found
                }
            }

            if (found) {
                // Return a success response if a user with the provided forgotOtp is found
                return ResponseEntity.ok(new Response(1, "Forgot OTP verified successfully", ""));
            } else {
                // Return an error response if no user with the provided forgotOtp is found
                return ResponseEntity.badRequest().body(new Response(-1, "Invalid Forgot OTP", ""));
            }
        } catch (Exception e) {
            // Handle any unexpected exceptions and return an error response
            logger.error("Error verifying forgot OTP: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Failed to verify forgot OTP", ""));
        }
    }

    @Override
    public ResponseEntity<?> emailNotification(UserWebModel userWebModel, String request) {
        Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            boolean sendVerificationRes = mailNotification.sendVerificationEmail(user);
            if (sendVerificationRes)
                return ResponseEntity.ok().body(new Response(1, "Mail sent successfully", "success"));
            else
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new Response(-1, "Mail not sent", "error"));
        } else {
            return ResponseEntity.ok().body(new Response(-1, "User not found", "error"));
        }
    }

    @Override
    public ResponseEntity<?> addSecondaryMobileNo(UserWebModel userWebModel) {
        try {
            User user = userRepository.findById(userWebModel.getUserId()).orElse(null);
            if (user == null) return ResponseEntity.ok().body("User not found");

            String newPhoneNumber = userWebModel.getPhoneNumber();

            // Check if the new phone number is already associated with another user
            Optional<User> existingUserWithPhoneNumber = userRepository.findByPhoneNumber(newPhoneNumber);
            if (existingUserWithPhoneNumber.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number is already in use");
            }
            int otp = Integer.parseInt(Utility.generateOtp(4));
            user.setOtp(otp);
            CompletableFuture.runAsync(() -> {
                String message = "Your OTP is " + otp + " for verification";
                twilioConfig.smsNotification(user.getPhoneNumber(), message);
            });
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP sent to new phone number.");
            response.put("newMobile", newPhoneNumber);
            response.put("otpSentMessage", "OTP has been sent to " + newPhoneNumber);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error adding secondary mobile number: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> otpSendEmail(UserWebModel userWebModel) {
        try {
            User user = userRepository.findById(userWebModel.getUserId()).orElse(null);
            if (user == null) return ResponseEntity.ok().body("User not found");

            String newPhoneNumber = userWebModel.getPhoneNumber();

            // Check if the new phone number is already associated with another user
            Optional<User> existingUserWithPhoneNumber = userRepository.findByPhoneNumber(newPhoneNumber);
            if (existingUserWithPhoneNumber.isPresent())  return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number is already in use");

            // Generate OTP
            int otp = Integer.parseInt(Utility.generateOtp(4));
            user.setEmailOtp(otp);
            userRepository.save(user);

            // Send verification email
            boolean sendVerificationRes = mailNotification.sendVerificationEmail(user);
            if (!sendVerificationRes)  return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new Response(-1, "Mail not sent", "error"));

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP sent to new phone number.");
            response.put("newMobile", newPhoneNumber);
            response.put("otpSentMessage", "OTP has been sent to " + newPhoneNumber);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error adding secondary mobile number -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error adding secondary mobile number: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> verifyMobileOtp(UserWebModel userWebModel) {
        try {
            User user = userRepository.findById(userWebModel.getUserId()).orElse(null);
            if (user == null) return ResponseEntity.ok().body("User not found");

            String newPhoneNumber = userWebModel.getPhoneNumber();
            int providedOtp = userWebModel.getOtp();

            // Verify if the provided OTP matches the stored OTP
            if (user.getOtp() != providedOtp) {
                return ResponseEntity.badRequest().body("Invalid OTP");
            }

            // Update the user's phone number
            user.setPhoneNumber(newPhoneNumber);
            userRepository.save(user);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Phone number updated successfully.");
            response.put("newPhoneNumber", newPhoneNumber);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error verifying OTP and updating phone number -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error verifying OTP and updating phone number: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> verifyEmail(UserWebModel userWebModel) {
        try {
            User user = userRepository.findById(userWebModel.getUserId()).orElse(null);
            if (user == null) return ResponseEntity.ok().body("User not found");

            String newPhoneNumber = userWebModel.getPhoneNumber();
            int providedOtp = userWebModel.getEmailOtp();

            // Verify if the provided OTP matches the stored OTP
            if (user.getEmailOtp() != providedOtp) {
                return ResponseEntity.badRequest().body("Invalid OTP");
            }

            // Update the user's phone number
            user.setPhoneNumber(newPhoneNumber);
            userRepository.save(user);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Phone number updated successfully.");
            response.put("newPhoneNumber", newPhoneNumber);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error verifying email otp and updating phone number -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error verifying OTP and updating phone number: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> addSecondaryEmail(UserWebModel userWebModel) {
        try {
            Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
            if (userOptional.isEmpty()) return ResponseEntity.ok().body("User not found");

            User user = userOptional.get();
            String newEmail = userWebModel.getSecondaryEmail();
            user.setSecondaryEmail(newEmail);
            userRepository.save(user);

            // Generate OTP
            int primaryMailOtp = Integer.parseInt(Utility.generateOtp(4));
            user.setEmailOtp(primaryMailOtp);
            userRepository.save(user);

            // Send verification email
            boolean sendVerificationRes = mailNotification.sendVerificationEmail(user);
            if (!sendVerificationRes) return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new Response(-1, "Mail not sent", "error"));

            // Generate OTP
            int secondaryMailOtp = Integer.parseInt(Utility.generateOtp(4));
            user.setSecondaryemailOtp(secondaryMailOtp);
            userRepository.save(user);


            //send verification secondaryEmail
            boolean sendVerificationRess = this.sendVerificationSecondaryEmail(user);
            if (!sendVerificationRess) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new Response(-1, "Mail not sent", "error"));
            }
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP sent to primary and secondary email addresses.");
            response.put("newEmail", newEmail);
            response.put("oldEmailMessage", "OTP has been sent to " + user.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error verifying email: " + e.getMessage());
        }
    }

    public boolean sendVerificationSecondaryEmail(User user) {
        try {
            if (Utility.isNullOrZero(user.getSecondaryemailOtp())) throw new IllegalArgumentException("OTP is null");
            String subject = "Email Id Verification";
            String mailContent = "<p>Please use the following OTP to verify your email on FilmHook:<b>" + user.getSecondaryemailOtp() + "</b></p>";
            return mailNotification.sendEmailSync(user.getName(), user.getEmail(), subject, mailContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ResponseEntity<?> verifyOldEmailOtps(UserWebModel userWebModel) {
        try {
            Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
            if (!userOptional.isPresent()) {
                return ResponseEntity.ok().body("User not found");
            }

            User user = userOptional.get();
            int providedOtp = userWebModel.getEmailOtp();

            // Verify the OTP
            if (user.getEmailOtp() == providedOtp) {
                // OTP matches
                return ResponseEntity.ok(new Response(1, "Email verified successfully", "success"));
            } else {
                // OTP does not match, clear secondary email and OTP fields
                user.setSecondaryEmail(null);
                user.setSecondaryemailOtp(0);
                userRepository.save(user);

                return ResponseEntity.badRequest().body(new Response(-1, "Invalid OTP. Secondary email reset", "error"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error verifying email OTP: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> verifynewEmailOtps(UserWebModel userWebModel) {
        try {
            Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
            if (!userOptional.isPresent()) {
                return ResponseEntity.ok().body("User not found");
            }

            User user = userOptional.get();
            int providedOtp = userWebModel.getSecondaryemailOtp();

            // Verify the OTP
            if (user.getSecondaryemailOtp() == providedOtp) {
                // OTP matches, mark the secondary email as verified
                user.setVerified(true);
                userRepository.save(user);

                return ResponseEntity.ok(new Response(1, "Secondary email verified successfully", "success"));
            } else {
                // OTP does not match
                return ResponseEntity.badRequest().body(new Response(-1, "Invalid OTP", "error"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error verifying secondary email OTP: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getSeconadryEmailId(UserWebModel userWebModel) {
        try {
            Optional<User> userOptional = userRepository.findById(userWebModel.getUserId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Check if the secondary email is verified
                if (user.getVerified() == true) {
                    // Return the secondary email if it is verified
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 1);
                    response.put("secondaryEmail", user.getSecondaryEmail());
                    response.put("message", "Secondary email retrieved successfully");
                    return ResponseEntity.ok(response);
                } else {
                    // Return a message indicating the secondary email is not verified
                    return ResponseEntity.badRequest().body("Secondary email is not verified");
                }
            } else {
                // Return a message indicating the user is not found
                return ResponseEntity.ok().body("User not found");
            }
        } catch (Exception e) {
            // Handle any exceptions and return an internal server error message
            return ResponseEntity.internalServerError().body("Error retrieving secondary email: " + e.getMessage());
        }
    }

	@Override
	public ResponseEntity<?> saveQueries(HelpAndSupportWebModel helpAndSupportWebModel) {
		  try {
	            HelpAndSupport dbData = new HelpAndSupport();
	            dbData.setUserId(userDetails.userInfo().getId());
	            dbData.setHelpAndSupportIsActive(true);
	            dbData.setHelpAndSupportCreatedBy(userDetails.userInfo().getId());
	            dbData.setMessage(helpAndSupportWebModel.getMessage());
	            dbData.setSubject(helpAndSupportWebModel.getSubject());
	            dbData.setReceipentEmail(helpAndSupportWebModel.getReceipentEmail());

	            helpAndSupportRepository.save(dbData);

	            // Send email
	            boolean emailSent = mailNotification.sendFilmHookQueries(dbData);
	            if (emailSent) {
	                return ResponseEntity.ok("Query saved and email sent successfully.");
	            } else {
	                return ResponseEntity.status(500).body("Query saved, but failed to send email.");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(500).body("An error occurred while saving the query.");
	        }
	    }

	@Override
	public Response updateUserFlag(UserWebModel userWebModel) {
		 Optional<User> userData = userRepository.findById(userWebModel.getUserId());
		    
		    if (userData.isPresent()) {
		        User user = userData.get();
		        user.setUserFlag(userWebModel.getUserFlag());
		        userRepository.save(user);
		        return new Response(1,"Success", "user flag updated successfully"); // Success response
		    } else {
		        return new Response(0,"fail", "User not found"); // Failure response
		    }
	}
	@Override
	public ResponseEntity<?> updateSecondaryMobileNumber(UserWebModel userWebModel) {
	    try {
	        Optional<User> userData = userRepository.findById(userWebModel.getUserId());
	        if (userData.isPresent()) {
	            User user = userData.get();
	            user.setPhoneNumber(userWebModel.getPhoneNumber());
	            user.setCountryCode(userWebModel.getCountryCode());
	            userRepository.save(user);
	            return ResponseEntity.ok(new Response(1, "Success", "User phone number updated successfully"));
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(0, "Fail", "User not found"));
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(0, "Fail", "An error occurred"));
	    }
	}



}
