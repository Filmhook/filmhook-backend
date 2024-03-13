package com.annular.filmHook.webModel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWebModel {

	private Integer userId;

	private String name;

	private String dob;

	private String gender;

	private String country;

	private String state;

	@Column
	private String district;

	private String password;

	private String phoneNumber;

	private String token;

	private boolean userIsActive;

	private Date userCreatedOn;

	private Integer userUpdatedBy;

	private String userType;

	private Date userUpdateOn;

	private String email;

	private Integer usercreatedBy;

	private String verificationCode;
}
