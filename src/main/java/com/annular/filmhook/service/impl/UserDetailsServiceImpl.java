package com.annular.filmhook.service.impl;

import java.util.Optional;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.annular.filmhook.configuration.UserStatusConfig;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.security.UserDetailsImpl;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	public static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	private static final String CARET = "^";

	@Autowired
	UserRepository userRepo;

//	JwtUtils jwtUtils;

	@Autowired
	UserStatusConfig loginConstants;

//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		logger.info("I am from loadUserByUsername() !!! ");
//		logger.info("UserName :- " + username);
//		logger.info("UserType from LoginConstants :- " + loginConstants.getUserType());
//		logger.info("login check --- > admin : "+loginConstants.isAdmin()+"  Driver :"+loginConstants.isDriver());
//		String userName, userType;
//		if (username != null && username.contains(CARET)) {
//			userName = username.substring(0, username.indexOf(CARET));
//			userType = username.substring(username.indexOf(CARET) + 1);
//			logger.info("userName : " + userName + "  userType: " + userType);
//		} else {
//			userName = username;
//			userType = loginConstants.getUserType();
//		}
//		User user = userRepo.findByUserName(userName, userType)
//				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with emailId: " + username));
//		logger.info("User from DB --> " + user.getUserId() + user.getEmail() + user.getUserType());
//		return UserDetailsImpl.build(user);
//	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		logger.info("I am from loadUserByUsername() !!! ");
		logger.info("Email :- " + email);
		logger.info("UserType from LoginConstants :- " + loginConstants.getUserType());
		email = email.contains("^") ? email.split("\\^")[0] : email;
		Optional<User> optionalUser = userRepo.findByEmailAndUserType(email);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			logger.info("User from DB --> " + user.getUserId() + " -- " + user.getEmail() + " -- " + user.getUserType());
			return UserDetailsImpl.build(user);
		} else {
			throw new UsernameNotFoundException("User Not Found with email: " + email);
		}
	}

}
