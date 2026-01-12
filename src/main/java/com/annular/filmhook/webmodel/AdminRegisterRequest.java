package com.annular.filmhook.webmodel;

import lombok.*;

@Data
@NoArgsConstructor
public class AdminRegisterRequest {
	private String email;
	private String password;
	private Integer roleId;
	private String fullName;
	private String phoneNumber;
	private String jobTitle;
	private String organizationUnit;
}
