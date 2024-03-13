package com.annular.filmHook.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;

	@Column
	private String name;
	
	@Column
	@JsonIgnore
	private Integer verificationCode;

	@Column
	private String dob;

	@Column
	private String gender;

	@Column
	private String country;

	@Column
	private String state;
	
	@Column
	private String email;

	@Column
	private String district;

	@Column
	@JsonIgnore
	private String password;

	@Column
	private String phoneNumber;

	@Column
	private boolean userIsActive;

	@Column
	@CreationTimestamp
	private Date userCreatedOn;

	@Column
	private Integer userUpdatedBy;

	@Column
	private String userType;

	@Column
	@CreationTimestamp
	private Date userUpdateOn;

	@Column
	private Integer usercreatedBy;
	
	@Column
	private String resetPassword;

	
}
