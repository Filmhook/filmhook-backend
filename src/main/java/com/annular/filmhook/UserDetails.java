package com.annular.filmhook;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.annular.filmhook.security.UserDetailsImpl;

@Component
public class UserDetails {

	public UserDetailsImpl userInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		return userDetails;
	}
}
