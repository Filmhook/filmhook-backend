package com.annular.filmhook;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusConfig {
	
	private boolean isAdmin;

	private boolean isDriver;
	
	private String userType;


}

