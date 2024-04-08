package com.annular.filmhook.configuration;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UserStatusConfig {
	
	private String userType;
}

