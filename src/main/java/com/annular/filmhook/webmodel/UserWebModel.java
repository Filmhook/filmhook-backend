package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Data;

@Data
public class UserWebModel {

	private Integer userId;
	private String password;
	private String verificationCode;

	private String email;
	private String userType;

	private String name;
	private String dob;
	private String gender;
	private String country;
	private String state;
	private String district;
	private String phoneNumber;
	private String token;
	private boolean status;

	private Integer createdBy;
	private Date createdOn;
	private Integer updatedBy;
	private Date updateOn;
}
